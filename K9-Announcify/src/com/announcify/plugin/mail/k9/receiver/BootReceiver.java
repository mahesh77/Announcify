package com.announcify.plugin.mail.k9.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.announcify.plugin.mail.k9.service.MailService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        context.startService(new Intent(context, MailService.class));
    }
}
