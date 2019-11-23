package android.example.com.studentlife_01.helper

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.example.com.studentlife_01.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class GroupDialog : DialogFragment() {
    // Use this instance of the interface to deliver action events
    internal lateinit var listener: GroupDialog.GroupDialogListener
    lateinit var et_name: EditText

    interface GroupDialogListener {
        fun onDialogPositiveClick(dialog: GroupDialog)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as GroupDialog.GroupDialogListener
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
            val view: View = inflater.inflate(R.layout.dialog_add_group, null)
            builder.setView(view)
            et_name = view.findViewById(R.id.et_name)



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