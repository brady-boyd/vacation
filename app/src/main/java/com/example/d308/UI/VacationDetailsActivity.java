package com.example.d308.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308.R;
import com.example.d308.adapters.ExcursionAdapter;
import com.example.d308.dao.ExcursionDao;
import com.example.d308.dao.VacationDao;
import com.example.d308.database.AppDatabase;
import com.example.d308.entities.Excursion;
import com.example.d308.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class VacationDetailsActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private Button buttonSave;
    private Button buttonDelete;
    private FloatingActionButton floatingActionButton;
    private VacationDao vacationDao;
    private ExcursionDao excursionDao;
    private Vacation currentVacation;
    private RecyclerView recyclerView;
    private ExcursionAdapter excursionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        editTextTitle = findViewById(R.id.editTextTitle);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerViewExcursion);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        AppDatabase database = AppDatabase.getInstance(this);

        vacationDao = database.vacationDao();
        excursionDao = database.excursionDao();

        Intent intent = getIntent();
        currentVacation = intent.getParcelableExtra("vacation");

        if (currentVacation != null) {
            editTextTitle.setText(currentVacation.getTitle());
            setupRecyclerView();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();

                if (currentVacation != null) {
                    currentVacation.setTitle(title);

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            vacationDao.update(currentVacation);
                            updateRecyclerView(); // Update the RecyclerView after saving the changes
                        }
                    });
                }

                finish();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVacation != null) {
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            vacationDao.delete(currentVacation);
                            updateRecyclerView(); // Update the RecyclerView after deleting the vacation
                        }
                    });
                }

                finish();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent excursionIntent = new Intent(VacationDetailsActivity.this, ExcursionActivity.class);
                if (currentVacation != null) {
                    excursionIntent.putExtra("vacationId", currentVacation.getId());
                }
                startActivity(excursionIntent);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadExcursions(); // Load the excursions and set them on the adapter
    }

    private void loadExcursions() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Excursion> excursions = excursionDao.getAllForVacation(currentVacation.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (excursionAdapter != null) {
                            excursionAdapter.setExcursions(excursions);
                        }
                    }
                });
            }
        });
    }

    private void updateRecyclerView() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<Excursion> excursions = excursionDao.getAllForVacation(currentVacation.getId());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the adapter with the new list of excursions
                        // This will refresh the RecyclerView
                        if (excursionAdapter != null) {
                            excursionAdapter.setExcursions(excursions);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
