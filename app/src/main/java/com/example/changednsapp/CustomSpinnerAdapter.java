package com.example.changednsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<UnitData> {
    private final Context context;
    private final List<UnitData> unitDataList;

    public CustomSpinnerAdapter(Context context, List<UnitData> unitDataList) {
        super(context, android.R.layout.simple_spinner_item, unitDataList);
        this.context = context;
        this.unitDataList = unitDataList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view;

        // Get the current item
        UnitData unitData = unitDataList.get(position);
        String unitNo = unitData.getUnitNo();
        String isConfirmed = unitData.isConfirmedValue();
        // Set the text
        textView.setText(unitNo);

        // Change color based on isConfirmed value
        if ("1".equals(isConfirmed)) {
            textView.setBackgroundColor(0xFF00FF00); // Green
        } else if ("0".equals(isConfirmed)) {
            textView.setBackgroundColor(0xFFFFA500); // Orange
        } else {
            textView.setBackgroundColor(0xFFFFFFFF); // Default color (white)
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;

        // Get the current item
        UnitData unitData = unitDataList.get(position);
        String unitNo = unitData.getUnitNo();
        String isConfirmed = unitData.isConfirmedValue();

        // Set the text
        textView.setText(unitNo);

        // Change color based on isConfirmed value
        if ("1".equals(isConfirmed)) {
            textView.setBackgroundColor(0xFF00FF00); // Green
        } else if ("0".equals(isConfirmed)) {
            textView.setBackgroundColor(0xFFFFA500); // Orange
        } else {
            textView.setBackgroundColor(0xFFFFFFFF); // Default color (white)
        }

        return view;
    }

    public UnitData getSelectedUnitData(int position) {
        return unitDataList.get(position);
    }
}
