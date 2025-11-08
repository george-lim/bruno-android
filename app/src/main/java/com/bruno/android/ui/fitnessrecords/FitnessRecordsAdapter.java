package com.bruno.android.ui.fitnessrecords;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bruno.android.R;
import com.bruno.android.persistence.FitnessRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsViewHolder> {

    // MARK: - Private members

    private List<FitnessRecord> data;
    private Locale locale;
    private final FitnessRecordsAdapterDelegate delegate;

    // MARK: - Lifecycle methods

    public FitnessRecordsAdapter(FitnessRecordsAdapterDelegate delegate) {
        this.data = new ArrayList<>();
        this.locale = null;
        this.delegate = delegate;
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
        View viewHolderItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_fitness_record, parent, false);
        return new FitnessRecordsViewHolder(viewHolderItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessRecordsViewHolder holder, int position) {
        holder.populate(data.get(position), locale);
        holder.itemView.setOnClickListener(view -> delegate.navigateToFitnessDetails(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
