package com.cs446.group7.bruno.ui.fitnessrecords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;
import com.cs446.group7.bruno.persistence.FitnessRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsViewHolder> {

    // MARK: - Private members

    private List<FitnessRecord> data;
    private Locale locale;

    // MARK: - Lifecycle methods

    public FitnessRecordsAdapter() {
        this.data = new ArrayList<>();
        this.locale = null;
    }

    // MARK: - Public methods

    public void setData(final List<FitnessRecord> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    // MARK: - RecyclerView.ViewHolder methods

    @NonNull
    @Override
    public FitnessRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_fitness_record, parent, false);
        return new FitnessRecordsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessRecordsViewHolder holder, int position) {
        holder.populate(data.get(position), locale);
        holder.itemView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("recordIndex", position);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
