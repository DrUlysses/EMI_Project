package com.team.emi_projekt.screen;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.team.emi_projekt.R;
import com.team.emi_projekt.misc.Item;

import java.util.Date;

public class AddScreen extends AppCompatActivity {

    private Item item;
    private String itemLabelText; //previous one
    private String sheetLabelText;

    private EditText
        editItemLabel,
        editItemComment;
    private Button
        saveEditing,
        cancelEditing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_screen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            item = (Item)getIntent().getSerializableExtra("Item");
            itemLabelText = (String)getIntent().getSerializableExtra("ItemLabel");
            sheetLabelText = (String)getIntent().getSerializableExtra("SheetLabel");
        }

        InitializeActivity();
    }

    private void InitializeActivity() {
        editItemLabel = (EditText) findViewById(R.id.editItemLabel);
        editItemComment = (EditText) findViewById(R.id.editItemComment);
        saveEditing = (Button) findViewById(R.id.saveEditing);
        cancelEditing = (Button) findViewById(R.id.cancelEditing);

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

        cancelEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void applyEditing() {
        item.setLabel(editItemLabel.getText().toString());
        item.setComment(editItemComment.getText().toString());
        item.setLastAdded(new Date());
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
