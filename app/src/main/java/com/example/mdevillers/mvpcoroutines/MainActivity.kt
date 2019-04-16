package com.example.mdevillers.mvpcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.*
import com.example.mdevillers.mvpcoroutines.framework.RetainedStateRepository
import com.example.mdevillers.mvpcoroutines.model.ArticleRepository
import com.example.mdevillers.mvpcoroutines.model.ArticleThumbnailRepository
import com.example.mdevillers.mvpcoroutines.mvp.Presenter
import com.example.mdevillers.mvpcoroutines.mvp.ViewProxy
import kotlin.coroutines.CoroutineContext

class MainActivity(
    private val scope: CoroutineScope = MainScope()
) : AppCompatActivity(), CoroutineScope by scope {

    private lateinit var stateRepository: RetainedStateRepository

    override val coroutineContext: CoroutineContext
        get() = scope.coroutineContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stateRepository = RetainedStateRepository(
            savedInstanceState?.getBundle(STATE_PRESENTER) ?: Bundle(),
            ViewModelProviders.of(this)
        )

        val viewProxy = ViewProxy(window.decorView)
        Presenter(
            viewProxy,
            scope,
            stateRepository,
            ArticleRepository(Singleton.callFactory),
            ArticleThumbnailRepository(Singleton.callFactory)
        ).also {
            viewProxy.presenter = it
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(STATE_PRESENTER, stateRepository.savedInstanceState())
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val STATE_PRESENTER = "PRESENTER"
    }
}


