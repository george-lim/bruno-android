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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.models.FitnessModel;
import com.cs446.group7.bruno.persistence.FitnessRecordData;
import com.cs446.group7.bruno.ui.AppbarFormatter;
import com.cs446.group7.bruno.viewmodels.FitnessRecordsViewModel;
import com.cs446.group7.bruno.viewmodels.FitnessRecordsViewModelDelegate;

import java.util.List;
import java.util.Locale;

public class FitnessRecordsFragment extends Fragment implements FitnessRecordsViewModelDelegate {

    // MARK: - UI components

    private RecyclerView fitnessRecordsRecyclerView;

    // MARK: - Private members

    private FitnessRecordsViewModel viewModel;
    private FitnessRecordsAdapter adapter;

    // MARK: - Lifecycle methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness_records, container, false);
        adapter = new FitnessRecordsAdapter();
        fitnessRecordsRecyclerView = view.findViewById(R.id.recycler_view_fitness_record);
        fitnessRecordsRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final FitnessModel model = new ViewModelProvider(requireActivity()).get(FitnessModel.class);
        viewModel = new FitnessRecordsViewModel(
                getActivity().getApplicationContext(),
                model,
                this
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    // MARK: - FitnessRecordsViewModelDelegate methods

    @Override
    public void setupUI() {
        fitnessRecordsRecyclerView.setHasFixedSize(true);
        fitnessRecordsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fitnessRecordsRecyclerView.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                fitnessRecordsRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );

        Drawable dividerDrawable = getResources().getDrawable(R.drawable.list_divider, null);
        itemDecoration.setDrawable(dividerDrawable);
        fitnessRecordsRecyclerView.addItemDecoration(itemDecoration);

        AppbarFormatter.format(
                (AppCompatActivity) getActivity(),
                getView(),
                R.id.appbar_fitness_records,
                getResources().getString(R.string.title_fitness_records),
                false);
    }

    @Override
    public void setAdapterData(final List<FitnessRecordData> data) {
        adapter.setData(data);
    }

    @Override
    public void setAdapterLocale(final Locale locale) {
        adapter.setLocale(locale);
    }
}
