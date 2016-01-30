package com.inboardhack.scdrift;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by benjaminran on 1/30/16.
 */
public class FindBoard extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog dialog;
    private StartActivity activity;

    public FindBoard(StartActivity activity) {
        this.activity = activity;
        context = activity;
        dialog = new ProgressDialog(context);
    }

    private Context context;

    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Looking for your board...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (success) {
            Toast.makeText(context, "Connected to board", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Board not found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Boolean doInBackground(final String... args) {
        try {
            Thread.sleep(3000); // TODO: Actually connect to board
            return true;
        } catch (Exception e) {
            Log.e("Schedule", "UpdateSchedule failed", e);
            return false;
        }
    }


}
