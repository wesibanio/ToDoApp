package com.example.nisan.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class add_item extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                EditText headerView = (EditText) findViewById(R.id.headerText);
                EditText bodyView = (EditText) findViewById(R.id.bodyText);

                intent.putExtra(String.valueOf(R.id.bodyText), bodyView.getText().toString());
                intent.putExtra(String.valueOf(R.id.headerText), headerView.getText().toString());
                setResult(RESULT_OK, intent);
                //startActivity(intent);
                add_item.this.finish();

            }
        });
    }

}
