package com.codingfeline.github.presentation

import com.codingfeline.github.Tags
import com.codingfeline.github.data.GitHubRepositoryIos
import com.codingfeline.github.domain.FetchViewer
import com.codingfeline.github.domain.ObserveViewer
import com.codingfeline.github.domain.ObserveViewerRepositories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun getViewerKodein(dataKodein: Kodein): Kodein {
    return Kodein {
        extend(dataKodein)

        bind<FetchViewer>() with provider { FetchViewer(instance()) }
        bind<ObserveViewer>() with provider { ObserveViewer(instance()) }
        bind<ObserveViewerRepositories>() with provider { ObserveViewerRepositories(instance()) }
        bind<MainViewModelStateNotifier>() with provider { MainViewModelStateNotifier(instance(Tags.UI_CONTEXT)) }
        bind<GitHubRepositoryIos>() with provider {
            GitHubRepositoryIos(
                instance(),
                instance(),
                instance(),
                instance(Tags.BG_CONTEXT)
            )
        }

        bind<MainViewModel>() with provider {
            MainViewModel(
                instance(Tags.BG_CONTEXT),
                instance(),
                instance(),
                instance()
            )
        }
    }
}


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun getViewerViewModelStateNotifier(viewerKodein: Kodein): MainViewModelStateNotifier {
    return viewerKodein.direct.instance()
}

fun getMainViewModel(viewerKodein: Kodein): MainViewModel {
    return viewerKodein.direct.instance()
}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainViewModelStateNotifier(
    uiContext: CoroutineContext
) : CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = job + uiContext

    fun stateChanged(viewModel: MainViewModel, stateChanged: (state: MainState) -> Unit) {
        launch {
            viewModel.states.consumeEach {
                stateChanged(it)
            }
        }

    }

    fun effectReceived(viewModel: MainViewModel, effectReceived: (effect: MainEffect) -> Unit) {
        launch {
            viewModel.effects.consumeEach {
                effectReceived(it)
            }
        }
    }

    fun dispose() {
        job.cancel()
    }
}
