package com.cs446.group7.bruno.ui.fitnessrecords;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cs446.group7.bruno.R;

public class FitnessRecordsAdapter extends RecyclerView.Adapter<FitnessRecordsAdapter.FitnessRecordViewHolder> {
    private int[] data;

    public FitnessRecordsAdapter(final int[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public FitnessRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_fitness_record, parent, false);
        return new FitnessRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessRecordViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmenttoplevel_to_fragmentfitnessdetails);
        });
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class FitnessRecordViewHolder extends RecyclerView.ViewHolder {

        public FitnessRecordViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
