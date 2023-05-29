package com.example.d308.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308.R;
import com.example.d308.dao.ExcursionDao;
import com.example.d308.database.AppDatabase;
import com.example.d308.entities.Excursion;

import java.util.concurrent.Executors;

public class ExcursionActivity extends AppCompatActivity {
    private EditText editTextExcursionTitle;
    private Button buttonSaveExcursion;

    private int excursionId;
    private ExcursionDao excursionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion);

        editTextExcursionTitle = findViewById(R.id.editTextExcursionTitle);
        buttonSaveExcursion = findViewById(R.id.buttonSaveExcursion);

        excursionId = getIntent().getIntExtra("excursionId", -1);

        AppDatabase database = AppDatabase.getInstance(this);
        excursionDao = database.excursionDao();

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
            Excursion updatedExcursion = new Excursion();
            updatedExcursion.setTitle(excursionTitle);

            Intent intent = getIntent();
            int vacationId = intent.getIntExtra("vacationId", -1);
            if (vacationId != -1) {
                updatedExcursion.setVacationId(vacationId);

                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (excursionId > 0) {
                            // Excursion already exists, perform update
                            updatedExcursion.setId(excursionId);
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
                });
            }
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

}
