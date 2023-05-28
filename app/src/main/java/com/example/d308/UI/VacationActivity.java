package com.example.d308.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308.R;
import com.example.d308.adapters.VacationAdapter;
import com.example.d308.dao.VacationDao;
import com.example.d308.database.AppDatabase;
import com.example.d308.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class VacationActivity extends AppCompatActivity implements VacationAdapter.VacationClickListener {
    private RecyclerView recyclerView;
    private VacationAdapter adapter;
    private VacationDao vacationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VacationAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAddVacation = findViewById(R.id.fabAddVacation);
        fabAddVacation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationActivity.this, VacationDetailsActivity.class);
                startActivity(intent);
            }
        });

        // Get an instance of the AppDatabase
        AppDatabase database = AppDatabase.getInstance(this);

        // Get the VacationDao from the AppDatabase
        vacationDao = database.vacationDao();

        updateRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // Retrieve all vacations from the database
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<Vacation> vacations = vacationDao.getAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Set the retrieved vacations to the adapter
                        adapter.setVacations(vacations);
                    }
                });
            }
        });
    }

    @Override
    public void onVacationClick(Vacation vacation) {
        Intent intent = new Intent(VacationActivity.this, VacationDetailsActivity.class);
        intent.putExtra("vacation", vacation);
        startActivity(intent);
    }

    @Override
    public void onVacationLongClick(Vacation vacation) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                vacationDao.delete(vacation);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateRecyclerView();
                    }
                });
            }
        });
    }
}
