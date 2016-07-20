package com.github.ar3s3ru.kubo.views.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.github.ar3s3ru.kubo.R;

/**
 * Copyright (C) 2016  Danilo Cianfrone
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

public class ErrorDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final int NEGATIVE_ID = -2;
    private static final int POSITIVE_ID = -1;

    private static final String INTENT = "com.github.ar3s3ru.kubo.views.dialogs.error.intent";
    private static final String ERROR  = "com.github.ar3s3ru.kubo.views.dialogs.error.data";
    private static final String CODE   = "com.github.ar3s3ru.kubo.views.dialogs.error.code";

    public static final String TAG = "ErrorDialog";

    /** Members variables */

    private Intent mLastAction;
    private String mError;
    private int    mErrorCode;

    public static ErrorDialog newInstance(@NonNull Intent action,
                                          @NonNull String error,
                                          int errorCode) {
        ErrorDialog dialog = new ErrorDialog();
        Bundle args = new Bundle();

        args.putParcelable(INTENT, action);
        args.putString(ERROR, error);
        args.putInt(CODE, errorCode);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save state
        outState.putParcelable(INTENT, mLastAction);
        outState.putString(ERROR, mError);
        outState.putInt(CODE, mErrorCode);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle takeArgs = getArguments();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (savedInstanceState != null) {
            takeArgs = savedInstanceState;
        }

        mLastAction = takeArgs.getParcelable(INTENT);
        mError      = takeArgs.getString(ERROR);
        mErrorCode  = takeArgs.getInt(CODE, 0);

        return builder.setTitle("Ops...")
                    .setMessage(getResources().getString(R.string.error_dialog_message, mErrorCode, mError))
                    .setPositiveButton(R.string.error_dialog_positive, this)
                    .setNegativeButton(R.string.error_dialog_negative, this)
                    .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == POSITIVE_ID) {
            // Send the inital intent again
            LocalBroadcastManager.getInstance(getContext())
                    .sendBroadcast(mLastAction);
        } else if (which == NEGATIVE_ID) {
            // Exit from ContentsActivity
            getActivity().finish();
        }
    }
}
