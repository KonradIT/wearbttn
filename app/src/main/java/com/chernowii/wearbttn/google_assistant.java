package com.chernowii.wearbttn;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;

/**
 * Created by konrad on 6/24/17.
 */

public class google_assistant extends ComplicationProviderService {

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
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("COMP_NAME", "ASSISTANT");
        editor.apply();
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
                                                this, R.drawable.ic_start_assistant))
                                .setTapAction(complicationTogglePendingIntent)
                                .build();
                break;
            case 1:
                // This case includes a burn-in protection icon. If the screen uses burn-in
                // protection, that icon (which avoids solid blocks of color) should be shown in
                // ambient mode.
                data =
                        new ComplicationData.Builder(type)
                                .setIcon(Icon.createWithResource(this, R.drawable.ic_start_assistant))
                                .setBurnInProtectionIcon(
                                        Icon.createWithResource(
                                                this, R.drawable.ic_start_assistant))
                                .setTapAction(complicationTogglePendingIntent)
                                .build();
                break;
            case 2:
                data =
                        new ComplicationData.Builder(type)
                                .setIcon(
                                        Icon.createWithResource(
                                                this, R.drawable.ic_start_assistant))
                                .setTapAction(complicationTogglePendingIntent)
                                .build();
                break;
        }
        manager.updateComplicationData(complicationId, data);
    }
}