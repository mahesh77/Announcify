package com.announcify.plugin.message.sms.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.announcify.api.background.service.PluginService;
import com.announcify.plugin.message.sms.service.WorkerService;

public class MessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		intent.setComponent(new ComponentName(context, WorkerService.class));
		intent.setAction(PluginService.ACTION_ANNOUNCE);
		context.startService(intent);
	}
}
