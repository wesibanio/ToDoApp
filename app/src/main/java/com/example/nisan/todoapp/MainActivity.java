package com.example.nisan.todoapp;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.nfc.NdefRecord.createMime;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private static Pattern callPattern = Pattern.compile("(?i)\\s*call\\s*(\\d*)");
    private List<ToDoItem> ToDoItemList;
    private final String TODO_LIST_TAG = "todo_list_tag";
    private final static int ADD_NOTE_REQ_CODE = 1;
    private final static int REM_NOTE_REQ_CODE = 2;
    private ToDoListAdapter adapter;

    private NfcAdapter mNfcAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddDialog();
            }
        });

        if (savedInstanceState != null) {
            ToDoItemList = (List<ToDoItem>) savedInstanceState.getSerializable(this.TODO_LIST_TAG);
        } else {
            ToDoItemList = new ArrayList<>();
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
        } else {
            mNfcAdapter.setNdefPushMessageCallback(this, this);


            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                readNfc(getIntent());
            }
        }

        ListView ToDoView = (ListView) findViewById(R.id.listView);
        adapter = new ToDoListAdapter(this, R.layout.todo_item, ToDoItemList);
        ToDoView.setAdapter(adapter);


        ToDoView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Which Action Would You Like To Perform?")
                        .setTitle("Option List")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setPositiveButton("Delete Item", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToDoItemList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });

                final Matcher callMatch = callPattern.matcher(ToDoItemList.get(position).getHeader());

                if (callMatch.find() && callMatch.group(1).length() > 0) {
                    dialog.setNeutralButton("Make The Call", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent dial = new Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:" + callMatch.group(1)));
                            startActivity(dial);
                            dialog.cancel();
                        }
                    });
                }
                dialog.show();

                return true;
            }

        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_add) {
            startAddDialog();
            //Intent intent = new Intent(MainActivity.this, add_item.class);
            //startActivityForResult(intent, ADD_NOTE_REQ_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(this.TODO_LIST_TAG, (Serializable) ToDoItemList);
    }

    private void startAddDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(R.layout.activity_add_item)
                .show();

        FloatingActionButton fab = (FloatingActionButton) dialog.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText headerView = (EditText) dialog.findViewById(R.id.headerText);
                EditText bodyView = (EditText) dialog.findViewById(R.id.bodyText);

                String newBody = bodyView.getText().toString();
                String newHeader = headerView.getText().toString();

                if (newHeader.equals("")) {
                    newHeader = "No Subject";
                    if (newBody.equals("")) {
                        dialog.cancel();
                        return;
                    }
                }

                DatePicker dateView = (DatePicker) dialog.findViewById(R.id.dueDate);

                Calendar cal = Calendar.getInstance();
                cal.set(dateView.getYear(), dateView.getMonth(), dateView.getDayOfMonth());

                ToDoItemList.add(new ToDoItem(newHeader, newBody, cal.getTime()));
                adapter.notifyDataSetChanged();
                dialog.cancel();

            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) dialog.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                return;
            }
        });
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        NdefRecord[] records = new NdefRecord[ToDoItemList.size()];
        for (int i = 0; i < records.length; i++) {
            records[i] = createMime("application/vnd.com.example.android.beam",
                    ToDoItemList.get(i).getBody().getBytes());
        }

        NdefMessage msg = new NdefMessage(records);
        return msg;
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter == null) {
            return;
        }
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
                new IntentFilter[] {ndef, },
                new String[][] { new String[] { NfcF.class.getName() }} );
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            readNfc(getIntent());
        }
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())){
            Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage toSend = parseListToNdef(tag);

            try {
                Ndef ndefTag = Ndef.get(tag);
                ndefTag.connect();
                ndefTag.writeNdefMessage(toSend);
                ndefTag.close();
            } catch (IOException | FormatException e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "you've just shared your list!", Toast.LENGTH_LONG ).show();
        }
    }

    private NdefMessage parseListToNdef(Parcelable parcelableExtra) {
        NdefRecord[] records = new NdefRecord[ToDoItemList.size()];
        for (int i = 0; i < records.length; i++) {
            records[i] = createRecord(ToDoItemList.get(i).getBody());
        }
        return new NdefMessage(records);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * parses the recieved ndef msg and switches content
     * @param intent calling intent
     */
    private void readNfc(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        if ("text/plain".equals(intent.getType())) {
            ToDoItemList.add(new ToDoItem("---Input Stream Notes ---".getBytes()));
        } else {
            ToDoItemList.clear();
        }
        NdefRecord[] records = msg.getRecords();
        for (int i = 0; i < records.length; i++) {
            ToDoItemList.add(new ToDoItem(records[i].getPayload()));
        }
    }
    private NdefRecord createRecord(String content) {
        try {
            // Get UTF-8 byte
            byte[] lang = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8"); // Content in UTF-8

            int langSize = lang.length;
            int textLength = text.length;

            ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + langSize + textLength);
            payload.write((byte) (langSize & 0x1F));
            payload.write(lang, 0, langSize);
            payload.write(text, 0, textLength);
            return (new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT, new byte[0],
                    payload.toByteArray()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}


