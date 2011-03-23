package com.announcify.ui.widget;

import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.announcify.R;
import com.announcify.api.background.sql.model.PluginModel;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class SectionedAdapter extends CursorAdapter {

    private static final String SECTIONS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final AlphabetIndexer indexer;
    private final LayoutInflater inflater;
    private final PluginModel model;

    private final int idIndex;

    public SectionedAdapter(final Context context, final PluginModel model, final Cursor cursor) {
        super(context, cursor);

        this.model = model;
        inflater = LayoutInflater.from(context);
        indexer = new AlphabetIndexer(getCursor(), cursor.getColumnIndexOrThrow(PluginModel.KEY_PLUGIN_NAME), SECTIONS);

        idIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final long id = cursor.getLong(idIndex);
        view.setTag(id);

        if ("BbAdMob".equals(model.getName(id))) {
            if (view.findViewById(R.id.admob) == null) {
                ((TextView) view.findViewById(R.id.plugin)).setText("Publisher? Put your ad here!");

                view.findViewById(R.id.separator).setVisibility(View.GONE);
                view.findViewById(R.id.icon).setVisibility(View.GONE);
                view.findViewById(R.id.settings).setVisibility(View.GONE);
                view.findViewById(R.id.check).setVisibility(View.GONE);

                view.findViewById(R.id.plugin).setOnClickListener(new OnClickListener() {

                    public void onClick(final View v) {
                        final Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Announcify - Advertisement");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "tom@announcify.com" });
                        sendIntent.setType("message/rfc822");
                        context.startActivity(sendIntent);
                    }
                });

                final AdRequest ad = new AdRequest();

                final HashSet<String> keywords = new HashSet<String>();
                keywords.add("text-to-voice");
                keywords.add("text-to-speech");
                keywords.add("accessibility");
                keywords.add("eyes-free");
                keywords.add("speech");
                keywords.add("voice");
                keywords.add("announcify");

                final Cursor plugins = model.getAll();
                plugins.moveToFirst();

                final int nameId = plugins.getColumnIndex(PluginModel.KEY_PLUGIN_NAME);
                do {
                    keywords.add(plugins.getString(nameId));
                } while (plugins.moveToNext());
                keywords.remove("BbAdMob");

                ad.setKeywords(keywords);

                final AdView adView = new AdView((Activity) context, AdSize.BANNER, "a14d81b0baa0b54");
                adView.setId(R.id.admob);
                ((LinearLayout) view).addView(adView);
                adView.setAdListener(new AdListener() {

                    public void onReceiveAd(final Ad arg0) {
                        view.findViewById(R.id.plugin_info).setVisibility(View.GONE);
                    }

                    public void onPresentScreen(final Ad arg0) {
                    }

                    public void onLeaveApplication(final Ad arg0) {
                    }

                    public void onFailedToReceiveAd(final Ad arg0, final ErrorCode arg1) {
                        // TODO: ...
                    }

                    public void onDismissScreen(final Ad arg0) {
                        final Builder builder = new Builder(context);
                        builder.setCancelable(true);
                        builder.setTitle(context.getString(R.string.dialog_pro_title));
                        builder.setMessage(context.getString(R.string.dialog_pro_message));
                        builder.setNegativeButton(context.getString(R.string.dialog_pro_cancel), null);
                        builder.setPositiveButton(context.getString(R.string.dialog_pro_ok), new DialogInterface.OnClickListener() {

                            public void onClick(final DialogInterface dialog, final int which) {
                                final CharSequence[] items = { "Android Market", "Flattr", "PayPal" };

                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(context.getString(R.string.dialog_pro_title));
                                builder.setItems(items, new DialogInterface.OnClickListener() {

                                    public void onClick(final android.content.DialogInterface dialog, final int item) {
                                        // TODO: change links
                                        if (items[0].equals(items[item])) {
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/p4jH2")));
                                        } else if (items[1].equals(items[item])) {
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/fhecu")));
                                        } else {
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/1e8K9")));
                                        }
                                    }
                                });
                                builder.create().show();

                                // TODO: donate-dialog like in
                                // openoffice document reader
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/p4jH2")));
                            }
                        });
                        builder.create().show();
                    }
                });
                adView.loadAd(ad);
            } else {
                view.findViewById(R.id.plugin_info).setVisibility(View.GONE);
                view.findViewById(R.id.separator).setVisibility(View.GONE);
                view.findViewById(R.id.icon).setVisibility(View.GONE);
                view.findViewById(R.id.admob).setVisibility(View.VISIBLE);
                view.findViewById(R.id.settings).setVisibility(View.GONE);
                view.findViewById(R.id.plugin).setVisibility(View.GONE);
                view.findViewById(R.id.check).setVisibility(View.GONE);

                view.findViewById(R.id.plugin).setOnClickListener(new OnClickListener() {

                    public void onClick(final View v) {
                        final Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Announcify - Advertisement");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
                        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "tom@announcify.com" });
                        sendIntent.setType("message/rfc822");
                        context.startActivity(sendIntent);
                    }
                });
            }

            return;
        }

        view.findViewById(R.id.icon).setVisibility(View.VISIBLE);
        view.findViewById(R.id.separator).setVisibility(View.VISIBLE);
        view.findViewById(R.id.settings).setVisibility(View.VISIBLE);
        view.findViewById(R.id.check).setVisibility(View.VISIBLE);
        view.findViewById(R.id.plugin).setVisibility(View.VISIBLE);
        if (view.findViewById(R.id.admob) != null) {
            view.findViewById(R.id.admob).setVisibility(View.GONE);
        }
        view.findViewById(R.id.plugin_info).setVisibility(View.VISIBLE);

        ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(context.getResources().getDrawable(R.drawable.launcher_icon));

        ((TextView) view.findViewById(R.id.plugin)).setText(model.getName(id));

        final ImageView check = (ImageView) view.findViewById(R.id.check);
        check.setTag(id);
        check.setImageDrawable(context.getResources().getDrawable(model.getActive(id) ? R.drawable.btn_check_buttonless_on : R.drawable.btn_check_buttonless_off));
        check.setOnClickListener(new OnClickListener() {

            public void onClick(final View v) {
                model.togglePlugin((Long) v.getTag());

                check.setImageDrawable(context.getResources().getDrawable(model.getActive(id) ? R.drawable.btn_check_buttonless_on : R.drawable.btn_check_buttonless_off));
            }
        });

//        view.setOnClickListener(new OnClickListener() {
//
//            public void onClick(final View v) {
//                context.startActivity(new Intent(model.getAction((Long) v.getTag())));
//            }
//        });
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        final int section = indexer.getSectionForPosition(position);

        final TextView sectionView = (TextView) convertView.findViewById(R.id.section);
        if (indexer.getPositionForSection(section) == position) {
            sectionView.setBackgroundColor(Color.parseColor("#AD0000"));
            sectionView.setText(indexer.getSections()[section].toString().trim());
            sectionView.setVisibility(View.VISIBLE);
        } else {
            sectionView.setVisibility(View.GONE);
            sectionView.setText(null);
        }

        return convertView;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        return inflater.inflate(com.announcify.R.layout.list_item_plugin, parent, false);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        indexer.onChanged();
    }
}
