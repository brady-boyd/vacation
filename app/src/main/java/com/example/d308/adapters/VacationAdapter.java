package com.example.d308.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308.R;
import com.example.d308.entities.VacationWithExcursions;

import java.util.ArrayList;
import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<VacationWithExcursions> vacations;
    private VacationClickListener listener;

    public VacationAdapter(VacationClickListener listener) {
        this.vacations = new ArrayList<>();
        this.listener = listener;
    }

    public void setVacations(List<VacationWithExcursions> vacations) {
        this.vacations.clear();
        this.vacations.addAll(vacations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        VacationWithExcursions vacation = vacations.get(position);
        holder.titleTextView.setText(vacation.vacation.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVacationClick(vacation);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onVacationLongClick(vacation);
                return true;
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return vacations.get(position).vacation.getId(); // Assuming Vacation class has a getId() method
    }

    @Override
    public int getItemCount() {
        return vacations.size();
    }

    public static class VacationViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }

    public interface VacationClickListener {
        void onVacationClick(VacationWithExcursions vacation);
        void onVacationLongClick(VacationWithExcursions vacation);
    }
}
