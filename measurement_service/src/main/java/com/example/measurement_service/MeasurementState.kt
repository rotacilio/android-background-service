package com.example.measurement_service

import java.io.Serializable

enum class MeasurementState : Serializable {
    INITIALIZED,
    START,
    PAUSE,
    STOP
}