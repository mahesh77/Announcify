package com.announcify.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import com.announcify.R;
import com.announcify.activity.control.RemoteControlDialog;
import com.announcify.error.ExceptionHandler;
import com.announcify.handler.AnnouncificationHandler;
import com.announcify.queue.LittleQueue;
import com.announcify.receiver.ControlReceiver;
import com.announcify.tts.Speaker;

public class ManagerService extends Service {
	private NotificationManager notificationManager;
	private ConditionManager conditionManager;
	private ControlReceiver controlReceiver;
	private AnnouncificationHandler handler;
	// private Volume manager;
	private HandlerThread thread;
	private Speaker speaker;

	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		// manager = new Volume(this);

		conditionManager = new ConditionManager(this);
		// if (conditionManager.isScreenOn()) {
		// manager.lowerSpeechVolume();
		// }

		thread = new HandlerThread("Announcifications");
		thread.start();

		speaker = new Speaker(ManagerService.this, new OnInitListener() {

			public void onInit(final int status) {
				handler.sendEmptyMessage(AnnouncificationHandler.WHAT_START);
			}
		});

		handler = new AnnouncificationHandler(ManagerService.this, thread.getLooper(), speaker);
		handler.post(new Runnable() {

			public void run() {
				Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(ManagerService.this));
			}
		});

		controlReceiver = new ControlReceiver(handler);
		final IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(RemoteControlDialog.ACTION_CONTINUE);
		controlFilter.addAction(RemoteControlDialog.ACTION_PAUSE);
		controlFilter.addAction(RemoteControlDialog.ACTION_SKIP);
		registerReceiver(controlReceiver, controlFilter);

		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 1993, new Intent(this, RemoteControlDialog.class), 0);
		final Notification notification = new Notification(R.drawable.notification_icon, null, 0);
		notification.setLatestEventInfo(this, "Important Announcification", "Press here to stop it.", pendingIntent);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(17, notification);
	}

	@Override
	public void onStart(final Intent intent, final int startId) {
		if (intent == null || intent.getExtras() == null) return;
		Bundle bundle = intent.getExtras();
		if (bundle == null) return;
		
		if (bundle.getInt(AnnouncifyService.EXTRA_PRIORITY, -1) > 0 && conditionManager.isScreenOn()) {
			return;
		}

		final Message msg = handler.obtainMessage(AnnouncificationHandler.WHAT_PUT_QUEUE);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	@Override
	public void onDestroy() {
		Log.e("Announcify", "shutdown");

		if (handler != null) {
			final Message msg = handler.obtainMessage(AnnouncificationHandler.WHAT_SHUTDOWN);
			handler.sendMessage(msg);
		}

		if (controlReceiver != null) {
			unregisterReceiver(controlReceiver);
		}

		conditionManager.quit();
		if (speaker != null) {
			speaker.shutdown();
		}
		if (thread != null && thread.isAlive()) {
			thread.stop();
		}
		if (notificationManager != null) {
			notificationManager.cancel(17);
		}
		// manager.quit();
	}

	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}
}