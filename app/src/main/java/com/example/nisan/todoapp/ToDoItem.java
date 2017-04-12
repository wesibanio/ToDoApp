package com.example.nisan.todoapp;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nisan on 3/21/2017.
 */

public class ToDoItem implements Serializable {
    private static final SimpleDateFormat  timeFormat = new SimpleDateFormat("HH:mm:ss\ndd-MM-yy");
    private String createdTime;
    private boolean isChecked;
    private String header;
    private String body;



    public ToDoItem(String header, String body) {
        Date now = new Date();
        createdTime = ToDoItem.timeFormat.format(now);

        this.isChecked = false;
        this.header = header;
        this.body = body;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

}
