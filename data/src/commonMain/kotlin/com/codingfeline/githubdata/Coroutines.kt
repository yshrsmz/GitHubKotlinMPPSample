package com.codingfeline.githubdata

import kotlinx.coroutines.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher

expect fun checkIfFrozen(name: String, instance: Any?)

expect fun printCurrentThread()
