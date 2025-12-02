package com.example.soilifymobileapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soilifymobileapp.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etPhone, etPassword, etConfirmPass;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            // TODO: Call POST /auth/register
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to Login
        });
    }
}