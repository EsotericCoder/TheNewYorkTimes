package com.esotericcoder.www.thenewyorktimes.fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.esotericcoder.www.thenewyorktimes.R;
import com.esotericcoder.www.thenewyorktimes.model.FilterSetting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FilterSettingFragment extends android.support.v4.app.Fragment
        implements DatePickerDialog.OnDateSetListener {

    private Button saveButton;
    private TextView beginDateTextView;
    private Spinner sortOption;
    private Date selectedDate;
    private int beginDate;
    private CheckBox checkBoxArts;
    private CheckBox checkBoxFashionStyle;
    private CheckBox checkBoxSports;
    private Listener listener;

    public interface Listener {
        void onFilterSettingReceived(FilterSetting filterSetting);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_settings,
                container, false);

        sortOption = view.findViewById(R.id.sort);
        saveButton = view.findViewById(R.id.save_task);
        beginDateTextView = view.findViewById(R.id.begin_date);
        checkBoxArts = view.findViewById(R.id.checkbox_arts);
        checkBoxFashionStyle = view.findViewById(R.id.checkbox_fashion_style);
        checkBoxSports = view.findViewById(R.id.checkbox_sports);

        String[] items = new String[]{"oldest", "newest"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);
        sortOption.setAdapter(adapter);

        beginDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterSetting newFilterSetting = new FilterSetting(beginDate,
                        sortOption.getSelectedItem().toString(),
                        generateNewsDesk());
                if (listener != null) {
                    listener.onFilterSettingReceived(newFilterSetting);
                }
            }
        });

        return view;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private String generateNewsDesk(){
        String checkedDesk = "";

        if(checkBoxArts.isChecked()){
            checkedDesk = "\"Arts\"";
        }

        if(checkBoxSports.isChecked()){
            checkedDesk = checkedDesk + "\"Sports\"";
        }

        if(checkBoxFashionStyle.isChecked()){
            checkedDesk = checkedDesk + "\"Fashion & Style\"";
        }
        return "news_desk:(" + checkedDesk + ")";
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment().setListener(this);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        selectedDate = c.getTime();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat textViewFormat = new SimpleDateFormat("MM/dd/yyyy");
        beginDateTextView.setText(textViewFormat.format(selectedDate));
        beginDate = Integer.parseInt(apiFormat.format(selectedDate));
    }


}

