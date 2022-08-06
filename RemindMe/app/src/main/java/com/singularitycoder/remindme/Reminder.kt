package com.singularitycoder.remindme

data class Reminder(
    val name: String,
    val date: String,
    var time: String = "",
    var countDownTime: Long = 0L
)