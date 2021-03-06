package com.announcify.plugin.twitter.twidroyd.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.announcify.plugin.twitter.twidroyd.service.WorkerService;

public class AnnouncifyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		intent.setComponent(new ComponentName(context, WorkerService.class));
		context.startService(intent);
	}
}
