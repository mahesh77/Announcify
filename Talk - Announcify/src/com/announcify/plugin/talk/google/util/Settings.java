
package com.announcify.plugin.talk.google.util;

import android.content.Context;

import com.announcify.api.background.util.PluginSettings;

public class Settings extends PluginSettings {

    public static final String ACTION_SETTINGS = "com.announcify.plugin.talk.google.SETTINGS";

    public Settings(final Context context) {
        super(context);
    }

    @Override
    public String getEventType() {
        return "Chat";
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public String getSettingsAction() {
        return ACTION_SETTINGS;
    }
}
