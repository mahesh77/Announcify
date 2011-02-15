package com.announcify.ui.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckedTextView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.announcify.R;
import com.announcify.api.background.sql.model.BaseModel;
import com.announcify.api.background.sql.model.ChooserModelInterface;
import com.announcify.api.background.util.AnnouncifySettings;
import com.announcify.api.ui.activity.BaseActivity;


public abstract class ChooserActivity extends BaseActivity {

    protected CheckedTextView checkBlock;
    protected ListView list;
    protected AutoCompleteTextView auto;
    protected SimpleCursorAdapter listAdapter;
    protected SimpleCursorAdapter autoAdapter;
    protected ChooserModelInterface model;
    protected Cursor listCursor;
    protected Cursor autoCursor;
    protected AnnouncifySettings settings;

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
        .getMenuInfo();

        if (item.getItemId() == R.id.menu_remove) {
            model.remove(info.id);

            refreshList();
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle, R.layout.activity_choose);

        settings = new AnnouncifySettings(this);

        checkBlock = (CheckedTextView) findViewById(R.id.check_block);

        listAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                null, 
                null);

        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(listAdapter);
        list.setBackgroundColor(Color.WHITE);
        list.setCacheColorHint(Color.TRANSPARENT);
        list.setFastScrollEnabled(true);

        registerForContextMenu(list);

        autoAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                null, null);

        auto = (AutoCompleteTextView) findViewById(R.id.auto_edit_chooser);
        auto.setSingleLine();
        auto.setAdapter(autoAdapter);
        
        auto.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent,
                    final View view, final int position, final long id) {
                model.add(autoAdapter.getItemId(position), ((TextView) view)
                        .getText().toString());

                refreshList();

                auto.setText("");
            }
        });
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.context_choose, menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshList();
    }

    @Override
    protected void onStop() {
        listCursor.close();
        autoCursor.close();

        super.onStop();
    }

    protected void refreshList() {
        if (listCursor != null) {
            listCursor.close();
        }

        listCursor = model.getAll();
        listAdapter.changeCursor(listCursor);
    }
}