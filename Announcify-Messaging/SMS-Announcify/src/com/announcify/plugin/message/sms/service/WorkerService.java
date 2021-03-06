package com.announcify.plugin.message.sms.service;

import android.content.Intent;
import android.telephony.SmsMessage;

import com.announcify.api.AnnouncifyIntent;
import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.ContactFilter;
import com.announcify.api.background.error.ExceptionHandler;
import com.announcify.api.background.service.PluginService;
import com.announcify.api.background.text.Formatter;
import com.announcify.plugin.message.sms.util.Settings;

public class WorkerService extends PluginService {

	public final static String ACTION_START_RINGTONE = "com.announcify.plugin.message.sms.ACTION_START_RINGTONE";
	public final static String ACTION_STOP_RINGTONE = "com.announcify.plugin.message.sms.ACTION_STOP_RINGTONE";

	public WorkerService() {
		super("Announcify - Message", ACTION_START_RINGTONE,
				ACTION_STOP_RINGTONE);
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(
				getBaseContext()));

		if (settings == null) {
			settings = new Settings(this);
		}

		if (ACTION_ANNOUNCE.equals(intent.getAction())) {
			final String message;
			String number;

			if (intent.getExtras().containsKey("pdus")) {
				final Object[] pdusObj = (Object[]) intent.getExtras().get(
						"pdus");

				final SmsMessage[] messages = new SmsMessage[pdusObj.length];
				for (int i = 0; i < pdusObj.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}

				String temp = "";

				if (messages.length > 1) {
					for (final SmsMessage currentMessage : messages) {
						temp = temp + currentMessage.getDisplayMessageBody()
								+ '\n';
					}
				} else {
					temp = messages[0].getDisplayMessageBody();
				}

				number = messages[0].getDisplayOriginatingAddress();
				message = temp;
			} else {
				message = "Please spread the word about Announcify";
				number = intent.getStringExtra("com.announcify.EXTRA_TEST");
			}

			final Settings settings = new Settings(this);

			if (number == null) {
				number = "";
			}
			final Contact contact = new Contact(this,
					new com.announcify.api.background.contact.lookup.Number(
							this), number);

			if (!settings.isChuckNorris()) {
				if (!ContactFilter.announcableContact(this, contact)) {
					playRingtone();
					return;
				}
			}

			final Formatter formatter = new Formatter(this, contact, settings);

			final AnnouncifyIntent announcify = new AnnouncifyIntent(this,
					settings);
			announcify.setStopBroadcast(ACTION_START_RINGTONE);
			announcify.announce(formatter.format(message));
		} else {
			super.onHandleIntent(intent);
		}
	}
}
