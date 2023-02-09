package com.vuzix.blade.devkit.battery_sample;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

/**
 * Main Activity that extend ActionMenuActivity.
 * This main class provide the basic information read the device battery/charge status.
 * For more information please reference:
 * https://developer.android.com/training/monitoring-device-state/battery-monitoring
 * Used Android API Classes:
 * https://developer.android.com/reference/android/content/BroadcastReceiver
 * https://developer.android.com/reference/android/os/AsyncTask
 * https://developer.android.com/reference/android/os/BatteryManager
 */
public class MainActivity extends ActionMenuActivity {

    private final String TAG = "VuzixBDK-Battery_Sample";
    private final IntentFilter batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private BroadcastReceiver BatteryReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /*

        After registration on Created, start the Broadcast receiver on Resume

     */
    @Override
    protected void onResume() {
        super.onResume();

         /*

        The BatteryManager broadcasts all battery and charging details in a sticky Intent

        Since the intent is sticky, you can simply register a null receiver to listen for changes in battery state (Intent.ACTION_BATTERY_CHANGED).

        For more information on the BatteryManager, please see https://developer.android.com/reference/android/os/BatteryManager

         */
         if(BatteryReceiver == null)
         {
             BatteryReceiver = new BroadcastReceiver() {
                 @Override
                 public void onReceive(Context context, final Intent intent) {
                     final PendingResult pendingResult = goAsync();

                     @SuppressLint("StaticFieldLeak") AsyncTask<String, Intent, Void> asyncTask = new AsyncTask<String, Intent, Void>() {
                         @Override
                         protected Void doInBackground(String... params) {

                             if (intent != null) {

                                 // Once we the battery changed receiver is registered, we can get information about the battery.
                                 // Here we get the battery level, how the battery is being charged, and what the charging status is.
                                 publishProgress(intent);
                             }

                             // Must call finish() so the BroadcastReceiver can be recycled.
                             pendingResult.finish();
                             return null;
                         }

                         @Override
                         protected void onProgressUpdate(Intent... values) {
                             super.onProgressUpdate(values);

                             logBatteryLevel(values[0]);
                             logChargingMethod(values[0]);
                             logChargingStatus(values[0]);

                         }
                     };
                     asyncTask.execute();
                 }
             };
         }

        Intent batteryStatus = registerReceiver(BatteryReceiver, batteryIntentFilter);
    }

    /*

        On Pause your Android Application is on the background and the receivers should be stop to
        conserve battery.

     */
    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(BatteryReceiver);
    }

    /*

            To get the battery status as a percentage, we need both the LEVEL and the SCALE of the battery.

             */
    private void logBatteryLevel(Intent batteryStatus) {

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPercentage = level / (float)scale * 100;

        Log.d(TAG, "Battery percentage is " + batteryPercentage + "%");
        ((TextView)findViewById(R.id.textView2)).setText(String.valueOf(batteryPercentage));
    }

    /*

    To get how the battery is charging, we need the EXTRA_PLUGGED value.

     */
    private void logChargingMethod(Intent batteryStatus) {

        int chargingMethod = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String chargingMethodText = getString(R.string.na);

        switch (chargingMethod) {
            case 0:
                chargingMethodText = "no external power source";
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                chargingMethodText = "power adapter";
                break;

            case BatteryManager.BATTERY_PLUGGED_USB:
                chargingMethodText = "USB battery";
                break;

            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                chargingMethodText = "wireless battery";
                break;
        }

        Log.d(TAG, "Battery is connected to " + chargingMethodText);
        ((TextView)findViewById(R.id.textView4)).setText(chargingMethodText);
    }

    /*

    To get how the battery status, we need the EXTRA_STATUS value.

     */
    private void logChargingStatus(Intent batteryStatus) {

        int chargingStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        String chargingText = getString(R.string.na);

        switch (chargingStatus) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                chargingText = "charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                chargingText = "discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                chargingText = "full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                chargingText = "not charging";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                chargingText = "unknown";
                break;
        }

        Log.d(TAG, "Battery is " + chargingText);
        ((TextView)findViewById(R.id.textView6)).setText(chargingText);
    }
}
