package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.model.ErrorResponse;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername;
    EditText edtFullname;
    EditText edtPassword;
    EditText edtEmail;
    Spinner spRole;
    Button btnRegister;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize vehicle spinner
        spRole = findViewById(R.id.spRole);
        ArrayAdapter adapterRole = ArrayAdapter.createFromResource(this, R.array.role, R.layout.spinner_item);
        spRole.setAdapter(adapterRole);

        final List<String> states = Arrays.asList("Student", "Lecturer");
        final Spinner role = findViewById(R.id.spRole);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.selected_item, states);
        adapter.setDropDownViewResource(R.layout.dropdown_item);

        role.setAdapter(adapter);


    }

    /**
     * Validate value of userID and password entered. Client side validation.
     * @param userID
     * @param password
     * @return
     */
    private boolean validateRegister(String userID, String password, String confirmPassword) {
        if (userID == null || userID.trim().length() == 0) {
            displayToast("Username is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            displayToast("Password not matched");
            return false;
        }

        if (password == null || password.trim().length() == 0){
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Call REST API to register
     * @param token
     * @param user
     */
    private void doRegister(String token, User user) {
        Call call  = userService.addUser(token, user);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                // received reply from REST API
                if (response.isSuccessful()) {
                    // parse response to POJO
                    User user = (User) response.body();

                    if (user.getToken() != null) {
                        // successful register. server replies a token value
                        displayToast("Register successful!");

                        // forward user to LoginActivity
                        finish();// stop this RegisterActivity
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                }
                else if (response.errorBody() != null){
                    // parse response to POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ErrorResponse e = new Gson().fromJson( errorResp,

                            ErrorResponse.class);
                    displayToast(e.getError().getMessage());
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
            }
        });
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        //MessageDigest works with MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] messageDigest = md.digest(password.getBytes());
        BigInteger bigInt = new BigInteger(1, messageDigest);

        return bigInt.toString(16);
    }

    public void AddNewUser(View view) throws NoSuchAlgorithmException {
        // get references to form elements
        edtUsername = findViewById(R.id.edtUsername);
        edtFullname = findViewById(R.id.edtFullname);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegister = findViewById(R.id.btnRegister);

        // get UserService instance
        userService = ApiUtils.getUserService();

        // get username and password entered by user
        String username = edtUsername.getText().toString();
        String fullname = edtFullname.getText().toString();
        String password = edtPassword.getText().toString();
        String email = edtEmail.getText().toString();

        //read vehicle spinner
        int roleIdx = spRole.getSelectedItemPosition();
        String role = null, token;

        //set role value
        switch(roleIdx) {
            //student
            case 0:
                role = "lecturer";
                break;
            case 1:
                role = "student";
                break;
        }

        User user = new User();
        token = "97132811-ae1a-4294-a11e-194ad2e76ade";

        RegisterActivity encryptor = new RegisterActivity();
        user.addUser(username, fullname, email, encryptor.encryptPassword(password), role);
        // do register
        doRegister(token, user);
    }

    public void LogInAcc(View view) {
        finish();
        startActivity( new Intent(this, LoginActivity.class));
    }
}