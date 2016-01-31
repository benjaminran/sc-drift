package com.inboardhack.scdrift;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by benjaminran on 1/30/16.
 */
public class FindBoard extends AsyncTask<String, Void, Boolean> {

    private static final long TIMEOUT = 15000;
    private ProgressDialog dialog;
    private BluetoothBridge bridge;
    private StartActivity activity;
    private Context context;
    private boolean tryAgain;

    public FindBoard(StartActivity activity) {
        tryAgain = false;
        context = activity;
        this.activity = activity;
        bridge = activity.dataService.bridge;
        dialog = new ProgressDialog(context);
    }


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
            new AlertDialog.Builder(context)
                    .setTitle("Failed to Find Board")
                    .setMessage("Try again?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tryAgain = true;
                            dialog.dismiss();
                            Intent intent = new Intent(context, StartActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                            activity.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    protected Boolean doInBackground(final String... args) {
        long startTime = System.currentTimeMillis();
        try {
            while(true) {
                if(bridge!=null && bridge.isConnected()) {
                    return true;
                }
                Thread.sleep(3000);
                return true;
                /*if(System.currentTimeMillis() - startTime > TIMEOUT) { // fail to connect
                    return false;
                }*/
            }
        } catch (Exception e) {
            Log.e("Schedule", "UpdateSchedule failed", e);
            return false;
        }
    }


}
