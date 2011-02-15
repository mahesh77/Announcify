package com.announcify.plugin.mail.google.service;

import android.content.Intent;

import com.announcify.api.AnnouncifyIntent;
import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.Filter;
import com.announcify.api.background.service.PluginService;
import com.announcify.api.background.text.Formatter;
import com.announcify.plugin.mail.google.contact.Mail;
import com.announcify.plugin.mail.google.util.Settings;


public class WorkerService extends PluginService {

    public static final String ACTION_START_RINGTONE = "com.announcify.plugin.mail.google.ACTION_START_RINGTONE";
    public static final String ACTION_STOP_RINGTONE = "com.announcify.plugin.mail.google.ACTION_STOP_RINGTONE";
    public static final String EXTRA_FROM = "com.announcify.plugin.mail.google.EXTRA_FROM";
    public static final String EXTRA_SUBJECT = "com.announcify.plugin.mail.google.EXTRA_SUBJECT";
    public static final String EXTRA_SNIPPET = "com.announcify.plugin.mail.google.EXTRA_SNIPPET";
    public static final String EXTRA_MESSAGE = "com.announcify.plugin.mail.google.EXTRA_MESSAGE";

    public WorkerService() {
        super("Announcify - Mail", ACTION_START_RINGTONE, ACTION_STOP_RINGTONE);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (settings == null) {
            settings = new Settings(this);
        }

        if (ACTION_ANNOUNCE.equals(intent.getAction())) {
            final String address = intent.getStringExtra(EXTRA_FROM);

            if ((address == null) && "".equals(address)) return;
            final Contact contact = new Contact(this, new Mail(this), address);

            if (!Filter.announcableContact(this, contact)) return;

            final Formatter formatter = new Formatter(this, contact, settings);

            String message = "";
            switch (((Settings) settings).getReadMessageMode()) {
                case 0:
                    message = intent.getStringExtra(EXTRA_SUBJECT);
                    break;

                case 1:
                    message = intent.getStringExtra(EXTRA_SNIPPET);
                    break;

                case 2:
                    message = intent.getStringExtra(EXTRA_MESSAGE);
                    break;
            }

            final AnnouncifyIntent announcify = new AnnouncifyIntent(this,
                    settings);
            announcify.setStopBroadcast(ACTION_START_RINGTONE);
            announcify.announce(formatter.format(message));
        } else {
            super.onHandleIntent(intent);
        }
        
        stopSelf();
    }
}