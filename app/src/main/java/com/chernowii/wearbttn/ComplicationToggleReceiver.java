package com.chernowii.wearbttn;

/**
 * Created by konrad on 6/23/17.
 */

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.complications.ProviderUpdateRequester;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

/** Receives intents on tap and causes complication states to be toggled and updated. */
public class ComplicationToggleReceiver extends BroadcastReceiver {
    private static final String EXTRA_PROVIDER_COMPONENT = "providerComponent";
    private static final String EXTRA_COMPLICATION_ID = "complicationId";

    static final String PREFERENCES_NAME = "ComplicationTestSuite";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        ComponentName provider = extras.getParcelable(EXTRA_PROVIDER_COMPONENT);
        int complicationId = extras.getInt(EXTRA_COMPLICATION_ID);

        String preferenceKey = getPreferenceKey(provider, complicationId);
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, 0);
        int value = pref.getInt(preferenceKey, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(preferenceKey, value + 1); // Increase value by 1
        editor.apply();

        // Request an update for the complication that has just been toggled.
        ProviderUpdateRequester requester = new ProviderUpdateRequester(context, provider);
        requester.requestUpdate(complicationId);
        //Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
        /*
        This should open the drawer because 3 is HOME keyevent, tried using adb shell and it opens the wear 2.0 drawer.
        Using adb shell no root needed.
        Can also try using start launcher activity or emulate power button press
         */
        try{
            Process su = Runtime.getRuntime().exec("/system/bin/sh -c ");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("input keyevent 26\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        }catch(IOException | InterruptedException e){
            try {
                throw new Exception(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Returns a pending intent, suitable for use as a tap intent, that causes a complication to be
     * toggled and updated.
     */
    static PendingIntent getToggleIntent(
            Context context, ComponentName provider, int complicationId) {
        Intent intent = new Intent(context, ComplicationToggleReceiver.class);
        intent.putExtra(EXTRA_PROVIDER_COMPONENT, provider);
        intent.putExtra(EXTRA_COMPLICATION_ID, complicationId);

        // Pass complicationId as the requestCode to ensure that different complications get
        // different intents.
        return PendingIntent.getBroadcast(
                context, complicationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Returns the key for the shared preference used to hold the current state of a given
     * complication.
     */
    static String getPreferenceKey(ComponentName provider, int complicationId) {
        return provider.getClassName() + complicationId;
    }
}