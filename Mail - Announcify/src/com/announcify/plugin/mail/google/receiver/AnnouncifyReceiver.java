
package com.announcify.plugin.mail.google.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.announcify.plugin.mail.google.service.WorkerService;

public class AnnouncifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Intent service = new Intent(context, WorkerService.class);
        service.setAction(intent.getAction());

        context.startService(service);
    }
}
