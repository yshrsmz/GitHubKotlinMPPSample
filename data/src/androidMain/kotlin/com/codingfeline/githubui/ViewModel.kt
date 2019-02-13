package com.codingfeline.githubui

/**
 * Just to suppress incompatible visibility error on `fun onCleared()`
 */
abstract class ViewModel2 : androidx.lifecycle.ViewModel() {
    public override fun onCleared() {
        super.onCleared()
    }
}

actual typealias ViewModel = ViewModel2
