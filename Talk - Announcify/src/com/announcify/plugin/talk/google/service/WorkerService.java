package com.announcify.plugin.talk.google.service;

import android.content.Intent;

import com.announcify.api.AnnouncifyIntent;
import com.announcify.api.background.contact.Contact;
import com.announcify.api.background.contact.Filter;
import com.announcify.api.background.service.PluginService;
import com.announcify.api.background.text.Formatter;
import com.announcify.plugin.talk.google.contact.Chat;
import com.announcify.plugin.talk.google.util.Settings;


public class WorkerService extends PluginService {

    public static final String ACTION_START_RINGTONE = "com.announcify.plugin.talk.google.ACTION_START_RINGTONE";
    public static final String ACTION_STOP_RINGTONE = "com.announcify.plugin.talk.google.ACTION_STOP_RINGTONE";
    public static final String EXTRA_FROM = "com.announcify.plugin.talk.EXTRA_FROM";
    public static final String EXTRA_MESSAGE = "com.announcify.plugin.talk.EXTRA_MESSAGE";

    public WorkerService() {
        super("Announcify - Talk", ACTION_START_RINGTONE, ACTION_STOP_RINGTONE);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (settings == null) {
            settings = new Settings(this);
        }

        if (ACTION_ANNOUNCE.equals(intent.getAction())) {
            final String address = intent.getStringExtra(EXTRA_FROM);

            final Settings settings = new Settings(this);

            if ((address == null) && "".equals(address)) return;
            final Contact contact = new Contact(this, new Chat(this), address);

            if (!Filter.announcableContact(this, contact)) return;

            final Formatter formatter = new Formatter(this, contact, settings);

            final AnnouncifyIntent announcify = new AnnouncifyIntent(this,
                    settings);
            announcify.setStopBroadcast(ACTION_START_RINGTONE);
            announcify.announce(formatter.format(intent
                    .getStringExtra(EXTRA_MESSAGE)));
        } else {
            super.onHandleIntent(intent);
        }
        
        stopSelf();
    }
}