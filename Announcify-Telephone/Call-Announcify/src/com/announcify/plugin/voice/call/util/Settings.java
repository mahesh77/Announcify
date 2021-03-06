package com.announcify.plugin.voice.call.util;

import android.content.Context;

import com.announcify.api.background.util.PluginSettings;
import com.announcify.plugin.voice.call.R;

public class Settings extends PluginSettings {

	public static final String ACTION_SETTINGS = "com.announcify.plugin.voice.call.SETTINGS";
	public static final String PREFERENCES_NAME = "com.announcify.plugin.voice.call";

	public Settings(final Context context) {
		super(context, PREFERENCES_NAME);
	}

	@Override
	public String getEventType() {
		return context.getString(R.string.event_call);
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getReadingRepeat() {
		final String s = preferences.getString(KEY_READING_REPEAT, "5");
		if (s.equals(DEFAULT_SETTING)) {
			return defaultSettings.getDefaultReadingRepeat();
		} else {
			return Integer.parseInt(s);
		}
	}

	@Override
	public String getSettingsAction() {
		return ACTION_SETTINGS;
	}
}
