
package com.announcify.plugin.mail.k9.service;

import java.util.LinkedList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.BaseColumns;

import com.announcify.api.background.service.PluginService;
import com.announcify.plugin.mail.k9.util.Settings;

public class MailService extends Service {
    private ContentObserver observer;

    private HandlerThread thread;

    private Settings settings;

    @Override
    public void onCreate() {
        settings = new Settings(this);

        thread = new HandlerThread("K9MailThread");
        thread.start();
        observer = new MailObserver(new Handler(thread.getLooper()));

        getContentResolver().registerContentObserver(
                Uri.parse("content://com.fsck.k9.messageprovider/inbox_messages/"), true,
                observer);
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);

        thread.getLooper().quit();

        super.onDestroy();
    }

    private class MailObserver extends ContentObserver {

        private long maxMessageIdSeen;

        public MailObserver(final Handler handler) {
            super(handler);

            final Cursor cursor = getContentResolver().query(
                    Uri.parse("content://com.fsck.k9.messageprovider/inbox_messages/"),
                    null, null, null, null);
            cursor.moveToFirst();

            maxMessageIdSeen = Long.valueOf(cursor.getString(cursor.getColumnIndex(BaseColumns._ID)));

            cursor.close();
        }

        @Override
        public void onChange(final boolean selfChange) {
            Cursor conversations = null;
            Cursor messages = null;

            try {
                String[] projection = new String[] {
                        "conversation_id", "maxMessageId"
                };
                conversations = getContentResolver().query(
                        Uri.parse("content://gmail-ls/conversations/" + Uri.encode(address)),
                        projection, projection[1] + " > " + maxMessageIdSeen, null, null);
                if (!conversations.moveToFirst()) {
                    return;
                }

                final long conversationId = Long.valueOf(conversations.getString(conversations
                        .getColumnIndex(projection[0])));

                maxMessageIdSeen = Long.valueOf(conversations.getString(conversations
                        .getColumnIndex(projection[1])));

                projection = new String[] {
                        "fromAddress", "subject", "snippet", "body"
                };

                messages = getContentResolver().query(
                        Uri.parse("content://gmail-ls/conversations/" + Uri.encode(address) + "/"
                                + Uri.parse(String.valueOf(conversationId)) + "/messages"),
                                projection, null, null, null);
                if (messages.moveToLast()) {
                    return;
                }

                if (!settings.getReadOwn()
                        && address
                        .equals(messages.getString(messages.getColumnIndex(projection[0])))) {
                    return;
                }

                final Intent intent = new Intent(MailService.this, WorkerService.class);
                intent.setAction(PluginService.ACTION_ANNOUNCE);
                intent.putExtra(WorkerService.EXTRA_FROM,
                        messages.getString(messages.getColumnIndex(projection[0])));
                intent.putExtra(WorkerService.EXTRA_SUBJECT,
                        messages.getString(messages.getColumnIndex(projection[1])));
                intent.putExtra(WorkerService.EXTRA_SNIPPET,
                        messages.getString(messages.getColumnIndex(projection[2])));
                intent.putExtra(WorkerService.EXTRA_MESSAGE,
                        messages.getString(messages.getColumnIndex(projection[3])));
                startService(intent);
            } finally {
                if (messages != null) {
                    messages.close();
                }
                if (conversations != null) {
                    conversations.close();
                }
            }
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
