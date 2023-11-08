package com.example.daniele.audiototext;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    Spinner spinner;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings,container,false);

        spinner=(Spinner)view.findViewById(R.id.spinner);

        spinner.setSelection(getSelected());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int position, long id) {
                //setting a preference that can be saved when the application close
                SharedPreferences.Editor editor = getContext().getSharedPreferences("pref", MODE_PRIVATE).edit();
                editor.putString("translationLanguage",adapter.getItemAtPosition(position).toString());
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView){}
        });

        return view;
    }

    private int getSelected() {
        //setting the previously selected language
        SharedPreferences pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        for(int i=0;i<spinner.getCount();i++){
            if(spinner.getItemAtPosition(i).toString().equals(pref.getString("translationLanguage","it-IT")))
                return i;
        }
        return 0;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getResources().getString(R.string.nav_settings) + " - " + getString(R.string.app_name));
        super.onViewCreated(view, savedInstanceState);
    }
}
