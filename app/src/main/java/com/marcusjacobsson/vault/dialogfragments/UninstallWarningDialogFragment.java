package com.marcusjacobsson.vault.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.marcusjacobsson.vault.R;

/**
 * Created by Marcus Jacobsson on 2015-10-08.
 */
public class UninstallWarningDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getResources().getString(R.string.dialog_uninstall_warning_message));
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setTitle(getResources().getString(R.string.dialog_uninstall_warning_title));

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

}
