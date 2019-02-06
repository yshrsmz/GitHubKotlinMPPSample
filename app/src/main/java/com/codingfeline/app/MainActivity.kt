package com.codingfeline.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.getGitHubRepository
import com.gojuno.koptional.toOptional
import com.squareup.sqldelight.runtime.rx.asObservable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

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


        GlobalScope.launch {
            repository.fetchViewer()
        }
    }
}
