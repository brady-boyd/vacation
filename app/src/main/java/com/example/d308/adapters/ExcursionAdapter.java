package com.example.d308.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308.R;
import com.example.d308.dao.ExcursionDao;
import com.example.d308.entities.Excursion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> excursions;
    private ExcursionDao excursionDao;

    public ExcursionAdapter(List<Excursion> excursions) {
        this.excursions = excursions;
    }

    public ExcursionAdapter(ExcursionDao dao) {
        this.excursions = new ArrayList<>();
        this.excursionDao = dao;
    }

    public ExcursionAdapter() {
        this(new ArrayList<>());
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.excursion_item_layout, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExcursionViewHolder holder, int position) {
        Excursion excursion = excursions.get(position);
        // use the corrected TextView from ViewHolder
        holder.titleTextView.setText(excursion.getTitle());
    }



    @Override
    public int getItemCount() {
        return excursions.size();
    }

    public void loadExcursions(int vacationId) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Excursion> excursions = excursionDao.getAllForVacation(vacationId);
                setExcursions(excursions);
            }
        });
    }


    public static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            // this should match the TextView id in your excursion_item_layout
            titleTextView = itemView.findViewById(R.id.textViewExcursion);
        }
    }
}
