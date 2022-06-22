package com.example.backgroundservicesample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.measurement_service.MeasurementService
import com.example.measurement_service.MeasurementService.Companion.SERVICE_COMMAND
import com.example.measurement_service.MeasurementState

class MainActivity : AppCompatActivity() {

    private var serviceHasStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            (it as Button).text = if (!serviceHasStarted) {
                startMeasurementService()
                "Stop Service"
            } else {
                stopMeasurementService()
                "Start Service"
            }
        }
    }

    private fun startMeasurementService() {
        sendCommandToForegroundService(MeasurementState.START)
        serviceHasStarted = true
    }

    private fun stopMeasurementService() {
        sendCommandToForegroundService(MeasurementState.STOP)
        serviceHasStarted = false
    }

    private fun sendCommandToForegroundService(command: MeasurementState) {
        ContextCompat.startForegroundService(this, getServiceIntent(command))
    }

    private fun getServiceIntent(command: MeasurementState) =
        Intent(this, MeasurementService::class.java).apply {
            putExtra(SERVICE_COMMAND, command)
        }
}