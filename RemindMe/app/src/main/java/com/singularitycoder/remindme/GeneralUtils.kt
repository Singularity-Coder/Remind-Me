package com.singularitycoder.remindme

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val DB_REMINDER = "db_reminder"
const val TABLE_REMINDER = "table_reminder"

val remindersTabNamesList = listOf("Pending", "Completed")

fun View.showSnackBar(
    message: String,
    anchorView: View? = null,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionBtnText: String = "NA",
    action: () -> Unit = {},
) {
    Snackbar.make(this, message, duration).apply {
        this.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        if (null != anchorView) this.anchorView = anchorView
        if ("NA" != actionBtnText) setAction(actionBtnText) { action.invoke() }
        this.show()
    }
}

fun getDeviceSize(): Point = try {
    Point(deviceWidth(), deviceHeight())
} catch (e: Exception) {
    e.printStackTrace()
    Point(0, 0)
}

fun deviceWidth() = Resources.getSystem().displayMetrics.widthPixels

fun deviceHeight() = Resources.getSystem().displayMetrics.heightPixels

// Get Epoch Time
val timeNow: Long
    get() = System.currentTimeMillis()

fun Long.toIntuitiveDateTime(): String {
    val postedTime = this
    val elapsedTimeMillis = timeNow - postedTime
    val elapsedTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis)
    val elapsedTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis)
    val elapsedTimeInHours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
    val elapsedTimeInDays = TimeUnit.MILLISECONDS.toDays(elapsedTimeMillis)
    val elapsedTimeInMonths = elapsedTimeInDays / 30
    return when {
        elapsedTimeInSeconds < 60 -> "Now"
        elapsedTimeInMinutes == 1L -> "$elapsedTimeInMinutes Minute ago"
        elapsedTimeInMinutes < 60 -> "$elapsedTimeInMinutes Minutes ago"
        elapsedTimeInHours == 1L -> "$elapsedTimeInHours Hour ago"
        elapsedTimeInHours < 24 -> "$elapsedTimeInHours Hours ago"
        elapsedTimeInDays == 1L -> "$elapsedTimeInDays Day ago"
        elapsedTimeInDays < 30 -> "$elapsedTimeInDays Days ago"
        elapsedTimeInMonths == 1L -> "$elapsedTimeInMonths Month ago"
        elapsedTimeInMonths < 12 -> "$elapsedTimeInMonths Months ago"
        else -> postedTime toTimeOfType DateType.SPACE_dd_MMM_yyyy_hh_mm_a
    }
}

infix fun Long.toTimeOfType(type: DateType): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat(type.value, Locale.getDefault())
    return dateFormat.format(date)
}

fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_LONG,
) = Toast.makeText(this, message, duration).show()

fun doAfter(duration: Long, task: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(task, duration)
}

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

val dateFormatList = listOf(
    "dd-MMMM hh:mm",
    "dd-MM-yyyy",
    "dd/MM/yyyy",
    "dd-MMM-yyyy",
    "dd/MMM/yyyy",
    "dd-MMM-yyyy",
    "dd MMM yyyy",
    "dd-MMM-yyyy h:mm a",
    "dd MMM yyyy, hh:mm a",
    "dd MMM yyyy, hh:mm:ss a",
    "dd MMM yyyy, h:mm:ss aaa",
    "yyyy/MM/dd",
    "yyyy-MM-dd",
    "yyyy.MM.dd HH:mm",
    "yyyy/MM/dd hh:mm aa",
    "yyyy-MM-dd'T'HH:mm:ss.SS'Z'",
    "hh:mm a"
)

fun convertLongToTime(time: Long, type: UByte): String {
    val date = Date(time)
    val dateFormat = SimpleDateFormat(dateFormatList.getOrElse(index = type.toInt(), defaultValue = { dateFormatList[3] }), Locale.getDefault())
    return dateFormat.format(date)
}

fun convertDateToLong(date: String, type: UByte): Long {
    if (date.isBlank()) return convertDateToLong(date = Date().toString(), type = 3u)
    val dateFormat = SimpleDateFormat(dateFormatList.getOrElse(index = type.toInt(), defaultValue = { dateFormatList[3] }), Locale.getDefault())
    return try {
        if (dateFormat.parse(date) is Date) dateFormat.parse(date).time else convertDateToLong(date = Date().toString(), type = 3u)
    } catch (e: Exception) {
        convertDateToLong(date = Date().toString(), type = 3u)
    }
}

/** [type] format: 04-Aug-2022 17:22*/
fun millisSinceEpoch(date: String, type: String = "dd-MMM-yyyy HH:mm"): Long {
    return try {
        val dateFormat = SimpleDateFormat(type, Locale.getDefault())
        dateFormat.parse(date)?.time ?: 0
    } catch (e: Exception) {
        0L
    }
}

// https://stackoverflow.com/questions/6531632/conversion-from-12-hours-time-to-24-hours-time-in-java
fun convertTime24HrTo12Hr(date24Hr: String): String {
    return try {
        val dateFormat24Hr = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat12Hr = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val parsedDate24Hr = dateFormat24Hr.parse(date24Hr) ?: Date()
        dateFormat12Hr.format(parsedDate24Hr)
    } catch (e: Exception) {
        ""
    }
}

fun countDown(
    fromTimeInMillis: Long,
    withIntervalInMillis: Long,
    onTick: (millisUntilFinished: Long) -> Unit,
    onFinish: () -> Unit,
): CountDownTimer {
    return object : CountDownTimer(fromTimeInMillis, withIntervalInMillis) {
        override fun onTick(millisUntilFinished: Long) {
            onTick.invoke(millisUntilFinished)
        }

        override fun onFinish() {
            onFinish.invoke()
        }
    }.start()
}

fun Int.seconds(): Long = TimeUnit.SECONDS.toMillis(this.toLong())

fun Int.minutes(): Long = TimeUnit.MINUTES.toMillis(this.toLong())

fun Int.hours(): Long = TimeUnit.HOURS.toMillis(this.toLong())

enum class DateType(val value: String) {
    SPACE_dd_MMM_yyyy(value = "dd MMM yyyy"),
    HYPEN_dd_MMM_yyyy_h_mm_a(value = "dd-MMM-yyyy h:mm a"),
    SPACE_dd_MMM_yyyy_hh_mm_a(value = "dd MMM yyyy, hh:mm a"),
    SPACE_dd_MMM_yyyy_hh_mm_ss_a(value = "dd MMM yyyy, hh:mm:ss a"),
    SPACE_dd_MMM_yyyy_h_mm_ss_aaa(value = "dd MMM yyyy, h:mm:ss aaa"),
    HYPEN_yyyy_MM_dd_T_HH_mm_ss_SS_Z(value = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
}
