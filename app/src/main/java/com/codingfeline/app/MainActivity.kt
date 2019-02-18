package com.codingfeline.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingfeline.github.initKodein
import com.codingfeline.github.presentation.ViewerViewModel
import com.codingfeline.github.presentation.getViewerKodein
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.erased.instance
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainActivity : AppCompatActivity(), CoroutineScope, KodeinAware {

    private val job = SupervisorJob()

    override val kodeinTrigger = KodeinTrigger()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override val kodein: Kodein by lazy {
        getViewerKodein(initKodein(applicationContext))
    }

    val viewModel: ViewerViewModel by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kodeinTrigger.trigger()
        setContentView(R.layout.activity_main)

        viewModel.init()

        launch {
            viewModel.states.consumeEach {
                println("state: $it")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
