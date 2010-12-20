package com.announcify.plugin.talk.service;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

public class TalkService extends WorkerService {
	private HandlerThread thread;
	private TalkObserver observer;

	@Override
	public void onCreate() {
		thread = new HandlerThread("TalkThread");
		observer = new TalkObserver(new Handler(thread.getLooper()));

		getContentResolver().registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "messages"), true, observer);
	}

	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(observer);

		thread.getLooper().quit();

		super.onDestroy();
	}

	private class TalkObserver extends ContentObserver {
		public TalkObserver(final Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(final boolean selfChange) {
			final String[] messageProjection = new String[] {"body"};
			final Cursor message = getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "messages"), messageProjection, "err_code = 0", null, "date DESC");
			message.moveToFirst();

			// body

			final String[] conversationProjection = new String[] {"last_unread_message"};
			final Cursor conversation = getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/"), "chats"), conversationProjection, null, null, "last_message_date DESC");
			conversation.moveToFirst();

			// last_unread_message

			final String[] contactProjection = new String[] {"username"};
			final Cursor contact = getContentResolver().query(Uri.withAppendedPath(Uri.parse("content://com.google.android.providers.talk/contacts"), "1"), contactProjection, "last_message_date = " + conversation.getLong(conversation.getColumnIndex("last_message_date")), null, null);
			contact.moveToFirst();

			// nickname / username

			final Intent intent = new Intent(TalkService.this, WorkerService.class);
			intent.putExtra(WorkerService.EXTRA_FROM, contact.getString(contact.getColumnIndex(contactProjection[0])));
			intent.putExtra(WorkerService.EXTRA_SUBJECT, message.getString(message.getColumnIndex(messageProjection[0])));
			startService(intent);

			contact.close();
			conversation.close();
			message.close();
		}
	}

	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}
}