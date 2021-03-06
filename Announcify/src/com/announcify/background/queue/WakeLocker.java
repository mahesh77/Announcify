package com.announcify.background.queue;

import android.content.Context;
import android.os.PowerManager;

public class WakeLocker {

	private static PowerManager.WakeLock lock;

	public static void getWakeLocker(final Context context) {
		final PowerManager mgr = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		lock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"com.announcify.WAKE_LOCK");
	}

	public static boolean isLocked() {
		if (lock == null) {
			return false;
		} else {
			return lock.isHeld();
		}
	}

	public static void lock(final Context context) {
		if (lock == null) {
			getWakeLocker(context);
		}

		if (!isLocked()) {
			lock.acquire();
		}
	}

	public static void unlock() {
		if (lock != null && isLocked()) {
			lock.release();
		}
	}
}
