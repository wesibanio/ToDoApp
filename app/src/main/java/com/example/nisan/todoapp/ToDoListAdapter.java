package com.example.nisan.todoapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nisan on 3/21/2017.
 */

public class ToDoListAdapter extends ArrayAdapter<ToDoItem> {

    private List<ToDoItem> currItemList;

    public ToDoListAdapter(Context context, int resource, List<ToDoItem> objects) {
        super(context, resource, objects);
        currItemList = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.todo_item, parent, false);
        }

        final ToDoItem currItem = currItemList.get(position);

        TextView header = (TextView) convertView.findViewById(R.id.item_header);
        TextView body = (TextView) convertView.findViewById(R.id.item_body);
        CheckBox checked = (CheckBox) convertView.findViewById(R.id.item_check);
        TextView date = (TextView) convertView.findViewById(R.id.item_date);

        header.setText(currItem.getHeader());
        body.setText(currItem.getBody());
        checked.setChecked(currItem.getIsChecked());
        date.setText(currItem.getTime());

        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currItem.setChecked(isChecked);
            }
        });
        return convertView;
    }
}
