package com.singularitycoder.remindme

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
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
        else -> postedTime toTimeOfType DateType.dd_MMM_yyyy_hh_mm_a
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

enum class DateType(val value: String) {
    dd_MMM_yyyy(value = "dd MMM yyyy"),
    dd_MMM_yyyy_h_mm_a(value = "dd-MMM-yyyy h:mm a"),
    dd_MMM_yyyy_hh_mm_a(value = "dd MMM yyyy, hh:mm a"),
    dd_MMM_yyyy_hh_mm_ss_a(value = "dd MMM yyyy, hh:mm:ss a"),
    dd_MMM_yyyy_h_mm_ss_aaa(value = "dd MMM yyyy, h:mm:ss aaa"),
    yyyy_MM_dd_T_HH_mm_ss_SS_Z(value = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
}
