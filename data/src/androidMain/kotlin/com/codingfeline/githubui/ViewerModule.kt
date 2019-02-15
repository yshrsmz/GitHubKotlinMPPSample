package com.codingfeline.githubui

import com.codingfeline.githubdata.Tags
import com.codingfeline.githubdomain.FetchViewer
import com.codingfeline.githubdomain.ObserveViewer
import com.codingfeline.githubdomain.ObserveViewerRepositories
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider

fun getViewerKodein(dataKodein: Kodein): Kodein {
    return Kodein {
        extend(dataKodein)

        bind<FetchViewer>() with provider { FetchViewer(instance()) }
        bind<ObserveViewer>() with provider { ObserveViewer(instance()) }
        bind<ObserveViewerRepositories>() with provider { ObserveViewerRepositories(instance()) }
        bind<ViewerViewModel>() with provider {
            ViewerViewModel(
                instance(Tags.BG_CONTEXT),
                instance(),
                instance(),
                instance()
            )
        }
    }
}
