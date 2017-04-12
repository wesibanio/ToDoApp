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
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are you sure you want to delete?")
                        .setTitle("Delete Item")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v("click!", "clieckckck");
                                ToDoItemList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ADD_NOTE_REQ_CODE) {

            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }

            String newHeader = extras.getString(String.valueOf(R.id.headerText));
            String newBody = extras.getString(String.valueOf(R.id.bodyText));
            ToDoItemList.add(new ToDoItem(newHeader, newBody));
            adapter.notifyDataSetChanged();

        }
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
                Intent intent = new Intent();
                EditText headerView = (EditText) dialog.findViewById(R.id.headerText);
                EditText bodyView = (EditText) dialog.findViewById(R.id.bodyText);

                String newBody = bodyView.getText().toString();
                String newHeader = headerView.getText().toString();

                if (newHeader.equals("")) {
                    newHeader = "No Subject";
                    if (newBody.equals("")) {
                        dialog.cancel();
                    }
                }
                ToDoItemList.add(new ToDoItem(newHeader, newBody));
                adapter.notifyDataSetChanged();
                dialog.cancel();

            }
        });
    }

}

