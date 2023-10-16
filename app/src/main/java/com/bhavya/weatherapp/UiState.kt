package com.bhavya.weatherapp

sealed interface UiState<out T> {
    /**
     * UI state of success scenaio, where T need to be defined.
     */
    data class Success<T>(val data: T) : UiState<T>


    /**
     * UI state of failure scenaio.
     */
    data class Error(val message: String) : UiState<Nothing>

    /**
     * UI state of initial state
     */
    object Loading : UiState<Nothing>
}