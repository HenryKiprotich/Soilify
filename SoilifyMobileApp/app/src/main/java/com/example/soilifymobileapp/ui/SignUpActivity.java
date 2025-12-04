package com.example.soilifymobileapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soilifymobileapp.R;
import com.example.soilifymobileapp.models.Token;
import com.example.soilifymobileapp.models.UserCreate;
import com.example.soilifymobileapp.network.ApiClient;
import com.example.soilifymobileapp.network.AuthApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etOtherName, etPhone, etEmail, etPassword, etConfirmPass, etLocation;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etOtherName = findViewById(R.id.etOtherName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        etLocation = findViewById(R.id.etLocation);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String firstName = etName.getText().toString().trim();
            String otherName = etOtherName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPass.getText().toString().trim();
            String location = etLocation.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            signUpUser(firstName, otherName, phone, email, password, location);
        });
    }

    private void signUpUser(String firstName, String otherName, String phone, String email, String password, String location) {
        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        UserCreate userCreate = new UserCreate(firstName, otherName, phone, email, password, location);

        Call<Token> call = authApi.signup(userCreate);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveToken(response.body().getAccessToken());
                    Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}
