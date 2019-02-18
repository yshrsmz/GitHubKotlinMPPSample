package com.codingfeline.github.presesntation

import com.codingfeline.github.data.Tags
import com.codingfeline.github.domain.FetchViewer
import com.codingfeline.github.domain.ObserveViewer
import com.codingfeline.github.domain.ObserveViewerRepositories
import com.codingfeline.github.presentation.ViewerViewModel
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
