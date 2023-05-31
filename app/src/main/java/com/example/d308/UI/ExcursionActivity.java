package com.example.d308.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.d308.R;
import com.example.d308.dao.ExcursionDao;
import com.example.d308.database.AppDatabase;
import com.example.d308.entities.Excursion;

import java.util.concurrent.Executors;

public class ExcursionActivity extends AppCompatActivity {
    private EditText editTextExcursionTitle;
    private Button buttonSaveExcursion;
    private Button buttonDeleteExcursion;

    private int excursionId;
    private ExcursionDao excursionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);
        buttonDeleteExcursion = findViewById(R.id.buttonDeleteExcursion);

        buttonDeleteExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExcursion();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        AppDatabase database = AppDatabase.getInstance(this);
        excursionDao = database.excursionDao();

        // Get the excursion ID from the intent.
        excursionId = getIntent().getIntExtra("EXCURSION_ID", -1);
        if (excursionId != -1) {
            // This is an edit operation. Load the excursion object.
            Executors.newSingleThreadExecutor().execute(() -> {
                Excursion excursion = excursionDao.getExcursion(excursionId);
                if (excursion != null) {
                    runOnUiThread(() -> {
                        // Populate the fields with the excursion data.
                        editTextExcursionTitle.setText(excursion.getTitle());
                        // similarly set other fields...
                    });
                }
            });
        }

        buttonSaveExcursion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExcursion();
            }
        });

        loadExcursion(); // load the excursion details

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
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Excursion updatedExcursion;

                    if (excursionId > 0) {
                        // Excursion already exists, get the current data
                        updatedExcursion = excursionDao.getExcursion(excursionId);
                        if (updatedExcursion == null) {
                            // Handle the case where the excursion does not exist.
                            return;
                        }
                    } else {
                        // Excursion is new, create a new object
                        updatedExcursion = new Excursion();
                    }

                    updatedExcursion.setTitle(excursionTitle);

                    Intent intent = getIntent();
                    int vacationId = intent.getIntExtra("vacationId", -1);
                    if (vacationId != -1) {
                        updatedExcursion.setVacationId(vacationId);

                        if (excursionId > 0) {
                            // Excursion already exists, perform update
                            excursionDao.update(updatedExcursion);
                        } else {
                            // Excursion is new, perform insert
                            excursionDao.insert(updatedExcursion);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }
            });
        }
    }


    private void loadExcursion() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Excursion excursion = excursionDao.getExcursion(excursionId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (excursion != null) {
                            editTextExcursionTitle.setText(excursion.getTitle());
                        }
                    }
                });
            }
        });
    }

    private void deleteExcursion() {
        if (excursionId > 0) { // If the excursion exists
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Excursion excursion = excursionDao.getExcursion(excursionId);
                    if (excursion != null) {
                        excursionDao.delete(excursion);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish(); // Return to the previous screen after deleting
                            }
                        });
                    }
                }
            });
        }
    }


}
