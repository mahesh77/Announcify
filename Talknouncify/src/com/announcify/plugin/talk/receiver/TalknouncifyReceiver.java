package com.announcify.plugin.talk.receiver;

import android.content.Context;
import android.content.Intent;

import com.announcify.plugin.talk.activity.Talknouncify;
import com.announcify.receiver.AnnouncifyReceiver;

public class TalknouncifyReceiver extends AnnouncifyReceiver {
	@Override
	public void onReceive(final Context context, final Intent intent) {
		final Intent respondIntent = new Intent(ACTION_PLUGIN_RESPOND);
		respondIntent.putExtra(EXTRA_PLUGIN_NAME, "Talknouncify");
		respondIntent.putExtra(EXTRA_PLUGIN_ACTION, Talknouncify.ACTION_SETTINGS);
		context.sendBroadcast(respondIntent);
	}
}