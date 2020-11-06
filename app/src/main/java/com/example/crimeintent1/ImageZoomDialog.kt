import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.crimeintent1.R
import com.example.crimeintent1.getScaledBitmap
import java.io.File

private const val ARG_IMAGE = "image"

class PictureDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {

            val builder = AlertDialog.Builder(it)


            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.image_zoom, null)

            // Inflate and set the layout for the dialog
            builder.setView(view)

            //get reference to crimePicture image view
            val crimePicture = view.findViewById(R.id.crimePicture) as ImageView

            //get the image file path argument
            val photoFile = arguments?.getSerializable(ARG_IMAGE) as File

            //get the scaled image
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())

            //set the picture in the crimePicture view
            crimePicture.setImageBitmap(bitmap)


            //set the dialog characteristics
            builder.setTitle("CrimePhoto")
                .setNegativeButton("Dismiss", DialogInterface.OnClickListener{ _, _ -> dialog?.cancel() } )


            // Create the AlertDialog object and return it
            builder.create()

        } ?: throw IllegalStateException("Acitivity cannot be null")

    }


    companion object {
        fun newInstance(photoFile: File): PictureDialogFragment {
            val args = Bundle().apply { putSerializable(ARG_IMAGE, photoFile) }

            return PictureDialogFragment().apply { arguments = args }

        }
    }
}