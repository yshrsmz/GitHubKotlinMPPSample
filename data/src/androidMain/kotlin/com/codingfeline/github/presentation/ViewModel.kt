package com.codingfeline.github.presentation

/**
 * Just to suppress incompatible visibility error on `ViewModel#onCleared()`
 */
abstract class ViewModel2 : androidx.lifecycle.ViewModel() {
    public override fun onCleared() {
        super.onCleared()
    }
}

actual typealias ViewModel = ViewModel2
