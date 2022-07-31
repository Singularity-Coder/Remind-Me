package com.singularitycoder.remindme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.singularitycoder.remindme.databinding.FragmentRemindersBinding

class RemindersFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(reminderType: String) = RemindersFragment().apply {
            arguments = Bundle().apply { putString(ARG_PARAM_REMINDER_TYPE, reminderType) }
        }
    }

    private lateinit var binding: FragmentRemindersBinding

    private var reminderTypeParam: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reminderTypeParam = it.getString(ARG_PARAM_REMINDER_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setupUI()
    }

    fun FragmentRemindersBinding.setupUI() {
        if (reminderTypeParam == remindersTabNamesList.first()) {
            cardAddReminder.isVisible = true
            etSearch.hint = "Search pending reminders"
        } else {
            cardAddReminder.isVisible = false
            etSearch.hint = "Search completed reminders"
        }
    }
}

private const val ARG_PARAM_REMINDER_TYPE = "ARG_PARAM_REMINDER_TYPE"