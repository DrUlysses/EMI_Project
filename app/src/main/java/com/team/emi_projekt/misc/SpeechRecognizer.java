package com.team.emi_projekt.misc;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.team.emi_projekt.MainActivity;
import com.team.emi_projekt.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;


public class SpeechRecognizer implements RecognitionListener {
    private Activity activity;
    //private TextView rootView;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    /* Keyword to activate listener */
    private static final String KEYPHRASE = String.valueOf(R.string.keyphrase);
    /* Words */
    private static final String MENU_SEARCH = String.valueOf(R.string.menu_search);
    private static final String NEW_CARD_SEARCH = String.valueOf(R.string.new_card_search);
    private static final String NEW_LIST_SEARCH = String.valueOf(R.string.new_list_search);
    private static final String ACTUAL_LIST_SEARCH = String.valueOf(R.string.actual_list_search);

    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    public SpeechRecognizer (Activity _activity) {
        this.activity = _activity;
        //this.rootView = (TextView)_activity.findViewById(R.id.caption_text);

        /* Set answers for each sentence */
        captions = new HashMap<>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(NEW_CARD_SEARCH, R.string.new_card_caption);
        captions.put(NEW_LIST_SEARCH, R.string.new_list_caption);
        captions.put(ACTUAL_LIST_SEARCH, R.string.actual_list_caption);
    }
//TODO: Make this crap as it needed to be (better is static)
    void runRecognizerSetup(final MainActivity mainActivity) {
        /* Recognizer initialization is a time-consuming and it involves IO,
         so we execute it in async task */
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(mainActivity);
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
                    //rootView.setText("Failed to init recognizer " + result);
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
        String caption = this.activity.getResources().getString(captions.get(searchName));
        //rootView.setText(caption);
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

        File languageModel = new File(assetsDir, "cmusphinx-voxforge-de.lm.bin");
        recognizer.addNgramSearch(NEW_CARD_SEARCH, languageModel);
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
            switchSearch(NEW_CARD_SEARCH);
        }
        else if (text.equals(NEW_LIST_SEARCH)) {
            recognizer.stop();
            makeText(this.activity.getApplicationContext(), NEW_LIST_SEARCH, Toast.LENGTH_SHORT).show();
        }
        else if (text.equals(ACTUAL_LIST_SEARCH)) {
            recognizer.stop();
            makeText(this.activity.getApplicationContext(), ACTUAL_LIST_SEARCH, Toast.LENGTH_SHORT).show();
        }
        else
            makeText(this.activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
       // ((TextView) this.activity.findViewById(R.id.caption_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(this.activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(Exception e) {
       // ((TextView) this.activity.findViewById(R.id.caption_text)).setText(e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    void onDestroy() {
        recognizer.cancel();
        recognizer.shutdown();
    }
}

