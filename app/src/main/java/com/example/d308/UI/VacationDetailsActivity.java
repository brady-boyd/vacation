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
import com.example.d308.dao.VacationDao;
import com.example.d308.database.AppDatabase;
import com.example.d308.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executors;

public class VacationDetailsActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private Button buttonSave;
    private Button buttonDelete;
    private FloatingActionButton floatingActionButton;
    private VacationDao vacationDao;
    private Vacation currentVacation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        editTextTitle = findViewById(R.id.editTextTitle);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        // Enable the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get an instance of the AppDatabase
        AppDatabase database = AppDatabase.getInstance(this);

        // Get the VacationDao from the AppDatabase
        vacationDao = database.vacationDao();

        Intent intent = getIntent();
        currentVacation = (Vacation) intent.getParcelableExtra("vacation");

        if (currentVacation != null) {
            // If currentVacation is not null, we are editing an existing vacation
            editTextTitle.setText(currentVacation.getTitle());
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();

                // Create a new Vacation object with the entered title
                final Vacation newVacation;
                if (currentVacation == null) {
                    // If currentVacation is null, we are creating a new vacation
                    newVacation = new Vacation();
                } else {
                    // If currentVacation is not null, we are updating an existing vacation
                    newVacation = currentVacation;
                }

                newVacation.setTitle(title);

                // Insert or update the newVacation into the database
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (currentVacation == null) {
                            vacationDao.insertAll(newVacation);
                        } else {
                            vacationDao.update(newVacation);
                        }
                    }
                });

                // Finish the activity
                finish();
            }
        });

        // Add click listener to the delete button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVacation != null) {
                    // Delete the current vacation
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            vacationDao.delete(currentVacation);
                        }
                    });
                }

                // Regardless of whether a vacation was deleted, finish the activity
                finish();
            }
        });

        // Add click listener to the floating action button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ExcursionActivity
                Intent excursionIntent = new Intent(VacationDetailsActivity.this, ExcursionActivity.class);
                startActivity(excursionIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Start the VacationActivity
            Intent vacationIntent = new Intent(VacationDetailsActivity.this, VacationActivity.class);
            startActivity(vacationIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
