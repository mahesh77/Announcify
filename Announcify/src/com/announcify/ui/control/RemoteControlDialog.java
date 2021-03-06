package com.announcify.ui.control;

import org.mailboxer.saymyname.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import com.announcify.background.service.ManagerService;

public class RemoteControlDialog extends Activity {

	public static final String ACTION_CONTINUE = "com.announcify.ACTION_CONTINUE";
	public static final String ACTION_PAUSE = "com.announcify.ACTION_PAUSE";
	public static final String ACTION_SKIP = "com.announcify.ACTION_SKIP";

	private String[] controls;

	private void fireBroadcast(final String action) {
		sendBroadcast(new Intent(action));
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		controls = getResources().getStringArray(R.array.control_items);

		final Builder bob = new AlertDialog.Builder(this);
		bob.setIcon(R.drawable.launcher_icon).setTitle(
				getString(R.string.control_title));
		bob.setItems(controls, new OnClickListener() {

			public void onClick(final DialogInterface dialog, final int which) {
				switch (which) {
				case 0:
					fireBroadcast(ACTION_PAUSE);
					break;
				case 1:
					fireBroadcast(ACTION_CONTINUE);
					break;
				case 2:
					fireBroadcast(ACTION_SKIP);
					break;
				case 3:
					RemoteControlDialog.this.stopService(new Intent(
							RemoteControlDialog.this, ManagerService.class));
					break;
				}

				dialog.dismiss();
				finish();
			}
		});
		bob.setOnCancelListener(new OnCancelListener() {

			public void onCancel(final DialogInterface dialog) {
				dialog.dismiss();
				finish();
			}
		});
		bob.create().show();
	}
}
