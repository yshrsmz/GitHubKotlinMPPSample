package com.codingfeline.githubui

import com.codingfeline.githubdata.Tags
import com.codingfeline.githubdomain.FetchViewer
import com.codingfeline.githubdomain.ObserveViewer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun getViewerKodein(dataKodein: Kodein): Kodein {
    return Kodein {
        extend(dataKodein)

        bind<FetchViewer>() with provider { FetchViewer(instance()) }
        bind<ObserveViewer>() with provider { ObserveViewer(instance()) }
        bind<ViewerViewModel2>() with provider { ViewerViewModel2(instance(Tags.BG_CONTEXT), instance(), instance()) }
    }
}


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun getViewerViewModel(viewerKodein: Kodein): ViewerViewModel2 {
    return viewerKodein.direct.instance()
}
