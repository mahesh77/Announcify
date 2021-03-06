package com.announcify.plugin.voice.call.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.announcify.api.background.service.PluginService;
import com.announcify.plugin.voice.call.service.WorkerService;

public class CallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(intent
				.getStringExtra(TelephonyManager.EXTRA_STATE))) {
			final Intent serviceIntent = new Intent(context,
					WorkerService.class);
			serviceIntent.putExtras(intent.getExtras());
			serviceIntent.setAction(PluginService.ACTION_ANNOUNCE);
			context.startService(serviceIntent);
		} else {
			final Intent ringtoneIntent = new Intent(
					WorkerService.ACTION_STOP_RINGTONE);
			context.sendBroadcast(ringtoneIntent);
		}
	}
}
