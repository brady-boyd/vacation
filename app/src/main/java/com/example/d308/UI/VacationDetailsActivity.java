package com.example.d308.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private EditText editTextHotel;
    private Button buttonDatePickerStart;
    private Button buttonDatePickerEnd;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private Button buttonEdit;
    private static final int NOTIFICATION_ID_START = 1;
    private static final int NOTIFICATION_ID_END = 2;


    private int vacationId;

    final SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
    final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        editTextTitle = findViewById(R.id.editTextTitle);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerViewExcursion);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextHotel = findViewById(R.id.editTextHotel);
        buttonDatePickerStart = findViewById(R.id.buttonDatePickerStart);
        buttonDatePickerEnd = findViewById(R.id.buttonDatePickerEnd);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        buttonEdit = findViewById(R.id.buttonEdit);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);

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
            vacationId = currentVacation.getId();
            editTextTitle.setText(currentVacation.getTitle());
            editTextHotel.setText(currentVacation.getHotel());
            editTextStartDate.setText(currentVacation.getStartDate());
            editTextEndDate.setText(currentVacation.getEndDate());
            setupRecyclerView();
        }

        editTextTitle.setEnabled(false);
        editTextHotel.setEnabled(false);
        editTextStartDate.setEnabled(false);
        editTextEndDate.setEnabled(false);
        buttonDatePickerStart.setEnabled(false);
        buttonDatePickerEnd.setEnabled(false);
        buttonEdit.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.GONE);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String hotel = editTextHotel.getText().toString();
                String startDate = editTextStartDate.getText().toString();
                String endDate = editTextEndDate.getText().toString();

                if (title.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                    Snackbar.make(v, "All fields required!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (currentVacation == null) {
                    // If there's no currentVacation, create a new one.
                    currentVacation = new Vacation();
                }

                currentVacation.setTitle(title);
                currentVacation.setHotel(hotel);
                currentVacation.setStartDate(startDate);
                currentVacation.setEndDate(endDate);

                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (currentVacation.getId() > 0) {
                            vacationDao.update(currentVacation);
                        } else {
                            long id = vacationDao.insert(currentVacation);
                            currentVacation.setId((int) id);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Disable all fields and show the Edit button
                                editTextTitle.setEnabled(false);
                                editTextHotel.setEnabled(false);
                                editTextStartDate.setEnabled(false);
                                editTextEndDate.setEnabled(false);
                                buttonDatePickerStart.setEnabled(false);
                                buttonDatePickerEnd.setEnabled(false);
                                buttonEdit.setVisibility(View.VISIBLE);
                                buttonSave.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVacation != null) {
                    // Check if the vacation has excursions
                    Log.d("VacationDetailsActivity", "Delete button clicked");
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            List<Excursion> excursions = vacationDao.getAllExcursionsForVacation(currentVacation.getId());
                            if (excursions != null && !excursions.isEmpty()) {
                                // The vacation has excursions, show a message or perform necessary action
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(v, "Can't delete vacation with excursions!", Snackbar.LENGTH_SHORT).show();
                                        Log.d("VacationDetailsActivity", "Vacation has excursions");
                                    }
                                });
                            } else {
                                // Delete the current vacation
                                Log.d("VacationDetailsActivity", "Deleting vacation");
                                vacationDao.delete(currentVacation);
                                finish();
                            }
                        }
                    });
                } else {
                    // Finish the activity
                    finish();
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable all the fields for editing
                editTextTitle.setEnabled(true);
                editTextHotel.setEnabled(true);
                editTextStartDate.setEnabled(true);
                editTextEndDate.setEnabled(true);
                buttonDatePickerStart.setEnabled(true);
                buttonDatePickerEnd.setEnabled(true);
                // Make the Edit button invisible and the Save button visible
                buttonEdit.setVisibility(View.GONE);
                buttonSave.setVisibility(View.VISIBLE);
            }
        });


        buttonDatePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        VacationDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);

                                String newStartDate = dateFormat.format(calendar.getTime());

                                // Check if new start date is after current end date
                                String endDateStr = editTextEndDate.getText().toString();
                                if (!endDateStr.isEmpty()) {
                                    try {
                                        Date endDate = dateFormat.parse(endDateStr);
                                        Date chosenStartDate = dateFormat.parse(newStartDate);

                                        if (endDate != null && chosenStartDate != null && chosenStartDate.after(endDate)) {
                                            Snackbar.make(v, "Start date should be before end date", Snackbar.LENGTH_LONG).show();
                                            return; // don't update the start date field
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                editTextStartDate.setText(newStartDate);
                            }
                        },
                        currentYear,
                        currentMonth,
                        currentDay
                );
                datePickerDialog.show();
            }
        });


        buttonDatePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        VacationDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);

                                String endDate = dateFormat.format(calendar.getTime());
                                editTextEndDate.setText(endDate);

                                // Check if end date is before start date
                                String startDateStr = editTextStartDate.getText().toString();
                                try {
                                    Date startDate = dateFormat.parse(startDateStr);
                                    Date chosenEndDate = dateFormat.parse(endDate);

                                    if (chosenEndDate.before(startDate)) {
                                        Snackbar.make(v, "End date should be after start date", Snackbar.LENGTH_LONG).show();
                                        editTextEndDate.setText(""); // clear the end date field
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        currentYear,
                        currentMonth,
                        currentDay
                );
                datePickerDialog.show();
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

    @Override
    protected void onResume() {
        super.onResume();
        loadExcursions();
        loadDataAndUpdateRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter = new ExcursionAdapter(excursionDao);
        recyclerView.setAdapter(excursionAdapter);

        // Handle click events on the excursion items
        excursionAdapter.setOnItemClickListener(new ExcursionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Excursion excursion) {
                Intent intent = new Intent(VacationDetailsActivity.this, ExcursionActivity.class);
                intent.putExtra("EXCURSION_ID", excursion.getId()); // assuming there is getId method in Excursion
                intent.putExtra("vacationId", vacationId); // Add this line to include the vacation ID
                startActivity(intent);
            }
        });
    }

    private void loadDataAndUpdateRecyclerView() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Pass the vacationId to the method
            List<Excursion> excursions = excursionDao.getAllForVacation(vacationId);
            runOnUiThread(() -> {
                if (excursionAdapter != null) {
                    excursionAdapter.setExcursions(excursions);
                    excursionAdapter.notifyDataSetChanged();
                }
            });
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_alert:
                setAlert(item); // Passing the selected MenuItem
                return true;
            case R.id.action_share: // handle share option
                shareVacationDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareVacationDetails() {
        if (currentVacation != null) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    StringBuilder shareBody = new StringBuilder();
                    shareBody.append("Title: ").append(currentVacation.getTitle()).append("\n");
                    shareBody.append("Hotel: ").append(currentVacation.getHotel()).append("\n");
                    shareBody.append("Start Date: ").append(currentVacation.getStartDate()).append("\n");
                    shareBody.append("End Date: ").append(currentVacation.getEndDate()).append("\n");

                    // Add excursion details
                    List<Excursion> excursions = excursionDao.getAllForVacation(currentVacation.getId());
                    if (excursions != null && !excursions.isEmpty()) {
                        shareBody.append("\nExcursions:\n");
                        for (Excursion excursion : excursions) {
                            shareBody.append("Title: ").append(excursion.getTitle()).append("\n");
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vacation Details");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody.toString());
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }
                    });
                }
            });
        } else {
            Snackbar.make(editTextTitle, "No Vacation Details to Share", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void loadExcursions() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if(currentVacation != null){
                    List<Excursion> excursions = excursionDao.getAllForVacation(currentVacation.getId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            excursionAdapter.setExcursions(excursions);
                        }
                    });
                }
            }
        });
    }

   /* private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Alert")
                .setMessage("Alert set!")
                .setPositiveButton("OK", null)
                .show();
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }


    private void scheduleNotification(Calendar calendar, int notificationId, String message) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationReceiver.EXTRA_MESSAGE, message);
        notificationId++;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void setAlert(MenuItem item) {
        String startDateStr = editTextStartDate.getText().toString();
        String endDateStr = editTextEndDate.getText().toString();
        String vacationTitle = editTextTitle.getText().toString(); // retrieve vacation title

        if (startDateStr.isEmpty() && endDateStr.isEmpty()) {
            return;
        }

        View menuItemView = findViewById(item.getItemId());
        PopupMenu popupMenu = new PopupMenu(this, menuItemView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_alert, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_start_date:
                        if (!startDateStr.isEmpty()) {
                            try {
                                Date startDate = dateFormat.parse(startDateStr);
                                Calendar calendarStart = Calendar.getInstance();
                                calendarStart.setTime(startDate);
                                Calendar currentCalendar = Calendar.getInstance();
                                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                currentCalendar.set(Calendar.MINUTE, 0);
                                currentCalendar.set(Calendar.SECOND, 0);
                                currentCalendar.set(Calendar.MILLISECOND, 0);
                                if (!calendarStart.before(currentCalendar)) {
                                    scheduleNotification(calendarStart, NOTIFICATION_ID_START, "Today is the beginning of " + vacationTitle + "!");
                                    Toast.makeText(getApplicationContext(), "Start Date Alert Set", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Alerts cannot be set for days in the past!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.menu_end_date:
                        if (!endDateStr.isEmpty()) {
                            try {
                                Date endDate = dateFormat.parse(endDateStr);
                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTime(endDate);
                                Calendar currentCalendar = Calendar.getInstance();
                                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                currentCalendar.set(Calendar.MINUTE, 0);
                                currentCalendar.set(Calendar.SECOND, 0);
                                currentCalendar.set(Calendar.MILLISECOND, 0);
                                if (!calendarEnd.before(currentCalendar)) {
                                    scheduleNotification(calendarEnd, NOTIFICATION_ID_END, "Today is the end of " + vacationTitle + "!");
                                    Toast.makeText(getApplicationContext(), "End Date Alert Set", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Alerts cannot be set for days in the past!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.menu_cancel:
                        // Cancel action (do nothing)
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

}

