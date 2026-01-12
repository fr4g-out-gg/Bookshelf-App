package com.example.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailField, passwordField, nameField, usernameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize all fields
        nameField = findViewById(R.id.regName);       // New: Real Name
        usernameField = findViewById(R.id.regUsername); // New: Username
        emailField = findViewById(R.id.regEmail);
        passwordField = findViewById(R.id.regPassword);
        Button signUpBtn = findViewById(R.id.btn_do_register);

        signUpBtn.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String pass = passwordField.getText().toString().trim();

            if (validateInput(name, username, email, pass)) {
                performRegistration(name, username, email, pass);
            }
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

    private boolean validateInput(String name, String username, String email, String pass) {
        if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performRegistration(String name, String username, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // 1. Update Firebase Auth Profile (Real Name)
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates);

                            // 2. Save Username and Stats to Firestore
                            saveUserToFirestore(user.getUid(), username);

                            Intent intent = new Intent(Register.this, Menu.class);
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String username) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("libraryCount", 0);
        userMap.put("readCount", 0);
        userMap.put("shelvesCount", 0);

        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Register.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}