package com.example.affine.beaconscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by lenovo on 17/11/17.
 */

public class PopUp {
    public static void pop(Context context, String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(body).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {

                }
            }
        });
        builder.create().show();
    }

    public static void pop(Context context, String title, String body, String positiveButtonName, String negativeButtonName, String neutralButtonName, final Callback callback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!positiveButtonName.isEmpty()) {
            builder.setTitle(title).setMessage(body).setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();
                        callback.onPosiveClick();
                    } catch (Exception e) {

                    }
                }
            });
        }
        if (!negativeButtonName.isEmpty()) {
            builder.setTitle(title).setMessage(body).setNegativeButton(negativeButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();
                        callback.onNegativeClick();
                    } catch (Exception e) {

                    }
                }
            });
        }
        if (!neutralButtonName.isEmpty()) {
            builder.setTitle(title).setMessage(body).setNeutralButton(neutralButtonName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();
                        callback.onNeutralClick();
                    } catch (Exception e) {

                    }
                }
            });
        }
        builder.create().show();
    }

    public static ProgressDialog progressBar(Context context, String message) {
        ProgressDialog waitDialog = new ProgressDialog(context);
        waitDialog.setTitle(message);
        return waitDialog;
    }

    public abstract static class Callback {
        protected void onPosiveClick() {

        }

        protected void onNegativeClick() {

        }

        protected void onNeutralClick() {

        }
    }
}

