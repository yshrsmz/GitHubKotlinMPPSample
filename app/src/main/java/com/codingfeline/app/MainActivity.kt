package com.codingfeline.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.getGitHubRepository
import com.gojuno.koptional.toOptional
import com.squareup.sqldelight.runtime.rx.asObservable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    val repository: GitHubRepository by lazy { getGitHubRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
