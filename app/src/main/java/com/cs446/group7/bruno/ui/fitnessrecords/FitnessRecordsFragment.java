package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.ui.AppbarFormatter;

import java.util.Locale;

public class FitnessRecordsFragment extends Fragment {

    private RecyclerView fitnessRecordsRecyclerView;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    private void setupListView(final View view) {
        // Fitness records list
        fitnessRecordsRecyclerView = view.findViewById(R.id.recycler_view_fitness_record);
        fitnessRecordsRecyclerView.setHasFixedSize(true);
        fitnessRecordsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Divider
        DividerItemDecoration itemDecoration = new DividerItemDecoration(fitnessRecordsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = getResources().getDrawable(R.drawable.list_divider, null);
        itemDecoration.setDrawable(dividerDrawable);
        fitnessRecordsRecyclerView.addItemDecoration(itemDecoration);
    }

    private void setupUI() {
        final FitnessRecordsAdapter adapter = new FitnessRecordsAdapter();
        fitnessRecordsRecyclerView.setAdapter(adapter);
    }
}
