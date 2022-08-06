package com.singularitycoder.remindme

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.singularitycoder.remindme.databinding.ListItemReminderBinding
import java.util.concurrent.TimeUnit

class RemindersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var reminderList = emptyList<Reminder>()
    private var longClickListener: (reminder: Reminder) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = ListItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReminderViewHolder).setData(reminderList[position])
    }

    override fun getItemCount(): Int = reminderList.size

    override fun getItemViewType(position: Int): Int = position

    fun setOnLongClickListener(listener: (reminder: Reminder) -> Unit) {
        longClickListener = listener
    }

    inner class ReminderViewHolder(
        private val itemBinding: ListItemReminderBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun setData(reminder: Reminder) {
            itemBinding.apply {
                tvDay.text = reminder.date.substringBefore("-")
                tvMonth.text = reminder.date.substringAfter("-").substringBefore("-").uppercase()
                tvTime.text = "At ${reminder.time}"
                tvTitle.text = reminder.name
                root.setOnLongClickListener {
                    longClickListener.invoke(reminder)
                    false
                }
            }
            countDown(
                fromTimeInMillis = reminder.countDownTime,
                withIntervalInMillis = 1.seconds(), // FIXME this is counting 2 sec down instead of 1
                onTick = { millisUntilFinished: Long ->
                    /** We have to subtract currentTimeMillis from this as this is
                     * the total time since 1970. So subtracting current time will
                     * give the millis from the time we set the reminder to the
                     * time we want to be reminded
                     * seconds = millis / 1000
                     * minutes = seconds / 60
                     * hours = minutes / 60
                     * days = hours / 24
                     * */
                    val totalTimeLeftInSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - timeNow)
                    val totalTimeLeftInMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - timeNow)
                    val totalTimeLeftInHours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished - timeNow)
                    val totalTimeLeftInDays = TimeUnit.MILLISECONDS.toDays(millisUntilFinished - timeNow)

                    /** We want to ignore the time thats already calculated for a category.
                     * Suppose the total time is 1.5 days, we can remove the days from hours,
                     * remove hours from minutes, remove minutes from seconds.
                     * So 1 day will be as it is, then we remove (total days * 24 hrs) from
                     * the total 1.5 days. 1.5 days is 36 hours. So we remove 1 day
                     * from 36 hours which would be 12 hrs. Similarly we remove hours
                     * from minutes (total hours * 60 min) and for seconds (total minutes * 60 sec) */
                    val actualHoursLeft = if (totalTimeLeftInDays > 0) totalTimeLeftInHours - (totalTimeLeftInDays * 24) else totalTimeLeftInHours
                    val actualMinutesLeft = if (totalTimeLeftInHours > 0) totalTimeLeftInMinutes - (totalTimeLeftInHours * 60) else totalTimeLeftInMinutes
                    val actualSecondsLeft = if (totalTimeLeftInMinutes > 0) totalTimeLeftInSeconds - (totalTimeLeftInMinutes * 60) else totalTimeLeftInSeconds
                    itemBinding.tvCountDownTime.text = "$totalTimeLeftInDays d  :  $actualHoursLeft h  :  $actualMinutesLeft m  :  $actualSecondsLeft s"
                },
                onFinish = {
                    // TODO send to completed table
                    // TODO send notification with cancel alarm action. Foreground service.
                    // TODO ring alaram
                }
            )
        }
    }
}
