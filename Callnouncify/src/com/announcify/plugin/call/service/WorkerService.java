package com.announcify.plugin.call.service;

import java.util.LinkedList;

import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.announcify.contact.Contact;
import com.announcify.contact.Lookup;
import com.announcify.error.ExceptionHandler;
import com.announcify.plugin.call.receiver.RingtoneReceiver;
import com.announcify.plugin.call.util.CallnouncifySettings;
import com.announcify.queue.LittleQueue;
import com.announcify.queue.PrepareMachine;
import com.announcify.service.AnnouncifyService;

public class WorkerService extends AnnouncifyService {

	public WorkerService() {
		super("Announcify - Call");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler()));

		final String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		Log.e("smn", number);
		final Contact contact = new Contact(this, number);
		if (number != null && !"".equals(number)) {
			Lookup.lookupNumber(contact);
			Lookup.getNickname(contact);
		}

		final CallnouncifySettings settings = new CallnouncifySettings(this);

		final PrepareMachine prepare = new PrepareMachine(this, settings, contact, "");
		final LinkedList<Object> list = prepare.prepare();

		if (list.isEmpty()) {
			return;
		}

		if (settings.getReadingWait() > 1000) {
			try {
				Thread.sleep(settings.getReadingWait());
			} catch (final InterruptedException e) {}
		}

		final LittleQueue queue = new LittleQueue("Callnouncify", list, RingtoneReceiver.ACTION_START_RINGTONE, "", this);
		queue.sendToService(this, 0);
	}
}
