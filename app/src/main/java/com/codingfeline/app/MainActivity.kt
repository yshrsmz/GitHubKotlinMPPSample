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

    val login = "yshrsmz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository.observeUser(login).asObservable()
            .map { it.executeAsOneOrNull().toOptional() }
            .subscribe { println("user: ${it.toNullable()}") }


        GlobalScope.launch {
            repository.fetchUser(login)
        }
    }
}
