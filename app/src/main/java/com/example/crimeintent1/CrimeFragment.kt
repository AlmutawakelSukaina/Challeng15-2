package com.example.crimeintent1
import PictureDialogFragment
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.io.File
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DIALOG_PICTURE = "DialogePicture"
class CrimeFragment : Fragment() , DatePickerFragment.Callbacks{


    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton:Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private  lateinit var callSuspectButton:Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private   var width:Int=0
    private var hiegth:Int=0






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
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        callSuspectButton=view.findViewById(R.id.suspect_Call) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView


        val observer: ViewTreeObserver = photoView.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
               width=photoView.width
                hiegth=photoView.height
            }
        })
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

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent,
                        getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }
        suspectButton.apply {

            val pickContactIntent =
                Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI).apply {
                   type=ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE

                }
            //pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            setOnClickListener {
                startActivityForResult(pickContactIntent,
                    REQUEST_CONTACT)
            }
            val packageManager: PackageManager =
                requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }
  callSuspectButton.setOnClickListener{
   val callSusp=Intent(Intent.ACTION_DIAL)
   callSusp.data=Uri.parse("tel:${crime.number}")
      Log.e("crim","${crime.number}")
      startActivity(callSusp)

            }

        photoButton.apply {
            val packageManager: PackageManager =
                requireActivity().packageManager
            val captureImage =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,
                    photoUri)
                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage,
                    REQUEST_PHOTO)

            }
        }
        photoView.setOnClickListener{
            if (photoFile.exists())
            {
                PictureDialogFragment.newInstance(photoFile).apply { show(this@CrimeFragment.parentFragmentManager, DIALOG_PICTURE ) }
            }

        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,Observer {   crime ->
                crime?.let {
                    this.crime = crime

                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    photoUri =
                        FileProvider.getUriForFile(requireActivity(),
                            "com.example.crimeintent1.fileprovider",
                            photoFile)

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
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        updatePhotoView(hiegth,width)


    }


    private fun updatePhotoView(h:Int,w:Int) {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path,
                w,h)
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }



    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data

                val queryFields = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,  ContactsContract.CommonDataKinds.Phone.NUMBER
                )





                val cursor = contactUri?.let {
                    requireActivity().contentResolver.query(it , queryFields , null , null , null)
                }
                cursor?.use {

                    if (it.count == 0) {
                        return
                    }

                    it.moveToFirst()
                    val suspect = it.getString(0)
                   val number=it.getString(1)
                    crime.number=number


                    crime.suspect = suspect

                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect

                }
            }
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView(hiegth,width)
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString =DateFormat.format(DATE_FORMAT,
            crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect,
                crime.suspect)
        }
        return getString(R.string.crime_report,
            crime.title, dateString, solvedString, suspect)
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
