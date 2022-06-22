package com.example.measurement_service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.measurement_service.helpers.NotificationHelper
import com.example.measurement_service.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class MeasurementService : Service(), CoroutineScope {

    var serviceState = MeasurementState.INITIALIZED
    private val helper by lazy { NotificationHelper(this) }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val binder by lazy { MeasurementBinder() }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.extras?.run {
            when (getSerializable(SERVICE_COMMAND) as MeasurementState) {
                MeasurementState.START -> {
                    Log.d(TAG, "onStartCommand: start service")
                    startMeasurementTracking()
                }
                MeasurementState.PAUSE -> {
                    Log.d(TAG, "onStartCommand: pause service")
                    pauseMeasurementTracking()
                }
                MeasurementState.STOP -> {
                    Log.d(TAG, "onStartCommand: stop service")
                    stopMeasurementTracking()
                }
                else -> return START_NOT_STICKY
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun startMeasurementTracking() {
        serviceState = MeasurementState.START

        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
        Log.d(TAG, "startMeasurementTracking: the service has started.")
    }

    private fun pauseMeasurementTracking() {
        serviceState = MeasurementState.PAUSE
        Log.d(TAG, "pauseMeasurementTracking: the service has paused.")
    }

    private fun stopMeasurementTracking() {
        serviceState = MeasurementState.STOP
        stopService()
        Log.d(TAG, "stopMeasurementTracking: the service has stopped.")
    }

    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    inner class MeasurementBinder : Binder() {
        fun getService(): MeasurementService = this@MeasurementService
    }

    companion object {
        const val SERVICE_COMMAND = "SERVICE_COMMAND"
    }
}