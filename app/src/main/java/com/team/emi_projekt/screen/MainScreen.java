package com.team.emi_projekt.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.team.emi_projekt.R;
import com.team.emi_projekt.misc.Item;
import com.team.emi_projekt.misc.SheetPreview;
import com.team.emi_projekt.misc.Sheets;
import com.team.emi_projekt.adapter.SheetsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AppCompatActivity {

    private Sheets sheets;
    private ExpandableListView listView;
    private ExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);


        List<SheetPreview> previews = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sheets = (Sheets) getIntent().getSerializableExtra("Sheets");
            previews = sheets.getPreviews();
        }

        listView = (ExpandableListView) findViewById(R.id.sheetsList);
        adapter = new SheetsAdapter(this, previews, sheets);
        listView.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: creation of new item
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //TODO: use onFocus or smth, + get data from addScreen normally
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle b = data.getExtras();
            if (b != null) {
                Item item = (Item) b.getSerializable("Item");
                String sheetLabelText = (String) b.getSerializable("SheetLabel");
                String itemLabelText = (String) b.getSerializable("ItemLabel");
                sheets.setItem(sheetLabelText, itemLabelText, item);
                ((SheetsAdapter) adapter).changeItemPreview(sheetLabelText, itemLabelText, item.getLabel(), item.getComment());
                ((SheetsAdapter) adapter).notifyDataSetChanged();
            }
        }
    }
}
