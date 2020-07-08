package com.cs446.group7.bruno.ui.fitnessrecords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;

public class FitnessRecordsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fitness_records, container, false);
        AppbarFormatter.format(
                (AppCompatActivity) getActivity(),
                view,
                R.id.appbar_fitness_records,
                getResources().getString(R.string.title_fitness_records),
                false);

        RecyclerView fitnessRecordsList = view.findViewById(R.id.recycler_view_fitness_record);
        fitnessRecordsList.setHasFixedSize(true);
        fitnessRecordsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        int[] data = new int[]{0,1,0,0,1,0,0,0,1,1,1,1,0,0,0};
        FitnessRecordsAdapter adapter = new FitnessRecordsAdapter(data);
        fitnessRecordsList.setAdapter(adapter);

        return view;
    }
}