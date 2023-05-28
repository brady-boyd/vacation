package com.example.d308.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308.R;
import com.example.d308.adapters.ExcursionAdapter;
import com.example.d308.entities.Excursion;

import java.util.ArrayList;
import java.util.List;

public class ExcursionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExcursionAdapter adapter;
    private List<Excursion> excursions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        // Assuming you have a RecyclerView in your activity_excursion layout
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your list of excursions (could be empty at this point)
        excursions = new ArrayList<>();

        // Initialize your adapter with the list of excursions
        adapter = new ExcursionAdapter(excursions);

        // Set the adapter on the RecyclerView
        recyclerView.setAdapter(adapter);
    }

    // Method to update the excursions when you have the data
    public void updateExcursions(List<Excursion> newExcursions) {
        excursions = newExcursions;
        adapter.setExcursions(excursions);
        adapter.notifyDataSetChanged();
    }
}
