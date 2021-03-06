package com.announcify.background.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.announcify.api.background.sql.model.ContactModel;
import com.announcify.api.background.sql.model.GroupModel;
import com.announcify.api.background.sql.model.PluginModel;
import com.announcify.api.background.sql.model.TranslationModel;

public class AnnouncifyDatabase extends SQLiteOpenHelper {

	public AnnouncifyDatabase(final Context context) {
		super(context, "announcify.db", null, 1);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + PluginModel.TABLE_NAME + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ PluginModel.KEY_PLUGIN_PACKAGE + " TEXT NOT NULL,"
				+ PluginModel.KEY_PLUGIN_NAME + " TEXT UNIQUE NOT NULL,"
				+ PluginModel.KEY_PLUGIN_PRIORITY + " INTEGER,"
				+ PluginModel.KEY_PLUGIN_ACTION + " TEXT NOT NULL,"
				+ PluginModel.KEY_PLUGIN_ACTIVE + " INTEGER);");

		db.execSQL("CREATE TABLE " + TranslationModel.TABLE_NAME + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ TranslationModel.KEY_TRANSLATION_FROM + " TEXT UNIQUE,"
				+ TranslationModel.KEY_TRANSLATION_TO + " TEXT NOT NULL,"
				+ TranslationModel.KEY_TRANSLATION_DESTINATION + " TEXT,"
				+ TranslationModel.KEY_TRANSLATION_SOURCE + " TEXT);");

		db.execSQL("CREATE TABLE " + GroupModel.TABLE_NAME + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ GroupModel.KEY_GROUP_ID + " INTEGER UNIQUE NOT NULL,"
				+ GroupModel.KEY_GROUP_TITLE + " TEXT NOT NULL);");

		db.execSQL("CREATE TABLE " + ContactModel.TABLE_NAME + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ContactModel.KEY_CONTACT_ID + " TEXT UNIQUE NOT NULL,"
				+ ContactModel.KEY_CONTACT_TITLE + " TEXT NOT NULL);");

		final ContentValues values = new ContentValues();
		values.put(PluginModel.KEY_PLUGIN_NAME, "Announcify++");
		values.put(PluginModel.KEY_PLUGIN_ACTIVE, 1);
		values.put(PluginModel.KEY_PLUGIN_PRIORITY, 0);
		values.put(PluginModel.KEY_PLUGIN_PACKAGE, "com.announcify");
		values.put(PluginModel.KEY_PLUGIN_ACTION, "com.announcify.SETTINGS");
		db.insert(PluginModel.TABLE_NAME, null, values);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		Log.e("smn", "onUpgrade");
	}
}
