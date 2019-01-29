package com.codingfeline.githubdata

import kotlinx.coroutines.CoroutineDispatcher

internal actual val ApplicationDispatcher: CoroutineDispatcher = MainLoopDispatcher
