package com.singularitycoder.remindme

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.singularitycoder.remindme.databinding.FragmentRemindersBinding

class RemindersFragment : Fragment() {

    // TODO Notification + Alaram
    // TODO Notification will have stop reminder which stops the alaram
    // TODO search filter - filter by date and time

    companion object {
        @JvmStatic
        fun newInstance(reminderType: String) = RemindersFragment().apply {
            arguments = Bundle().apply { putString(ARG_PARAM_REMINDER_TYPE, reminderType) }
        }
    }

    private lateinit var binding: FragmentRemindersBinding

    private var reminderTypeParam: String? = null
    private val remindersAdapter = RemindersAdapter()
    private val reminderList = mutableListOf<Reminder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderTypeParam = arguments?.getString(ARG_PARAM_REMINDER_TYPE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupUI()
        binding.setUpUserActionListeners()
    }

    private fun FragmentRemindersBinding.setupUI() {
        cardAddReminder.isEnabled = etReminder.text.isNullOrBlank().not()
        ibAddReminder.isEnabled = etReminder.text.isNullOrBlank().not()
        if (reminderTypeParam == remindersTabNamesList.first()) {
            cardAddReminderParent.isVisible = true
            etSearch.hint = "Search pending reminders"
        } else {
            cardAddReminderParent.isVisible = false
            etSearch.hint = "Search completed reminders"
        }
        rvReminders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = remindersAdapter
        }
    }

    private fun FragmentRemindersBinding.setUpUserActionListeners() {
        etReminder.doAfterTextChanged { it: Editable? ->
            cardAddReminder.isEnabled = etReminder.text.isNullOrBlank().not()
            ibAddReminder.isEnabled = etReminder.text.isNullOrBlank().not()
        }
        remindersAdapter.setOnLongClickListener {
            // TODO bottom sheet
            // TODO Mark as complete
            // TODO Delete - Confirmation on delete popup
        }
        ibAddReminder.setOnClickListener {
            cardAddReminder.performClick()
        }
        cardAddReminder.setOnClickListener {
            // TODO same behaviour on IME option click
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Set reminder date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())  // Opens the date picker with today's date selected.
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "TAG_DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener { it: Long? ->
            val date = convertLongToTime(time = it ?: return@addOnPositiveButtonClickListener, type = 3u)
            println("Date selected: $date") // 04-Aug-2022
            val reminder = Reminder(
                name = binding.etReminder.text.toString(),
                date = date,
            )
            showTimePicker(reminder)
        }
        datePicker.addOnNegativeButtonClickListener {
        }
    }

    private fun showTimePicker(reminder: Reminder) {
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("Set reminder time")
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .build()
        timePicker.show(parentFragmentManager, "tag_time_picker")
        timePicker.addOnPositiveButtonClickListener {
            val reminderTime = convertTime24HrTo12Hr(date24Hr = "${timePicker.hour}:${timePicker.minute}")
            val reminderDateTimeInMillis = millisSinceEpoch("${reminder.date} ${timePicker.hour}:${timePicker.minute}")
            reminder.apply {
                time = reminderTime.ifBlank { "${timePicker.hour}:${timePicker.minute}" }
                countDownTime = reminderDateTimeInMillis
            }
            reminderList.add(reminder)
            remindersAdapter.reminderList = reminderList
            remindersAdapter.notifyItemRangeInserted(reminderList.lastIndex, reminderList.size)
            binding.etReminder.apply {
                setText("")
                clearFocus()
            }
            println("""
                        Hours: ${timePicker.hour}
                        Minutes: ${timePicker.minute}
                        InputMode: ${timePicker.inputMode}
                        reminderTime: $reminderTime
                        reminderDateTimeInMillis: $reminderDateTimeInMillis
                    """.trimIndent())
        }
        timePicker.addOnNegativeButtonClickListener {
        }
    }
}

private const val ARG_PARAM_REMINDER_TYPE = "ARG_PARAM_REMINDER_TYPE"