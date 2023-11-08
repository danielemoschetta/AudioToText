package com.example.daniele.audiototext;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class HomeFragment extends Fragment {

    //graphics
    Button btnRecognize;
    Button btnClear;
    Button btnOpenWhatsapp;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home,container,false);

        btnRecognize = (Button) view.findViewById(R.id.btnRecognize);
        btnRecognize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).Recognize();
                showButtons();
            }
        });

        btnOpenWhatsapp = (Button) view.findViewById(R.id.btnOpenWhatsapp);
        btnOpenWhatsapp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).openWhatsapp();
                showButtons();
            }
        });

        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity)getActivity()).ClearLists();
            }
        });

        listView = (ListView) view.findViewById(R.id.result_list);
        ((MainActivity)getActivity()).adapter = new ArrayAdapter<Result>(getActivity(), android.R.layout.simple_list_item_1,((MainActivity)getActivity()).resultList);
        listView.setAdapter(((MainActivity)getActivity()).adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long id){
                ((MainActivity)getActivity()).copyToClipboard(position);
            }
        });

        showButtons();

        return view;
    }


    private void showButtons() { //change the state of the buttons if an audio file has been shared with the app
        if(((MainActivity)getActivity()).audioShared())
            btnRecognize.setVisibility(View.VISIBLE);
        else
            btnOpenWhatsapp.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_name));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
