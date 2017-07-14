package com.nebula.haseeb.winiff.utils.checklist;

/**
 * Created by haseeb on 4/5/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nebula.haseeb.winiff.R;

public class CustomAdapter extends ArrayAdapter<CheckableListItem> {
    CheckableListItem[] modelItems = null;
    Context context;

    public CustomAdapter(Context context, CheckableListItem[] resource) {
        super(context, R.layout.row, resource);
        this.context = context;
        this.modelItems = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row, parent, false);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.textView1);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    CheckableListItem checkableListItem = (CheckableListItem) cb.getTag();
                    checkableListItem.setValue(cb.isChecked());
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.code.setText(modelItems[position].getName());
        holder.name.setChecked(modelItems[position].getValue());
        holder.name.setTag(modelItems[position]);

        return convertView;
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }
}