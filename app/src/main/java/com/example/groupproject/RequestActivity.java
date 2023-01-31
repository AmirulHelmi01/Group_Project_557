package com.example.groupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.groupproject.adapter.UserSpinnerAdapter;
import com.example.groupproject.model.Consultation;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.ConsultService;
import com.example.groupproject.remote.UserService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    Spinner edtTime;
    Button edtDate;
    private Spinner spLecturer;
    User user;
    private UserSpinnerAdapter usersSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        initDatePicker();
        edtDate = findViewById(R.id.edtDate);
        edtDate.setText(getTodaysDate());

        final List<String> states2 = Arrays.asList("8.00am-10.00am", "10.00am-12.00pm", "12.00pm-2.00pm", "2.00pm-4.00pm", "4.00pm-6.00pm");
        final Spinner time = findViewById(R.id.edtTime);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.selected_item, states2);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        time.setAdapter(adapter);

        updateSpinner();


    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            edtDate.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void updateSpinner()
    {
        spLecturer = findViewById(R.id.spLecturer);

        // retrieve book info from database using the book id
        // get user info from SharedPreferences
        user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // retrieved list of user and set to spinner
        // send request to add new book to the REST API
        UserService userService = ApiUtils.getUserService();
        Call<List<User>> call = userService.getAllUsers(user.getToken());

        // execute
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book added successfully?
                List<User> users = response.body();

                if (users != null) {
                    // set to spinner
                    usersSpinnerAdapter = new UserSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, users);
                    spLecturer.setAdapter(usersSpinnerAdapter);

                    int lecturerId = user.getId();

                    // set spinner. find the index of this authorId in the spinner
                    for (int i = 0; i< usersSpinnerAdapter.getCount(); i++) {
                        if (usersSpinnerAdapter.getItem(i).getId() == lecturerId)
                            spLecturer.setSelection(i);
                    }
                } else {
                    displayAlert("Retrieve users failed.");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void AddRequest(View view) {

        User lecturer = (User) spLecturer.getSelectedItem();
        Button edtDate = findViewById(R.id.edtDate);
        Spinner edtTime = findViewById(R.id.edtTime);
        EditText edtPurpose = findViewById(R.id.edtPurpose);

        String date = edtDate.getText().toString();
        String time = edtTime.getSelectedItem().toString();
        String purpose = edtPurpose.getText().toString();

        Consultation c = new Consultation(0, user.getId(), lecturer.getId(), date, time, purpose, "Pending");

        Log.d("MyApp:", "Note info: " + c.toString());

        // send request to add new book to the REST API
        ConsultService consultService = ApiUtils.getConsultService();
        Call<Consultation> call = consultService.requestConsult(user.getToken(), c);

        // execute
        call.enqueue(new Callback<Consultation>() {
            @Override
            public void onResponse(Call<Consultation> call, Response<Consultation> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // note added successfully?
                Consultation addedNote = response.body();
                if (addedNote != null) {
                    // display message
                    Toast.makeText(getApplicationContext(),
                            "Request successful.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Add New Request failed.");
                }
            }

            @Override
            public void onFailure(Call<Consultation> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
}