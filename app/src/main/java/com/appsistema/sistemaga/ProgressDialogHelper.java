package com.appsistema.sistemaga;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

public class ProgressDialogHelper {
    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);

            // Setear color del ProgressDialog
            Drawable drawable = new ProgressBar(context).getIndeterminateDrawable().mutate();
            drawable.setColorFilter(context.getResources().getColor(R.color.botones), PorterDuff.Mode.SRC_IN);
            progressDialog.setIndeterminateDrawable(drawable);

            progressDialog.show();
        }
 }
 public static void ocultarProgressDialog(){

        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }
 }

}
