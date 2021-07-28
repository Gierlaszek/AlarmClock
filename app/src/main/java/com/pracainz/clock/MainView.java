package com.pracainz.clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

public class MainView extends Fragment {


    private NumberPicker numPickHour;
    private NumberPicker numPickMinute;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main_view, container, false);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickButton(v);
            }
        });

        int maxNumHour = 23;
        int maxNumMinute = 59;

        numPickHour = view.findViewById(R.id.NumPickHour);
        numPickMinute = view.findViewById(R.id.NumPickMin);

        numPickHour.setMaxValue(maxNumHour);
        numPickMinute.setMaxValue(maxNumMinute);
        numPickHour.setMinValue(0);
        numPickMinute.setMinValue(0);

        numPickHour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        numPickMinute.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });


        return view;
    }

    //-----------------
    //save setting time
    //start service
    //-----------------
    public void onClickButton(View view){

        String savedTime = "";
        try{

            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Shared", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            savedTime = filterTime(numPickHour.getValue(), numPickMinute.getValue());

            edit.putString("SavedTime", savedTime);
            edit.apply();

            getActivity().stopService(new Intent(getActivity(), MyService.class));
            getActivity().startService(new Intent(getActivity(), MyService.class));

        }
        catch (NullPointerException e){
            System.out.println("NullPointerException: " +  e.getLocalizedMessage());
        }

    }

    private String filterTime(int hour, int minute){

        String correctTime;

        if(hour < 10){
            correctTime = "0" + hour + ":";
        }
        else{
            correctTime = hour + ":";
        }

        if(minute < 10){
            correctTime += "0" + minute;
        }
        else{
            correctTime += "" + minute;
        }

        return correctTime;
    }
}