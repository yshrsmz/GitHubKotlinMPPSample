package com.codingfeline.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.codingfeline.githubdata.getGitHubRepository
import com.squareup.sqldelight.runtime.rx.asObservable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val repository = getGitHubRepository(applicationContext)

    val login = "yshrsmz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository.observeUser(login).asObservable()
            .map { it.executeAsOneOrNull() }
            .subscribe { println("user: $it") }


        GlobalScope.launch {
            repository.fetchUser(login)
        }
    }
}
