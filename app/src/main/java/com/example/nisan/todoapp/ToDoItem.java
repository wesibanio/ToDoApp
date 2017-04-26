package com.example.nisan.todoapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by nisan on 3/21/2017.
 */

public class ToDoItem implements Serializable {
    private static final SimpleDateFormat  timeFormat = new SimpleDateFormat("dd-MM-yy");
    private Date dueTime;
    private boolean isChecked;
    private String header;
    private String body;

    public static Map<String, Object> parseItem(ToDoItem item) {
        Map<String, Object> map = new HashMap<>();
        map.put("dueTime", item.getDueTime());
        map.put("isChecked", item.getIsChecked());
        map.put("header", item.getHeader());
        map.put("body", item.getBody());
        return map;
    }

    public ToDoItem() {
    }

    public ToDoItem(String header, String body, Date due) {

        this.dueTime = due;
        this.isChecked = false;
        this.header = header;
        this.body = body;
    }

    public long getDaysToGo() {
        Date now = new Date();
        return TimeUnit.DAYS.convert(this.dueTime.getTime() - now.getTime(),
                        TimeUnit.MILLISECONDS);
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    public Date getDueTime() {
        return dueTime;
    }

    public String getTime() {
        return ToDoItem.timeFormat.format(dueTime);
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }
}
