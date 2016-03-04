package com.purrprogramming.purrprogrammingshirt

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.content.ContextCompat
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.BoxInsetLayout
import android.util.Log
import android.view.View
import android.widget.TextView

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : WearableActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mContainerView: BoxInsetLayout? = null
    private var mTextView: TextView? = null
    private var mClockView: TextView? = null
    private var currentColor = R.color.ambient
    private var mGoogleApiClient: GoogleApiClient? = null
    private var messageNodeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()

        mContainerView = findViewById(R.id.container) as BoxInsetLayout
        mTextView = findViewById(R.id.text) as TextView
        mClockView = findViewById(R.id.clock) as TextView

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE)

        mGoogleApiClient = GoogleApiClient.Builder(this).addApi(Wearable.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()
        mGoogleApiClient!!.connect()
        StartWearableActivityTask().execute()
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        updateDisplay()
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        updateDisplay()
    }

    override fun onExitAmbient() {
        updateDisplay()
        super.onExitAmbient()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results[0]
            Log.i("Speech ", spokenText)
            var command = ""

            when (spokenText) {
                "rubyfuza" -> {
                    currentColor = R.color.ruby
                    mTextView!!.setTextColor(ContextCompat.getColor(applicationContext, currentColor))
                    mTextView!!.setText(R.string.ruby)
                    command = "ruby"
                    Log.i("detected", getString(R.string.ruby))
                }
                "South Africa" -> {
                    currentColor = R.color.south_africa
                    mTextView!!.setTextColor(ContextCompat.getColor(applicationContext, currentColor))
                    mTextView!!.setText(R.string.south_africa)
                    command = "south_africa"
                    Log.i("detected", getString(R.string.south_africa))
                }
                "Alley Cat" -> {
                    currentColor = R.color.purr_programming
                    mTextView!!.setTextColor(ContextCompat.getColor(applicationContext, currentColor))
                    mTextView!!.setText(R.string.purr_programming)
                    command = "purr"
                    Log.i("detected", getString(R.string.purr_programming))
                }
                else -> {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    // Start the activity, the intent will be populated with the speech text
                    startActivityForResult(intent, SPEECH_REQUEST_CODE)
                }
            }
            if (!command.equals("")) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, messageNodeId,
                        command, null).setResultCallback { sendMessageResult -> Log.i("Message Sent", sendMessageResult.status.isSuccess.toString()) }
            }
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateDisplay() {
        if (isAmbient) {
            mContainerView!!.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.black))
            mTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.ambient))
            mClockView!!.visibility = View.VISIBLE

            mClockView!!.text = AMBIENT_DATE_FORMAT.format(Date())
        } else {
            mContainerView!!.background = null
            mTextView!!.setTextColor(ContextCompat.getColor(applicationContext, currentColor))
            mClockView!!.visibility = View.GONE
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.i("on connected", "here")
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i("On conneced", "failed")
    }

    private inner class StartWearableActivityTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg args: Void): Void? {
            getNode()
            return null
        }
    }

    private fun getNode() {
        val nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await()

        if (nodes.nodes.size > 0) {
            messageNodeId = nodes.nodes[0].id
        }
    }

    companion object {

        private val AMBIENT_DATE_FORMAT = SimpleDateFormat("HH:mm", Locale.US)
        private val SPEECH_REQUEST_CODE = 0
    }
}
