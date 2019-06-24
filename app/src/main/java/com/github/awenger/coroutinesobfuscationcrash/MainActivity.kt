package com.github.awenger.coroutinesobfuscationcrash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.io.IOException

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
            val result1 = async { retrieve() }
            val result2 = async { retrieve() }
            listOf(result1, result2).awaitAll()
            Log.d(LOG_TAG, "all successful retrieved")
            true
        }
    } catch (ex: IOException) {
        Log.e(LOG_TAG, "caught ${ex.message}")
        false
    }

    @Throws(IOException::class)
    private suspend fun retrieve() {
        delay(100)
        Log.d(LOG_TAG, "waited for 100ms, throwing IOException")
        throw IOException("could not contact server")
    }

    companion object {
        const val LOG_TAG = "Main"
    }
}
