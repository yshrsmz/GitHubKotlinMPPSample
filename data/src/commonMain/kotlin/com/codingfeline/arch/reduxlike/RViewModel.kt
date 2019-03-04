package com.codingfeline.arch.reduxlike

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.ensureNeverFrozen
import co.touchlab.stately.freeze
import com.codingfeline.github.platform.backToFront
import com.codingfeline.github.presentation.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

// https://github.com/itome/owl

interface Action
interface Intent
interface Effect
interface State

abstract class Processor<A : Action>(
    context: CoroutineContext
) : CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = context + job

    private var postActionCallback = ThreadLocalRef<MutableCollection<(action: A) -> Unit>>()

    init {
        postActionCallback.set(mutableSetOf())
        ensureNeverFrozen()
    }

    abstract fun processAction(action: A)

    protected fun put(action: A) {
        println("put: ${action::class}")
        val holder = Holder(value = postActionCallback)
        backToFront({ action.freeze() }) { holder.value.get()!!.forEach { it(action) } }
    }

    fun setPostActionCallback(callback: (action: A) -> Unit) {
        this.postActionCallback.get()!!.add(callback)
    }

    open fun onCleared() {
        cancel()
        postActionCallback.remove()
    }

    data class Holder<A : Action>(val value: ThreadLocalRef<MutableCollection<(action: A) -> Unit>>)
}


@ExperimentalCoroutinesApi
abstract class RViewModel<I : Intent, A : Action, S : State, E : Effect>(
    initialState: S,
    bgContext: CoroutineContext,
    private val processor: Processor<A> = NothingProcessor(bgContext)
) : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = bgContext + job

    abstract fun intentToAction(intent: I, state: S): A

    abstract fun reduce(previousState: S, action: A): S

    private var _state: S = initialState
    val state get() = _state

    private val _states = BroadcastChannel<S>(1)
    val states: ReceiveChannel<S>
        get() = _states.openSubscription().also { _states.offer(_state) }

    private val _effects = BroadcastChannel<E>(1)
    val effects: ReceiveChannel<E>
        get() = _effects.openSubscription()

    fun dispatch(intent: I) {
        val state = _state
        val action = intentToAction(intent, state)
        setState(action)
        processor.processAction(action)
    }

    private fun setState(action: A) {
        launch {
            val nextState = reduce(_state, action)
            if (_state != nextState) {
                _state = nextState
                sendState(nextState)
            }
        }
    }

    protected suspend fun sendState(state: S) {
        _states.send(state)
    }

    protected suspend fun sendEffect(effect: E) {
        _effects.send(effect)
    }

    init {
        processor.setPostActionCallback(::setState)
    }

    override fun onCleared() {
        super.onCleared()
        cancel()
        processor.onCleared()
    }


    class NothingProcessor<A : Action>(bgContext: CoroutineContext) : Processor<A>(bgContext) {
        override fun processAction(action: A) {
            // no-op
        }
    }
}
