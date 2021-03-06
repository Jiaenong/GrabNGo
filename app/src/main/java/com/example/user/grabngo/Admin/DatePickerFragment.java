package com.example.user.grabngo.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.user.grabngo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by User on 6/1/2018.
 */

public class DatePickerFragment extends DialogFragment {
    private DatePicker datePicker;
    private EditText editTextExpiredDate;
    private static int number;
    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "com.example.user.piggyplanner.date";

    public  static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public  static DatePickerFragment newInstancePromotion(Date date, int i){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);
        number = i;

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        editTextExpiredDate = (EditText) getActivity().findViewById(R.id.editTextExpiry);

        if(editTextExpiredDate==null){
            if(number==1) {
                editTextExpiredDate = (EditText) getActivity().findViewById(R.id.editTextStartDate);
            }else{
                editTextExpiredDate = (EditText) getActivity().findViewById(R.id.editTextEndDate);
            }
        }

        Date date = new Date();
        if(!editTextExpiredDate.getText().toString().equals("")){
            try {
                date=new SimpleDateFormat("dd/MM/yyyy").parse(editTextExpiredDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker,null);

        datePicker = (DatePicker)v.findViewById(R.id.datePicker);
        datePicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Select Date")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = datePicker.getYear();
                                int month = datePicker.getMonth()+1;
                                int day = datePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,month,day).getTime();
                                editTextExpiredDate.setText(day+"/"+month+"/"+year);
                                sendResult(Activity.RESULT_OK,date);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,null)
                .create();
    }

    private void sendResult(int resultCode, Date date){
        if(getTargetFragment()==null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
