package com.team.emi_projekt;

import android.Manifest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Dimension;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.NamedRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.team.emi_projekt.misc.Item;
import com.team.emi_projekt.misc.Sheets;
import com.team.emi_projekt.misc.SheetsReader;
import com.team.emi_projekt.screen.MainScreen;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    private TextView mOutputText;
    private Button mCallApiButton;
    private Button changeAccount;
    private Sheets sheets;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    //TODO: overwrite onResult
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sheets = SheetsReader.loadSheets(this);

        changeAccount = (Button) findViewById(R.id.changeAccount);

        mOutputText = (TextView) findViewById(R.id.loginText);
        mCallApiButton = (Button) findViewById(R.id.login);

        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCredential.setSelectedAccountName(null);
                getPreferences(Context.MODE_PRIVATE).edit().putString(PREF_ACCOUNT_NAME, null).apply();
                sheets = new Sheets();
                SheetsReader.storeSheets(MainActivity.this, sheets);
                chooseAccount();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            //TODO: change to the dialog window, where u must to turn on the internet or close the app
            mOutputText.setText("No network connection available. Please get an internet connection");
        } else {
            if (Objects.equals(sheets.getPrivateKey(), ""))
                sheets.setPrivateKey(mCredential.getSelectedAccountName());
            new MakeRequestTask(mCredential).execute();
        }
    }

    private void setResults() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            //TODO: change to the dialog window, where u must to turn on the internet or close the app
            mOutputText.setText("No network connection available. Please get an internet connection");
        } else {
            new MakeUploadTask(mCredential).execute();
        }
    }


    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions( this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        sheets.setPrivateKey(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
            case Activity.RESULT_FIRST_USER:
                Sheets temp = SheetsReader.loadSheets(MainActivity.this);
                if (temp != null) {
                    sheets = temp;
                    setResults();
                }
                break;
        }
    }


    /* TODO: add here a switch for offline mode */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(MainActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Boolean> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("EMI_Projekt")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        /**
         * TODO: set spreadsheet generator
         * EMI test spreadsheet
         * https://docs.google.com/spreadsheets/d/1rtX9L-pbCQ4w8NTq96Nh3TBGZ5--8o8E5tIKMjnU0Ug/edit?usp=sharing
         *
         * @return List of names and majors
         * @throws IOException
         */
        private Boolean getDataFromApi() throws IOException {

            String id = "1rtX9L-pbCQ4w8NTq96Nh3TBGZ5--8o8E5tIKMjnU0Ug";
            String range = "A:K"; /* range is A1 notation {%SheetName(first visible if nothing wrote)% ! %from% : %until%} */
            Boolean userExists = false;

            List<Sheet> cloudSheets = this.mService.spreadsheets().get(id).execute().getSheets();
            String tempSheetLabel;

            if (cloudSheets != null)
                for (Sheet temp : cloudSheets) {
                    tempSheetLabel = temp.getProperties().getTitle();
                    if (tempSheetLabel.contains(sheets.getPrivateKey())) {
                        userExists = true;
                        if (!sheets.hasSheet(tempSheetLabel))
                            sheets.addSheet(tempSheetLabel);
                    }
                }

            if (!userExists) {
                String privateSheetLabel = "MyList|" + sheets.getPrivateKey();
                sheets.addSheet(privateSheetLabel);
                sheets.addItem("MyList", new Item("Sugar", "One bag", privateSheetLabel));
            } else {
                sheets.removeAllItems();
                for (String sheet : sheets.getFullLabels()) {
                    ValueRange response = this.mService.spreadsheets().values()
                            .get(id, sheet + "!" + range)
                            .execute();
                    List<List<Object>> values = response.getValues();
                    if (values != null) {
                        for (List row : values) {
                            Item tempItem = new Item(row, sheet);
                            sheets.addItem(sheets.getLabel(sheet), tempItem);
                        }
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPreExecute() {
            mProgress.setMessage("Searching for the account");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            mProgress.hide();
            if (!output) {
                mOutputText.setText("No results returned.");
            } else {
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                SheetsReader.storeSheets(MainActivity.this, sheets);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    private class MakeUploadTask extends AsyncTask<Void, Void, Integer> {

        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeUploadTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("EMI_Projekt")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * TODO: set spreadsheet generator
         * EMI test spreadsheet
         * https://docs.google.com/spreadsheets/d/1rtX9L-pbCQ4w8NTq96Nh3TBGZ5--8o8E5tIKMjnU0Ug/edit?usp=sharing
         *
         * @return List of names and majors
         * @throws IOException
         */
        private Integer getDataFromApi() throws IOException {

            Integer updatedCount = 0;
            String id = "1rtX9L-pbCQ4w8NTq96Nh3TBGZ5--8o8E5tIKMjnU0Ug";
            String range = "A:K"; /* range is A1 notation {%SheetName(first visible if nothing wrote)% ! %from% : %until%} */

            List<Sheet> cloudSheets = this.mService.spreadsheets().get(id).execute().getSheets();
            HashMap<String, Integer> cloudSheetsTitles = new HashMap<>();
            String tempCloudSheetTitle;
            Integer tempCloudSheetId;
            for (Sheet tempCloudSheet : cloudSheets) {
                tempCloudSheetTitle = tempCloudSheet.getProperties().getTitle();
                tempCloudSheetId = tempCloudSheet.getProperties().getSheetId();
                if (tempCloudSheetTitle.contains(sheets.getPrivateKey())) {
                    cloudSheetsTitles.put(tempCloudSheetTitle, tempCloudSheetId);
                    mService.spreadsheets().values().clear(id, tempCloudSheetTitle + "!" + range, new ClearValuesRequest()).execute();
                }
            }

            //Delete sheets
            if (sheets.getLabels() != null) {
                Set<String> sheetLabels = sheets.getFullLabels();
                for (String cloudSheetLabel : cloudSheetsTitles.keySet()) {
                    if (!sheetLabels.contains(cloudSheetLabel)) {
                        List<Request> requests = new ArrayList<Request>();
                        requests.add(new Request()
                                .setDeleteSheet(new DeleteSheetRequest()
                                        .setSheetId(cloudSheetsTitles.get(cloudSheetLabel))));


                        BatchUpdateSpreadsheetRequest body =
                                new BatchUpdateSpreadsheetRequest().setRequests(requests);
                        mService.spreadsheets().batchUpdate(id, body).execute();
                    }
                }
            }

            //Edit/add sheets and values
            if (sheets.getLabels() != null) {
                List<ValueRange> data = new ArrayList<ValueRange>();
                for (String sheetLabel : sheets.getFullLabels()) {
                    if (!cloudSheetsTitles.containsKey(sheetLabel)) {
                        List<Request> requests = new ArrayList<Request>();
                        requests.add(new Request()
                                .setAddSheet(new AddSheetRequest()
                                        .setProperties(new SheetProperties()
                                                .setTitle(sheetLabel))));
                        BatchUpdateSpreadsheetRequest spreadsheetRequestBody =
                                new BatchUpdateSpreadsheetRequest().setRequests(requests);
                        mService.spreadsheets().batchUpdate(id, spreadsheetRequestBody).execute();
                    }

                    List<List<Object>> values = sheets.getItemsData(sheets.getLabel(sheetLabel));
                    data.add(new ValueRange().setRange(sheetLabel + "!" + range).setValues(values));

                    updatedCount++;
                }

                BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                        .setValueInputOption("RAW")
                        .setData(data);
                mService.spreadsheets().values().batchUpdate(id, body).execute();
            }
            return updatedCount;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Integer output) {
            mProgress.hide();
            if (output == null) {
                Toast.makeText(MainActivity.this, "No results Uploaded.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                SheetsReader.storeSheets(MainActivity.this, sheets);
                Toast.makeText(MainActivity.this, "Succeed sync, updated " + output + " Sheet(s)", Toast.LENGTH_LONG).show();
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    mProgress.setMessage("The following error occurred:\n"
                            + mLastError.getMessage());
                    Toast.makeText(MainActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Sync was cancelled", Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing. :) mb change here something?
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing. Same thing :/
    }
}
