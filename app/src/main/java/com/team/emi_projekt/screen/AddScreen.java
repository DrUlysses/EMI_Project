package com.team.emi_projekt.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.team.emi_projekt.R;
import com.team.emi_projekt.misc.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddScreen extends AppCompatActivity {

    private Item item;
    private String itemLabelText; //previous one
    private String sheetLabelText;
    private String fullSheetLabelText;

    private Integer anotherOwnersCount;

    private LinearLayout
        layout;
    private EditText
        editSheetLabel,
        editItemLabel,
        editItemComment;
    private Button
        saveEditing,
        cancelEditing,
        deleteItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //TODO: add AutoCompleteTextView for Sheets TextView
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_screen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            item = (Item)getIntent().getSerializableExtra("Item");
            itemLabelText = (String)getIntent().getSerializableExtra("ItemLabel");
            sheetLabelText = (String)getIntent().getSerializableExtra("SheetLabel");
            fullSheetLabelText = (String)getIntent().getSerializableExtra("FullSheetLabel");
        }

        InitializeActivity();
    }

    private void InitializeActivity() {
        layout = (LinearLayout) findViewById(R.id.linearLayout);

        editSheetLabel = (EditText) findViewById(R.id.editSheetLabel);
        editSheetLabel.setText(sheetLabelText);

        saveEditing = (Button) findViewById(R.id.saveEditing);

        cancelEditing = (Button) findViewById(R.id.cancelEditing);
        cancelEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteItem = (Button) findViewById(R.id.deleteEditedItem);

        if (fullSheetLabelText == null) {
            Button shareButton = (Button) findViewById(R.id.shareButton);
            shareButton.setVisibility(View.VISIBLE);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(Intent.ACTION_SEND);
                    newIntent.setType("text/plain");
                    String shareBody = "Can you please buy " + editItemLabel.getText().toString() + " (" + editItemComment.getText().toString() + ") ?";
                    String shareSub = "ToBuy";
                    newIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    newIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(newIntent, "Share using"));
                }
            });

            editItemLabel = (EditText) findViewById(R.id.editItemLabel);
            editItemLabel.setVisibility(View.VISIBLE);
            editItemComment = (EditText) findViewById(R.id.editItemComment);
            editItemComment.setVisibility(View.VISIBLE);

            editItemLabel.setText(item.getLabel());
            editItemComment.setText(item.getComment());

            saveEditing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyEditing();

                    getIntent().putExtra("Item", item);
                    getIntent().putExtra("ItemLabel", itemLabelText);
                    getIntent().putExtra("SheetLabel", sheetLabelText);

                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            });


            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyEditing();
                    item.setLabel("");
                    item.setSheet("");

                    getIntent().putExtra("Item", item);
                    getIntent().putExtra("ItemLabel", itemLabelText);
                    getIntent().putExtra("SheetLabel", sheetLabelText);

                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            });
        } else {
            //TODO: rework the whole system of sheet labels
            List<String> fullSheetLabel = new ArrayList<String>(Arrays.asList(fullSheetLabelText.split("\\|")));
            anotherOwnersCount = fullSheetLabel.size() - 2;

            for (int i = 0; i < anotherOwnersCount; i++)
                addNewOwnerEditView(i, fullSheetLabel.get(i + 2));

            final TextView userName = (TextView) findViewById(R.id.userName);
            userName.setVisibility(View.VISIBLE);
            userName.setText(fullSheetLabel.get(1));

            Button addOwnerButton = (Button) findViewById(R.id.addOwnerButton);
            addOwnerButton.setVisibility(View.VISIBLE);

            addOwnerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewOwnerEditView(anotherOwnersCount++, "");
                }
            });

            saveEditing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getIntent().putExtra("SheetLabel", sheetLabelText);
                    getIntent().putExtra("FullSheetLabel",  editSheetLabel.getText().toString() + "|" + userName.getText().toString() + getAnotherOwnersNames());

                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            });


            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getIntent().putExtra("SheetLabel", sheetLabelText);
                    getIntent().putExtra("FullSheetLabel", "");

                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            });
        }
    }

    private void applyEditing() {
        item.setLabel(editItemLabel.getText().toString());
        item.setComment(editItemComment.getText().toString());
        item.setSheet(editSheetLabel.getText().toString());
        item.setLastAdded(new Date());
    }

    private void addNewOwnerEditView(Integer id, String name) {
        final EditText anotherOwnerName = new EditText(this);
        anotherOwnerName.setId(id);
        anotherOwnerName.setHint("Friends E-Mail");
        if (!Objects.equals(name, ""))
            anotherOwnerName.setText(name);
        anotherOwnerName.setWidth(366);
        anotherOwnerName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        anotherOwnerName.setPadding(8,15,8,8);

        anotherOwnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(anotherOwnerName);
                anotherOwnersCount--;
            }
        });
        layout.addView(anotherOwnerName);
    }

    private String getAnotherOwnersNames() {
        String result = "";
        for (int i = 0; i < anotherOwnersCount; i++) {
            EditText temp = (EditText) findViewById(i);
            result += "|" + temp.getText().toString();
        }
        return result;
    }

//    SpeechRecognizer recognizer;
//    recognizer = new SpeechRecognizer(this);
//
//        /* Setup voice recognition */
//       recognizer.runRecognizerSetup(MainActivity.this);
//
//               this.recognizer.onDestroy();
//      recognizer.runRecognizerSetup(MainActivity.this);


}
