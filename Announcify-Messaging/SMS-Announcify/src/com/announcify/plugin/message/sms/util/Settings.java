package com.announcify.plugin.message.sms.util;

import android.content.Context;

import com.announcify.api.background.util.PluginSettings;
import com.announcify.plugin.message.sms.R;

public class Settings extends PluginSettings {

	public static final String ACTION_SETTINGS = "com.announcify.plugin.message.sms.SETTINGS";
	public static final String PREFERENCES_NAME = "com.announcify.plugin.message.sms";

	public Settings(final Context context) {
		super(context, PREFERENCES_NAME);
	}

	@Override
	public String getEventType() {
		return context.getString(R.string.event_sms);
	}

	@Override
	public int getPriority() {
		return 4;
	}

	@Override
	public String getSettingsAction() {
		return ACTION_SETTINGS;
	}
}
