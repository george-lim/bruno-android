package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;

public class FitnessRecordsFragment extends Fragment {

    private RecyclerView fitnessRecordsList;

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
        setupListView(view);
        return view;
    }

    private void setupListView(View view) {
        // Fitness records list
        fitnessRecordsList = view.findViewById(R.id.recycler_view_fitness_record);
        fitnessRecordsList.setHasFixedSize(true);
        fitnessRecordsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Divider
        DividerItemDecoration itemDecoration = new DividerItemDecoration(fitnessRecordsList.getContext(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = getResources().getDrawable(R.drawable.list_divider, null);
        itemDecoration.setDrawable(dividerDrawable);
        fitnessRecordsList.addItemDecoration(itemDecoration);
        // Data
        int[] data = new int[]{0,1,0,0,1,0,0,0,1,1,1,1,0,0,0}; // dummy
        FitnessRecordsAdapter adapter = new FitnessRecordsAdapter(data);
        fitnessRecordsList.setAdapter(adapter);
    }
}