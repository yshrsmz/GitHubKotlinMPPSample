package com.codingfeline.github.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal

interface Intent
interface Action
interface Result
interface State
interface Effect
interface UseCaseUpdate

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
abstract class MviViewModel<E : Intent, S : State>(
    initialState: S,
    private val usecaseContext: CoroutineContext,
    private val defaultStateConstructor: (S) -> S
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = usecaseContext + SupervisorJob()

    private var _state: S = initialState
    val state get() = _state
    val stateAsDefault get() = defaultStateConstructor(_state)

    private val _events = Channel<E>(Channel.UNLIMITED)
    val events get() = _events

    private val useCases = Channel<ReceiveChannel<UseCaseUpdate>>(Channel.UNLIMITED)

    private val _states = BroadcastChannel<S>(1)
    val states: ReceiveChannel<S>
        get() = _states.openSubscription().also { _states.offer(stateAsDefault) }

    init {
        launch {
            _events.consumeEach {
                useCases.send(handleEvent(it))
            }
        }

        launch {
            useCases.consumeEach { updates ->
                updates.consumeEach {
                    _state = updateState(it)
                    _states.send(_state)
                }
            }
        }
    }

    protected abstract fun handleEvent(event: E): ReceiveChannel<UseCaseUpdate>

    protected abstract fun updateState(update: UseCaseUpdate): S

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
abstract class MviViewModel2<R : Result, S : State, E : Effect>(
    initialState: () -> S,
    @ThreadLocal val bgContext: CoroutineContext
) : ViewModel(), CoroutineScope {

    @ThreadLocal
    private val job = SupervisorJob()

    @ThreadLocal
    override val coroutineContext: CoroutineContext = bgContext + job

    private var _state: S = initialState()
    // get current state
    val state get() = _state

    private val _states = BroadcastChannel<S>(1)
    val states get() = _states.openSubscription().also { _states.offer(_state) }

    private val _effects = BroadcastChannel<E>(1)
    val effects: ReceiveChannel<E> get() = _effects.openSubscription()

    private val results = Channel<R>(Channel.UNLIMITED)

    init {
        launch {
            results.consumeEach { result ->
                val newState = reduce(result, _state)
                _state = newState
                _states.send(newState)
            }
        }
    }

    protected suspend fun sendResult(result: R) {
        results.send(result)
    }

    protected suspend fun sendEffect(effects: E) {
        _effects.send(effects)
    }

    protected abstract fun reduce(result: R, previousState: S): S

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
