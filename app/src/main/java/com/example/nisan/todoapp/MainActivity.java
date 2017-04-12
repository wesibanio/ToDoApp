package com.example.nisan.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static Pattern callPattern = Pattern.compile("(?i)\\s*call\\s*(\\d*)");
    private List<ToDoItem> ToDoItemList;
    private final String TODO_LIST_TAG = "todo_list_tag";
    private final static int ADD_NOTE_REQ_CODE = 1;
    private final static int REM_NOTE_REQ_CODE = 2;
    private ToDoListAdapter adapter;
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

}

