package com.example.groupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.adapter.ConsultListAdapter;
import com.example.groupproject.adapter.RequestListAdapter;
import com.example.groupproject.model.Consultation;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.ConsultService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LecturerActivity extends AppCompatActivity {
    RecyclerView consultList;
    ConsultListAdapter consultAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);
        // get reference to the RecyclerView bookList
        consultList = findViewById(R.id.consultList);

        //register for context menu
        registerForContextMenu(consultList);

        updateListView();

    }

    public void logout(View view) {
        // clear the shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).logout();

        // display message
        Toast.makeText(getApplicationContext(),
                "You have successfully logged out.",
                Toast.LENGTH_LONG).show();

        // forward to LoginActivity
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void updateListView() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get book service instance
        ConsultService consultService = ApiUtils.getConsultService();

        // execute the call. send the user token when sending the query
        consultService.getAllConsult(user.getToken()).enqueue(new Callback<List<Consultation>>() {
            @Override
            public void onResponse(Call<List<Consultation>> call, Response<List<Consultation>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                List<Consultation> consults;

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }

                // Get list of book object from response
                consults = response.body();

                // initialize adapter
                consultAdapter = new ConsultListAdapter(getApplicationContext(), consults, user);

                // set adapter to the RecyclerView
                consultList.setAdapter(consultAdapter);

                // set layout to recycler view
                consultList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(consultList.getContext(),
                        DividerItemDecoration.VERTICAL);
                consultList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Consultation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    public void Approve(View view) {
        Consultation c = consultAdapter.getSelectedItem();
        c.setStatus("Approved");

        Log.d("MyApp:", "Note info: " + c.toString());

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the book record to the REST API
        ConsultService consultService = ApiUtils.getConsultService();
        Call<Consultation> call = consultService.updateConsult(user.getToken(), c);

        context = this;
        // execute
        call.enqueue(new Callback<Consultation>() {
            @Override
            public void onResponse(Call<Consultation> call, Response<Consultation> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book updated successfully?
                Consultation updatedConsult = response.body();
                if (updatedConsult != null) {
                    // display message
                    Toast.makeText(context,
                            "Status updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, LecturerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Update status failed.");
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

    public void Decline(View view) {
        Consultation c = consultAdapter.getSelectedItem();
        c.setStatus("Declined");

        Log.d("MyApp:", "Note info: " + c.toString());

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the book record to the REST API
        ConsultService consultService = ApiUtils.getConsultService();
        Call<Consultation> call = consultService.updateConsult(user.getToken(), c);

        context = this;
        // execute
        call.enqueue(new Callback<Consultation>() {
            @Override
            public void onResponse(Call<Consultation> call, Response<Consultation> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book updated successfully?
                Consultation updatedConsult = response.body();
                if (updatedConsult != null) {
                    // display message
                    Toast.makeText(context,
                            "Status updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, LecturerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Update status failed.");
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
}