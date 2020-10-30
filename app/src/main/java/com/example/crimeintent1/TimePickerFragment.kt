package com.example.crimeintent1

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_Time = "time"
class TimePickerFragment : DialogFragment(){

    interface Callbacks {
        fun onTimeSelected(time:Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_Time) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

      val  timeListener=   TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

          val resultDate : Date = GregorianCalendar( initialYear, initialMonth, initialDay,hour,minute).time
          targetFragment?.let { fragment -> (fragment as DatePickerFragment.Callbacks).onDateSelected(resultDate)
              

            }

        }



      var timeDoialog= TimePickerDialog( requireContext(), timeListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)


        return timeDoialog
    }

    companion object {
        fun newInstance(time: Date): TimePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_Time, time)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }



}