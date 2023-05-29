package com.example.d308.UI;

import android.os.Bundle;
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
    private ExcursionAdapter adapter;
    private List<Excursion> excursions;
    private EditText editTextExcursionTitle;
    private Button buttonSaveExcursion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        recyclerView = findViewById(R.id.recyclerViewExcursion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        excursions = new ArrayList<>();
        adapter = new ExcursionAdapter(excursions);
        recyclerView.setAdapter(adapter);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);

        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion();
            }
        });
    }

    private void saveExcursion() {
        String excursionTitle = editTextExcursionTitle.getText().toString().trim();
        if (!excursionTitle.isEmpty()) {
            Excursion newExcursion = new Excursion();
            newExcursion.setTitle(excursionTitle);

            // Get the vacationId from the intent and set it to the newExcursion
            int vacationId = getIntent().getIntExtra("vacationId", -1);
            if (vacationId != -1) {
                newExcursion.setVacationId(vacationId);

                // Get the ExcursionDao and insert the newExcursion
                ExcursionDao excursionDao = AppDatabase.getInstance(this).excursionDao();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        excursionDao.insert(newExcursion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearForm();
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
}
