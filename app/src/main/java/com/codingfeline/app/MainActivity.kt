package com.codingfeline.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.initKodein
import com.gojuno.koptional.toOptional
import com.squareup.sqldelight.runtime.rx.asObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.erased.instance
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope, KodeinAware {

    private val job = SupervisorJob()

    override val kodeinTrigger = KodeinTrigger()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    override val kodein: Kodein by lazy {
        initKodein(applicationContext)
    }

    val repository: GitHubRepository by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kodeinTrigger.trigger()
        setContentView(R.layout.activity_main)

        repository.observeViewer().asObservable()
            .map { it.executeAsOneOrNull().toOptional() }
            .subscribe { println("user: ${it.toNullable()}") }

        repository.observeAllViewer().asObservable()
            .map { it.executeAsList() }
            .subscribe { println("viewer:$it") }


        launch {
            repository.fetchViewer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
