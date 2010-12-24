package com.announcify.plugin.talk.service;

import java.util.LinkedList;

import android.content.Intent;
import android.util.Log;

import com.announcify.contact.Contact;
import com.announcify.contact.Lookup;
import com.announcify.error.ExceptionHandler;
import com.announcify.plugin.talk.receiver.RingtoneReceiver;
import com.announcify.plugin.talk.util.TalknouncifySettings;
import com.announcify.queue.LittleQueue;
import com.announcify.queue.PrepareMachine;
import com.announcify.service.AnnouncifyService;

public class WorkerService extends AnnouncifyService {
	public static final String EXTRA_FROM = "com.announcify.plugin.mail.EXTRA_FROM";
	public static final String EXTRA_SUBJECT = "com.announcify.plugin.mail.EXTRA_SUBJECT";

	public WorkerService() {
		super("Announcify - Talk");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler()));

		final String address = intent.getStringExtra(EXTRA_FROM);
		Log.e("smn", address);
		final Contact contact = new Contact(this, address);
		if (address != null && !"".equals(address)) {
			Lookup.lookupMail(contact);
			Lookup.getNickname(contact);
		}

		final TalknouncifySettings settings = new TalknouncifySettings(this);

		final PrepareMachine prepare = new PrepareMachine(this, settings, contact, intent.getStringExtra(EXTRA_SUBJECT));
		final LinkedList<Object> list = prepare.prepare();

		if (list.isEmpty()) {
			return;
		}

		if (settings.getReadingWait() > 1000) {
			try {
				Thread.sleep(settings.getReadingWait());
			} catch (final InterruptedException e) {}
		}

		final LittleQueue queue = new LittleQueue("Talknouncify", list, "", RingtoneReceiver.ACTION_STOP_RINGTONE, this);
		queue.sendToService(this, 2);
	}
}
