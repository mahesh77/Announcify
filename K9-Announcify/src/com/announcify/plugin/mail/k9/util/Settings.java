package com.announcify.plugin.mail.k9.util;

import android.content.Context;

import com.announcify.api.background.util.PluginSettings;
import com.announcify.plugin.mail.k9.R;

public class Settings extends PluginSettings {

    public static final String ACTION_SETTINGS = "com.announcify.plugin.mail.k9.SETTINGS";
    public static final String PREFERENCES_NAME = "com.announcify.plugin.k9.google";

    public Settings(final Context context) {
        super(context, PREFERENCES_NAME);
    }

    @Override
    public String getEventType() {
        return context.getString(R.string.event_k9);
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public int getReadMessageMode() {
        return Integer.parseInt(preferences.getString(KEY_MESSAGE, "0"));
    }

    @Override
    public String getSettingsAction() {
        return ACTION_SETTINGS;
    }
}
