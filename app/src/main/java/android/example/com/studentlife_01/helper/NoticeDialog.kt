package android.example.com.studentlife_01.helper

import android.Manifest
import android.app.Activity
import android.app.Dialog

import android.example.com.studentlife_01.R
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.example.com.studentlife_01.activity.GroupDetailsActivity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_add_notice.*
import java.io.IOException

class NoticeDialog : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: NoticeDialogListener
    lateinit var et_title: EditText
    lateinit var et_content: EditText
    lateinit var btnChooser: Button
    lateinit var imageView: ImageView



    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: NoticeDialog)
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun getPermissions(dialog: NoticeDialog)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view: View = inflater.inflate(R.layout.dialog_add_notice, null)
            builder.setView(view)
            et_title = view.findViewById(R.id.et_title)
            et_content = view.findViewById(R.id.et_content)
            btnChooser = view.findViewById(R.id.buttonChoose)
            imageView = view.findViewById(R.id.imageView)

            btnChooser.setOnClickListener{
               
                listener.getPermissions(this)
            }


                // if the dialog is cancelable
            builder.setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                    Log.v("positive", "dialog")
                    listener.onDialogPositiveClick(this)

                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    listener.onDialogNegativeClick(this)
                })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
