package com.team.emi_projekt.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.team.emi_projekt.R;
import com.team.emi_projekt.misc.Item;
import com.team.emi_projekt.misc.SheetPreview;
import com.team.emi_projekt.misc.Sheets;
import com.team.emi_projekt.adapter.SheetsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {

    private Sheets sheets;
    private ExpandableListView listView;
    private ExpandableListAdapter adapter;
    private Button syncButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        syncButton = (Button) findViewById(R.id.syncButton);

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

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().putExtra("Sheets", sheets);
                setResult(Activity.RESULT_FIRST_USER, getIntent());
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, AddScreen.class);
                Bundle bundle = new Bundle();
                //This is bad. Really bad
                Item item = new Item();
                bundle.putSerializable("SheetLabel", "");
                bundle.putSerializable("ItemLabel", "");
                bundle.putSerializable("Item", item);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle b = data.getExtras();
            if (b != null) {
                Item item = (Item) b.getSerializable("Item");
                String sheetLabelText = (String) b.getSerializable("SheetLabel");
                String itemLabelText = (String) b.getSerializable("ItemLabel");
                if (Objects.equals(sheetLabelText, "")) {
                    if (sheets.hasSheet(item.getSheet())) {
                        Item temp = sheets.getItem(item.getSheet(), item.getLabel());
                        if (temp != null) {
                            if (Objects.equals(temp.getComment(), item.getComment()))
                                return;
                            else {
                                sheets.getItem(item.getSheet(), item.getLabel()).merge(item);
                                ((SheetsAdapter) adapter).setPreviews(sheets.getPreviews());
                                ((SheetsAdapter) adapter).notifyDataSetChanged();
                                return;
                            }
                        }
                    } else {
                        sheets.addSheet(item.getSheet());
                    }
                    sheets.addItem(item.getSheet(), item); //here can be bug
                    ((SheetsAdapter) adapter).setPreviews(sheets.getPreviews());
                    ((SheetsAdapter) adapter).notifyDataSetChanged();
                    return;
                } else if (!Objects.equals(item.getSheet(), sheetLabelText)) {
                    sheets.moveItem(item, sheetLabelText);
                    ((SheetsAdapter) adapter).setPreviews(sheets.getPreviews());
                    ((SheetsAdapter) adapter).notifyDataSetChanged();
                    return;
                } else if (!Objects.equals(item.getLabel(), itemLabelText)) {
                    if (sheets.hasItemLabel(item.getLabel(), sheetLabelText)) {
                        sheets.getItem(sheetLabelText, item.getLabel()).merge(item);
                        ((SheetsAdapter) adapter).setPreviews(sheets.getPreviews());
                        ((SheetsAdapter) adapter).notifyDataSetChanged();
                        return;
                    }
                    else {
                        sheets.addItem(sheetLabelText, item);
                        ((SheetsAdapter) adapter).setPreviews(sheets.getPreviews());
                        ((SheetsAdapter) adapter).notifyDataSetChanged();
                        return;
                    }

                } else {
                    Item temp = sheets.getItem(sheetLabelText, itemLabelText);
                    if (!Objects.equals(item.getComment(), temp.getComment()))
                        sheets.getItem(sheetLabelText, itemLabelText).setComment(item.getComment());
                    ((SheetsAdapter) adapter).setPreviews(sheets.getPreviews());
                    ((SheetsAdapter) adapter).notifyDataSetChanged();
                }
            }
        }
    }
}
