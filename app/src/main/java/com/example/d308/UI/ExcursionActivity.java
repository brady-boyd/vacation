package com.example.d308.UI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308.R;
import com.example.d308.adapters.ExcursionAdapter;
import com.example.d308.dao.ExcursionDao;
import com.example.d308.database.AppDatabase;
import com.example.d308.entities.Excursion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ExcursionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExcursionAdapter excursionAdapter;
    private List<Excursion> excursionList;
    private EditText editTextExcursionTitle;
    private Button buttonSaveExcursion;

    private int vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        recyclerView = findViewById(R.id.recyclerViewExcursion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        excursionList = new ArrayList<>();
        excursionAdapter = new ExcursionAdapter(excursionList);
        recyclerView.setAdapter(excursionAdapter);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);

        vacationId = getIntent().getIntExtra("vacationId", -1);

        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveExcursion() {
        String excursionTitle = editTextExcursionTitle.getText().toString().trim();
        if (!excursionTitle.isEmpty()) {
            Excursion newExcursion = new Excursion();
            newExcursion.setTitle(excursionTitle);

            if (vacationId != -1) {
                newExcursion.setVacationId(vacationId);

                ExcursionDao excursionDao = AppDatabase.getInstance(this).excursionDao();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        excursionDao.insert(newExcursion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearForm();
                                loadExcursions();
                            }
                        });
                    }
                });
            }
        }
    }

    private void clearForm() {
        editTextExcursionTitle.setText("");
    }

    private void loadExcursions() {
        ExcursionDao excursionDao = AppDatabase.getInstance(this).excursionDao();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Excursion> excursions = excursionDao.getAllForVacation(vacationId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        excursionList.clear();
                        excursionList.addAll(excursions);
                        excursionAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
