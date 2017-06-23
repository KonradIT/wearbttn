package com.chernowii.wearbttn;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;

public class button_activity extends ComplicationProviderService {
    /*
    TODO:
    * Slide from left or right of the face (like Wear Mini Launcher and other 3rd party launchers) to trigger the launcher
    * Permanent overlay button
     */
    @Override
    public void onComplicationUpdate(int complicationId, int type, ComplicationManager manager) {
        if (type != ComplicationData.TYPE_ICON) {
            manager.noUpdateRequired(complicationId);
            return;
        }

        ComponentName thisProvider = new ComponentName(this, getClass());
        PendingIntent complicationTogglePendingIntent =
                ComplicationToggleReceiver.getToggleIntent(this, thisProvider, complicationId);

        SharedPreferences preferences =
                getSharedPreferences(ComplicationToggleReceiver.PREFERENCES_NAME, 0);

        int state =
                preferences.getInt(
                        ComplicationToggleReceiver.getPreferenceKey(thisProvider, complicationId),
                        0);

        ComplicationData data = null;
        switch (state % 3) {
            case 0:
                data =
                        new ComplicationData.Builder(type)
                                .setIcon(
                                        Icon.createWithResource(
                                                this, R.drawable.ic_start_launcher))
                                .setTapAction(complicationTogglePendingIntent)
                                .build();
                break;
            case 1:
                // This case includes a burn-in protection icon. If the screen uses burn-in
                // protection, that icon (which avoids solid blocks of color) should be shown in
                // ambient mode.
                data =
                        new ComplicationData.Builder(type)
                                .setIcon(Icon.createWithResource(this, R.drawable.ic_ambient))
                                .setBurnInProtectionIcon(
                                        Icon.createWithResource(
                                                this, R.drawable.ic_ambient))
                                .setTapAction(complicationTogglePendingIntent)
                                .build();
                break;
            case 2:
                data =
                        new ComplicationData.Builder(type)
                                .setIcon(
                                        Icon.createWithResource(
                                                this, R.drawable.ic_start_launcher))
                                .setTapAction(complicationTogglePendingIntent)
                                .build();
                break;
        }
        manager.updateComplicationData(complicationId, data);
    }
}