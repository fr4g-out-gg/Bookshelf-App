package com.example.bookshelf;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {

    private EditText emailField;
    private Button sendBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.resetEmailEditText);
        sendBtn = findViewById(R.id.btn_send_reset_link);

        sendBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();

            if (email.isEmpty()) {
                emailField.setError("Email is required");
                return;
            }

            // Firebase Password Reset Logic
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this,
                                    "Check your email to reset password", Toast.LENGTH_LONG).show();
                            finish(); // Returns user to Login screen
                        } else {
                            Toast.makeText(ForgotPassword.this,
                                    "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Enable the back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // This takes the user back
        return true;
    }
}