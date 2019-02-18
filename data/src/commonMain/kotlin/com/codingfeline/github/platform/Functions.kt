package com.codingfeline.github.platform

expect fun checkIfFrozen(name: String, instance: Any?)

expect fun printCurrentThread()

internal expect fun <B> backToFront(b: () -> B, job: (B) -> Unit)
