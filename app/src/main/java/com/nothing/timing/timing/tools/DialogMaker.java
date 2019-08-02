package com.nothing.timing.timing.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogMaker {


    public static void executeDialog(Context context, String msg, DialogInterface.OnClickListener dialog) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton("Yep", dialog)
                .setNegativeButton("Nope", dialog).show();

    }


}
