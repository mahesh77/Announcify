
package com.announcify;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import greendroid.app.GDApplication;

import android.content.Intent;

import com.announcify.ui.activity.AnnouncifyActivity;

@ReportsCrashes(formKey = "dGlaZGFQcDJNOFgtbUJSRnVnSWpmN3c6MQ")
public class AnnouncifyApplication extends GDApplication {

    @Override
    public Class<?> getHomeActivityClass() {
        return AnnouncifyActivity.class;
    }
    
    @Override
    public Intent getMainApplicationIntent() {
        return new Intent(this, AnnouncifyActivity.class);
    }

    @Override
    public void onCreate() {
        ACRA.init(this);

        super.onCreate();
    }
}
