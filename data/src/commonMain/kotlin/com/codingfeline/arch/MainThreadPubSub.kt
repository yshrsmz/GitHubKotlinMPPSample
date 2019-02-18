package com.codingfeline.arch

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.freeze
import com.codingfeline.github.platform.backToFront

class MainThreadPubSub<T> : BasePub<T>(), Sub<T> {

    private val subSetLocal = ThreadLocalRef<MutableCollection<Sub<T>>>()

    init {
        subSetLocal.set(mutableSetOf())
    }

    override fun subs(): MutableCollection<Sub<T>> = subSetLocal.get()!!

    override fun onNext(next: T) {
        backToFront({ next.freeze() }) { applyNextValue(it) }
    }

    override fun onError(t: Throwable) {
        backToFront({ t.freeze() }) { applyError(it) }
    }
}
