package com.team.emi_projekt.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.emi_projekt.R;
import com.team.emi_projekt.misc.Item;
import com.team.emi_projekt.misc.SheetPreview;
import com.team.emi_projekt.misc.Sheets;
import com.team.emi_projekt.screen.AddScreen;
import com.team.emi_projekt.screen.MainScreen;

import java.util.List;

public class SheetsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<SheetPreview> sheetPreviews;
    private Sheets sheets;

    public SheetsAdapter(Context context, List<SheetPreview> sheetPreviews, Sheets sheets) {
        this.context = context;
        this.sheetPreviews = sheetPreviews;
        this.sheets = sheets;
    }

    @Override
    public int getGroupCount() {
        return sheetPreviews.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return sheetPreviews.get(groupPosition).getItemNames().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sheetPreviews.get(groupPosition).getSheetName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sheetPreviews.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_screen_list,null);
        }
        TextView sheetLabel = (TextView)convertView.findViewById(R.id.sheetLabel);
        sheetLabel.setTypeface(null, Typeface.BOLD);
        sheetLabel.setText(headerTitle);
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        SheetPreview temp = (SheetPreview)getChild(groupPosition, childPosition);
        final String itemLabelText = temp.getItemNames().get(childPosition);
        final String itemCommentText = temp.getItemComments().get(childPosition);
        final String sheetLabelText = temp.getSheetName();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_screen_item,null);
        }

        LinearLayout itemView = (LinearLayout) convertView.findViewById(R.id.itemView);
        TextView itemLabel = (TextView)convertView.findViewById(R.id.itemLabel);
        TextView itemComment = (TextView)convertView.findViewById(R.id.itemComment);
        itemLabel.setText(itemLabelText);
        itemComment.setText(itemCommentText);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddScreen.class);
                Bundle bundle = new Bundle();
                Item item = sheets.getItem(sheetLabelText, itemLabelText);
                bundle.putSerializable("SheetLabel", sheetLabelText);
                bundle.putSerializable("ItemLabel", itemLabelText);
                bundle.putSerializable("Item", item);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
