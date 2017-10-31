package com.team.emi_projekt;

import android.Manifest;

import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";

    /* Keyword to activate listener */
    private static final String KEYPHRASE = "einkaufsliste";

    /* Words */
    private static final String MENU_SEARCH = "menü";
    private static final String NEW_CARD_SEARCH = "neue karte";
    private static final String NEW_LIST_SEARCH = "neue liste";
    private static final String ACTUAL_LIST_SEARCH = "aktuelle liste";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;

    private HashMap<String, Integer> captions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /* Set answers for each sentence */
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(NEW_CARD_SEARCH, R.string.new_card_caption);
        captions.put(NEW_LIST_SEARCH, R.string.new_list_caption);
        captions.put(ACTUAL_LIST_SEARCH, R.string.actual_list_caption);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizer.startListening(KWS_SEARCH);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        ((TextView) findViewById(R.id.caption_text)).setText("Preparing the recognizer");

        /* Check if user has given permission to record audio */
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        /* Setup voice recognition */
        runRecognizerSetup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
            automatically handle clicks on the Home/Up button, so long
            as you specify a parent activity in AndroidManifest.xml. */
        int id = item.getItemId();

        /* noinspection SimplifiableIfStatement */
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    /* Check permissions */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }

    private void runRecognizerSetup() {
        /* Recognizer initialization is a time-consuming and it involves IO,
         so we execute it in async task */
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text)).setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    /* To change searching string */
    private void switchSearch(String searchName) {
        recognizer.stop();

        /* If we are not spotting, start listening with timeout (10000 ms or 10 seconds). */
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        /* Set answer phrase */
        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        /* The recognizer can be configured to perform multiple searches
            of different kind and switch between them */

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "german"))
                .setDictionary(new File(assetsDir, "german.dic"))
                /* To disable logging of raw audio comment out this call (takes a lot of space on the device) */
                //.setRawLogDir(assetsDir)
                .getRecognizer();
        recognizer.addListener(this);

        /* Create keyword-activation search. */
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        /* Usage example (set the instruction file) */
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
    /* If hear some words */
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        /* Wait for the command after hearing the keyword */
        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else if (text.equals(NEW_CARD_SEARCH)) {
            recognizer.stop();
            makeText(getApplicationContext(), NEW_CARD_SEARCH, Toast.LENGTH_SHORT).show();
        }
        else if (text.equals(NEW_LIST_SEARCH)) {
            recognizer.stop();
            makeText(getApplicationContext(), NEW_LIST_SEARCH, Toast.LENGTH_SHORT).show();
        }
        else if (text.equals(ACTUAL_LIST_SEARCH)) {
            recognizer.stop();
            makeText(getApplicationContext(), ACTUAL_LIST_SEARCH, Toast.LENGTH_SHORT).show();
        }
        else
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.caption_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Exception e) {
        ((TextView) findViewById(R.id.caption_text)).setText(e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }
}
