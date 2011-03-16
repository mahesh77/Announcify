package com.announcify.plugin.talk.google.service;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.announcify.api.background.error.ExceptionHandler;
import com.announcify.api.background.service.PluginService;


public class TalkService extends Service {

    private class TalkObserver extends ContentObserver {
        
        private boolean paused;
        
        private Handler handler;

        public TalkObserver(final Handler handler) {
            super(handler);
            
            this.handler = handler;
        }

        @Override
        public void onChange(final boolean selfChange) {
            if (paused) return;
            
            final String[] messageProjection = new String[] { "body", "date" };
            final Cursor message = getContentResolver()
                    .query(Uri.withAppendedPath(
                            Uri.parse("content://com.google.android.providers.talk/"),
                            "messages"), messageProjection, "err_code = 0",
                            null, "date DESC");
            if (!message.moveToFirst()) return;

            // body

            final String[] conversationProjection = new String[] {
                    "last_unread_message", "last_message_date" };
            final Cursor conversation = getContentResolver()
                    .query(Uri.withAppendedPath(
                            Uri.parse("content://com.google.android.providers.talk/"),
                            "chats"), conversationProjection, null, null,
                            "last_message_date DESC");
            if (!conversation.moveToFirst()) return;

            // last_unread_message

            final String[] contactProjection = new String[] { "username" };
            final Cursor contact = getContentResolver()
                    .query(Uri
                            .withAppendedPath(
                                    Uri.parse("content://com.google.android.providers.talk/"),
                                    "contacts"),
                            contactProjection,
                            "last_message_date = "
                                    + conversation.getLong(conversation
                                            .getColumnIndex("last_message_date")),
                            null, null);
            if (!contact.moveToFirst()) return;

            // nickname / username

            try {
                final Intent intent = new Intent(TalkService.this,
                        WorkerService.class);
                intent.setAction(PluginService.ACTION_ANNOUNCE);
                intent.putExtra(WorkerService.EXTRA_FROM,
                        contact.getString(contact
                                .getColumnIndex(contactProjection[0])));
                intent.putExtra(WorkerService.EXTRA_MESSAGE,
                        message.getString(message
                                .getColumnIndex(messageProjection[0])));
                startService(intent);
                
                paused = true;
                handler.postDelayed(new Runnable() {
                    
                    public void run() {
                        paused = false;
                    }
                }, 5000);
            } catch (final Exception e) {
                e.printStackTrace();
            }

            contact.close();
            conversation.close();
            message.close();
        }
    }

    private HandlerThread thread;
    private TalkObserver observer;

    @Override
    public IBinder onBind(final Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getBaseContext()));
        
        thread = new HandlerThread("TalkThread");
        thread.start();
        
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            
            public void run() {
                Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getBaseContext()));
            }
        });
        
        observer = new TalkObserver(handler);

        getContentResolver().registerContentObserver(
                Uri.withAppendedPath(Uri
                        .parse("content://com.google.android.providers.talk/"),
                        "messages"), true, observer);
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);

        thread.getLooper().quit();

        super.onDestroy();
    }
}