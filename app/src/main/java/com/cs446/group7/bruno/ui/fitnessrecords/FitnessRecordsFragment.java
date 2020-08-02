package com.cs446.group7.bruno.ui.fitnessrecords;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.dao.FitnessSessionData;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.viewmodels.FitnessRecordsViewModel;
import com.cs446.group7.bruno.viewmodels.FitnessRecordsViewModelDelegate;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FitnessRecordsFragment extends Fragment implements FitnessRecordsViewModelDelegate {

    private RecyclerView fitnessRecordsList;
    private FitnessRecordsViewModel viewModel;

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

        final FitnessModel model = new ViewModelProvider(requireActivity()).get(FitnessModel.class);
        viewModel = new FitnessRecordsViewModel(getActivity().getApplicationContext(), model,this);
    }

    private void setupListView(final View view) {
        // Fitness records list
        fitnessRecordsList = view.findViewById(R.id.recycler_view_fitness_record);
        fitnessRecordsList.setHasFixedSize(true);
        fitnessRecordsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Divider
        DividerItemDecoration itemDecoration = new DividerItemDecoration(fitnessRecordsList.getContext(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = getResources().getDrawable(R.drawable.list_divider, null);
        itemDecoration.setDrawable(dividerDrawable);
        fitnessRecordsList.addItemDecoration(itemDecoration);
    }

    @Override
    public void setupUI(final List<FitnessSessionData> detailsDAOList, final Locale locale) {
        final FitnessRecordsAdapter adapter = new FitnessRecordsAdapter(detailsDAOList, locale);
        fitnessRecordsList.setAdapter(adapter);
    }
}
