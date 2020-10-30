package com.example.crimeintent1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0
class CrimeFragment : Fragment() , DatePickerFragment.Callbacks{


    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton:Button
    private lateinit var solvedCheckBox: CheckBox

    private val crimeDetailViewModel: CrimeDetailViewModel by
    lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID =
            arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)

    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime,
            container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText

        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton=view.findViewById(R.id.crime_tIME) as Button

       /* dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }*/
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment,
                    REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(),
                    DIALOG_DATE)
            }
        }

        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment,
                    REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager(),
                    DIALOG_TIME)
            }
        }






        solvedCheckBox = view.findViewById(R.id.imageView) as
                CheckBox





        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,Observer {   crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            } )

    }
    override fun onStart() {
        super.onStart()
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
// This space intentionally left blank
            }
            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
// This one too
            }
        }
        titleField.addTextChangedListener(titleWatcher)
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        timeButton.text= crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }

    }
    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()

    }

}
