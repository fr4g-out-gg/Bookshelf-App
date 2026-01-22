package com.example.bookshelf;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfileDebug";
    private EditText nameEdit, usernameEdit;
    private MaterialButton saveBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // Note: You can add a ProgressBar in your XML with ID 'loadingProgress' if you want a visual loader
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inicializácia Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializácia Views
        nameEdit = findViewById(R.id.editRealName);
        usernameEdit = findViewById(R.id.editUsername);
        saveBtn = findViewById(R.id.btn_save_profile);

        // Nastavenie Toolbaru
        setupToolbar();

        // 1. Unpack and Pre-fill data
        unpackIntentData();

        // 2. Save Button Click Listener with Debugging
        if (saveBtn != null) {
            saveBtn.setOnClickListener(v -> {
                Log.d(TAG, "Save button clicked!");
                saveProfileChanges();
            });
        } else {
            Log.e(TAG, "Save button NOT found in layout. Check R.id.btn_save_profile");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void unpackIntentData() {
        String currentName = getIntent().getStringExtra("CURRENT_NAME");
        String currentUsername = getIntent().getStringExtra("CURRENT_USERNAME");

        Log.d(TAG, "Received Name: " + currentName);
        Log.d(TAG, "Received Username: " + currentUsername);

        if (currentName != null) nameEdit.setText(currentName);
        if (currentUsername != null) usernameEdit.setText(currentUsername);
    }

    private void saveProfileChanges() {
        String newName = nameEdit.getText().toString().trim();
        String newUsername = usernameEdit.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        // Validácia
        if (newName.isEmpty() || newUsername.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null) {
            Log.e(TAG, "No authenticated user found.");
            return;
        }

        // UI Feedback: Disable button to prevent multiple clicks
        saveBtn.setEnabled(false);
        saveBtn.setText("Saving...");

        Log.d(TAG, "Attempting to update Auth DisplayName to: " + newName);
        updateProfileAuth(user, newName, newUsername);
    }

    private void updateProfileAuth(FirebaseUser user, String name, String username) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Firebase Auth updated successfully.");
                updateFirestoreData(user.getUid(), username);
            } else {
                Log.e(TAG, "Auth Update Failed", task.getException());
                resetButton();
                Toast.makeText(this, "Failed to update profile name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFirestoreData(String uid, String username) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        Log.d(TAG, "Attempting to update Firestore for UID: " + uid);

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Firestore updated successfully.");
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    finish(); // Návrat na Profile screen
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore Update Failed", e);
                    resetButton();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void resetButton() {
        saveBtn.setEnabled(true);
        saveBtn.setText("Save changes");
    }
}