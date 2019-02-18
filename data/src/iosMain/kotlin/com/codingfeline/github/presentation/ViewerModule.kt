package com.codingfeline.githubui

import com.codingfeline.githubdata.GitHubRepositoryIos
import com.codingfeline.githubdata.Tags
import com.codingfeline.githubdomain.FetchViewer
import com.codingfeline.githubdomain.ObserveViewer
import com.codingfeline.githubdomain.ObserveViewerRepositories
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
        bind<ViewerViewModel>() with provider {
            ViewerViewModel(
                instance(Tags.BG_CONTEXT),
                instance(),
                instance(),
                instance()
            )
        }
        bind<ViewerViewModelStateNotifier>() with provider { ViewerViewModelStateNotifier(instance(Tags.UI_CONTEXT)) }
        bind<GitHubRepositoryIos>() with provider {
            GitHubRepositoryIos(
                instance(),
                instance(),
                instance(),
                instance(Tags.BG_CONTEXT)
            )
        }
    }
}


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun getViewerViewModel(viewerKodein: Kodein): ViewerViewModel {
    return viewerKodein.direct.instance()
}

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
fun getViewerViewModelStateNotifier(viewerKodein: Kodein): ViewerViewModelStateNotifier {
    return viewerKodein.direct.instance()
}


@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class ViewerViewModelStateNotifier(
    uiContext: CoroutineContext
) : CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = job + uiContext

    fun stateChanged(viewModel: ViewerViewModel, callback: (state: ViewerState) -> Unit) {
        launch {
            viewModel.states.consumeEach {
                callback(it)
            }
        }
    }

    fun dispose() {
        job.cancel()
    }
}
