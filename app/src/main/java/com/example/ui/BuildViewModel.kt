package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CreationGenerator
import com.example.data.GeminiBuildService
import com.example.data.db.AppDatabase
import com.example.data.db.BuildRepository
import com.example.model.BuildCategory
import com.example.model.ComplexityTier
import com.example.model.LegoCreation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "BuildViewModel"

enum class ComplexityLevelOption(
  val label: String,
  val pieceRange: String,
  val defaultPieces: Int,
  val minPieces: Int,
  val maxPieces: Int,
  val description: String
) {
  MICRO("Micro", "10–50 pcs", 35, 10, 50, "Quick micro-scale creations with clever small-part geometry."),
  SIMPLE("Simple", "50–150 pcs", 100, 50, 150, "Balanced, classic models with movable elements."),
  MODERATE("Moderate", "150–350 pcs", 220, 150, 350, "Detailed builds featuring SNOT brackets and mechanisms."),
  COMPLEX("Complex", "350–700 pcs", 480, 350, 700, "Advanced modular constructions and articulated frames."),
  EXTREME("Extreme", "700–1100 pcs", 950, 700, 1100, "Apex titan creations reaching up to the 1100-piece maximum.")
}

sealed interface AiForgeUiState {
  data object Idle : AiForgeUiState
  data class Loading(val message: String = "Consulting Gemini Master Builder AI...") : AiForgeUiState
  data class Success(val creation: LegoCreation) : AiForgeUiState
  data class Error(val message: String) : AiForgeUiState
}

class BuildViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BuildRepository

  private val _selectedComplexity = MutableStateFlow(ComplexityLevelOption.SIMPLE)
  val selectedComplexity: StateFlow<ComplexityLevelOption> = _selectedComplexity.asStateFlow()

  private val _pieceBudget = MutableStateFlow(100)
  val pieceBudget: StateFlow<Int> = _pieceBudget.asStateFlow()

  private val _selectedCategory = MutableStateFlow(BuildCategory.ALL)
  val selectedCategory: StateFlow<BuildCategory> = _selectedCategory.asStateFlow()

  private val _customPrompt = MutableStateFlow("")
  val customPrompt: StateFlow<String> = _customPrompt.asStateFlow()

  private val _currentCreation = MutableStateFlow<LegoCreation>(
    CreationGenerator.generate(targetPieces = 100, categoryFilter = BuildCategory.ALL, combinationSeed = 42)
  )
  val currentCreation: StateFlow<LegoCreation> = _currentCreation.asStateFlow()

  private val _aiForgeState = MutableStateFlow<AiForgeUiState>(AiForgeUiState.Idle)
  val aiForgeState: StateFlow<AiForgeUiState> = _aiForgeState.asStateFlow()

  private val _isRolling = MutableStateFlow(false)
  val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

  private val _checkedPartIds = MutableStateFlow<Set<String>>(emptySet())
  val checkedPartIds: StateFlow<Set<String>> = _checkedPartIds.asStateFlow()

  private val _completedStepNumbers = MutableStateFlow<Set<Int>>(emptySet())
  val completedStepNumbers: StateFlow<Set<Int>> = _completedStepNumbers.asStateFlow()

  val savedCreations: StateFlow<List<LegoCreation>>

  init {
    val db = AppDatabase.getDatabase(application)
    repository = BuildRepository(db.savedBuildDao())
    savedCreations = repository.allSavedCreations.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    viewModelScope.launch {
      val isSaved = repository.isSaved(_currentCreation.value.id)
      if (isSaved) {
        _currentCreation.value = _currentCreation.value.copy(isSaved = true)
      }
    }
  }

  fun setComplexity(level: ComplexityLevelOption) {
    _selectedComplexity.value = level
    _pieceBudget.value = level.defaultPieces
  }

  fun setPieceBudget(pieces: Int) {
    _pieceBudget.value = pieces.coerceIn(10, 1100)
  }

  fun setCategory(category: BuildCategory) {
    _selectedCategory.value = category
  }

  fun setCustomPrompt(prompt: String) {
    _customPrompt.value = prompt
  }

  /**
   * Forges creation using Gemini AI via Firebase AI Logic.
   */
  fun forgeCreationWithGemini() {
    viewModelScope.launch {
      _aiForgeState.value = AiForgeUiState.Loading("Forging blueprint with Gemini AI...")
      try {
        val complexity = _selectedComplexity.value
        val creation = GeminiBuildService.forgeCreationWithAi(
          complexityLevel = complexity.label,
          targetPieces = _pieceBudget.value,
          category = _selectedCategory.value,
          themePrompt = _customPrompt.value
        )
        val isSaved = repository.isSaved(creation.id)
        val finalCreation = creation.copy(isSaved = isSaved)

        _currentCreation.value = finalCreation
        _checkedPartIds.value = emptySet()
        _completedStepNumbers.value = emptySet()
        _aiForgeState.value = AiForgeUiState.Success(finalCreation)
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "Gemini creation generation timed out", e)
        _aiForgeState.value = AiForgeUiState.Error("Gemini AI timed out after 60 seconds. Please try again.")
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e(TAG, "Gemini creation failed", e)
        _aiForgeState.value = AiForgeUiState.Error(
          e.localizedMessage ?: "Failed to forge creation with Gemini AI."
        )
      }
    }
  }

  fun resetAiForgeState() {
    _aiForgeState.value = AiForgeUiState.Idle
  }

  /**
   * Fast offline procedural roll fallback.
   */
  fun rollNewCreation() {
    viewModelScope.launch {
      _isRolling.value = true
      delay(250)
      val newCreation = CreationGenerator.generate(
        targetPieces = _pieceBudget.value,
        categoryFilter = _selectedCategory.value
      )
      val isSaved = repository.isSaved(newCreation.id)
      _currentCreation.value = newCreation.copy(isSaved = isSaved)
      _checkedPartIds.value = emptySet()
      _completedStepNumbers.value = emptySet()
      _isRolling.value = false
      _aiForgeState.value = AiForgeUiState.Idle
    }
  }

  fun loadCreation(creation: LegoCreation) {
    _currentCreation.value = creation
    _pieceBudget.value = creation.pieceCount
    _checkedPartIds.value = emptySet()
    _completedStepNumbers.value = emptySet()
    _aiForgeState.value = AiForgeUiState.Idle
  }

  fun loadCombinationByNumber(index: Int) {
    val creation = CreationGenerator.generate(
      targetPieces = null,
      categoryFilter = BuildCategory.ALL,
      combinationSeed = index.coerceIn(1, 1100)
    )
    loadCreation(creation)
  }

  fun togglePartChecked(partId: String) {
    val current = _checkedPartIds.value.toMutableSet()
    if (current.contains(partId)) {
      current.remove(partId)
    } else {
      current.add(partId)
    }
    _checkedPartIds.value = current
  }

  fun checkAllParts() {
    val allIds = _currentCreation.value.parts.map { it.id }.toSet()
    _checkedPartIds.value = allIds
  }

  fun clearPartChecks() {
    _checkedPartIds.value = emptySet()
  }

  fun toggleStepCompleted(stepNumber: Int) {
    val current = _completedStepNumbers.value.toMutableSet()
    if (current.contains(stepNumber)) {
      current.remove(stepNumber)
    } else {
      current.add(stepNumber)
    }
    _completedStepNumbers.value = current
  }

  fun toggleSaveCurrentCreation() {
    viewModelScope.launch {
      val current = _currentCreation.value
      if (current.isSaved) {
        repository.removeCreation(current.id)
        _currentCreation.value = current.copy(isSaved = false)
      } else {
        val updated = current.copy(isSaved = true)
        repository.saveCreation(updated)
        _currentCreation.value = updated
      }
    }
  }

  fun updateSavedCreation(creation: LegoCreation) {
    viewModelScope.launch {
      repository.updateCreation(creation)
      if (_currentCreation.value.id == creation.id) {
        _currentCreation.value = creation
      }
    }
  }

  fun deleteSavedCreation(creationId: String) {
    viewModelScope.launch {
      repository.removeCreation(creationId)
      if (_currentCreation.value.id == creationId) {
        _currentCreation.value = _currentCreation.value.copy(isSaved = false)
      }
    }
  }
}
