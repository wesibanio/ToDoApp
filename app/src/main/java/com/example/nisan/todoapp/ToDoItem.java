package com.example.nisan.todoapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by nisan on 3/21/2017.
 */

public class ToDoItem implements Serializable {
    private static final SimpleDateFormat  timeFormat = new SimpleDateFormat("dd-MM-yy");
    private final long daysToGo = 0;
    private Date dueTime;
    private boolean isChecked;
    private String header;
    private String body;



    public ToDoItem(String header, String body, Date due) {

        this.dueTime = due;
        this.isChecked = false;
        this.header = header;
        this.body = body;
    }

    public ToDoItem(byte[] payload) {

    }

    public String getDueTime() {
        return ToDoItem.timeFormat.format(dueTime);
    }

    public long getDaysToGo() {
        Date now = new Date();
        return TimeUnit.DAYS.convert(this.dueTime.getTime() - now.getTime(),
                        TimeUnit.MILLISECONDS);
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
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
