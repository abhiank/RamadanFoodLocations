package com.ramadan.food;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

/**
 * Created by abhimanyuagrawal on 25/06/15.
 */
public class Utils {

    private static ProgressDialog mProgressDialog;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showProgressDialogue(Context context, String dialogueMessage) {
        if(mProgressDialog==null) {
            mProgressDialog = new ProgressDialog(context, android.R.style.Theme_Translucent);
            mProgressDialog.show();
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#66000000")));
            //mProgressDialog.setMessage(dialogueMessage);
            mProgressDialog.setContentView(R.layout.progress_dialog);
            mProgressDialog.setCancelable(false);
            ((TextView)mProgressDialog.findViewById(R.id.progress_text)).setText(dialogueMessage);
        }
    }

    public static void dismissProgressDialogue() {
        if (null != mProgressDialog ) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
