package com.example.groupproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.adapter.RequestListAdapter;
import com.example.groupproject.model.Consultation;
import com.example.groupproject.model.DeleteResponse;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.ConsultService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentActivity extends AppCompatActivity {
    ConsultService consultService;
    RecyclerView requestList;
    RequestListAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // get reference to the RecyclerView bookList
        requestList = findViewById(R.id.requestList);

        //register for context menu
        registerForContextMenu(requestList);

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
        consultService = ApiUtils.getConsultService();

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
                requestAdapter = new RequestListAdapter(getApplicationContext(), consults, user);

                // set adapter to the RecyclerView
                requestList.setAdapter(requestAdapter);

                // set layout to recycler view
                requestList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requestList.getContext(),
                        DividerItemDecoration.VERTICAL);
                requestList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Consultation>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    public void AddRequest(View view) {
        finish();// stop this StudentActivity
        startActivity(new Intent(this, RequestActivity.class));
    }

    public void Cancel(View view) {
        Consultation c = requestAdapter.getSelectedItem();

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // prepare REST API call
        consultService = ApiUtils.getConsultService();
        Call<DeleteResponse> call = consultService.deleteConsult(user.getToken(), c.getConsultation_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                    startActivity(intent);
                    finish();

                    // 200 means OK
                    displayToast("Request successfully deleted");
                } else {
                    displayAlert("Request failed to delete");
                    Log.e("MyApp:", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    public void Filter(View view) {
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

    /**
     * Display a Toast message
     * @param message
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}