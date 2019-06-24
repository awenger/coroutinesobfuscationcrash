package com.github.awenger.coroutinesobfuscationcrash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.io.IOException
import kotlin.coroutines.resumeWithException

class MainActivity : AppCompatActivity() {

    private val actor = setupActor()
    private var jobId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { triggerNextJob() }
    }

    private fun triggerNextJob() {
        runBlocking { actor.send(jobId++) }
    }

    private fun setupActor(): SendChannel<Int> {
        return GlobalScope.actor {
            for (msg in channel) {
                val successful = call()
                Log.d(LOG_TAG, "success: $successful")
            }
        }
    }

    private suspend fun call() = try {
        coroutineScope {
            val result1 = async(start = CoroutineStart.LAZY) { retrieve("A1", false) }
            val result2 = async(start = CoroutineStart.LAZY) { retrieve("A2", true) }
            listOf(result1, result2).awaitAll()
            Log.d(LOG_TAG, "all successful retrieved")
            true
        }
    } catch (ex: IOException) {
        Log.e(LOG_TAG, "caught ${ex.message}")
        false
    }

    private suspend fun retrieve(tag: String, fail: Boolean): String {
        return suspendCancellableCoroutine {
            it.invokeOnCancellation { Log.d(tag, "invoke on cancellation") }
            if (fail) {
                Thread.sleep(10)
                Log.d(tag, "failing with IOException")
                it.resumeWithException(IOException("failed"))
            } else {
                Thread.sleep(10)
                it.resume("abc") { Log.d(tag, "cancel during on resume with $it") }
            }
        }
    }

    companion object {
        const val LOG_TAG = "Main"
    }
}
