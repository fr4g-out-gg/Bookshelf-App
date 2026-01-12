package com.example.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private TextView tvRealName, tvUsername, tvEmail;
    private TextView tvLibraryCount, tvReadCount, tvShelvesCount;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI Elements
        tvRealName = findViewById(R.id.profileRealName);
        tvUsername = findViewById(R.id.profileUsername);
        tvEmail = findViewById(R.id.profileEmail);

        tvLibraryCount = findViewById(R.id.countLibrary);
        tvReadCount = findViewById(R.id.countRead);
        tvShelvesCount = findViewById(R.id.countShelves);

        loadUserData();

        Button EditProfileBtn = findViewById(R.id.btn_edit_profile);

        EditProfileBtn.setOnClickListener(v -> {
            // Intent to open a RegisterActivity (you'll need to create this activity)
            Intent intent = new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // FORCE RELOAD to get updated DisplayName/PhotoURL from Firebase Auth
            user.reload().addOnCompleteListener(task -> {

                // Now get the fresh instance after reload
                FirebaseUser updatedUser = mAuth.getCurrentUser();

                if (updatedUser != null) {
                    // 1. Get data from Firebase Authentication (Now Fresh!)
                    String email = updatedUser.getEmail();
                    String fullName = updatedUser.getDisplayName();

                    tvEmail.setText(email);
                    tvRealName.setText(fullName != null ? fullName : "No Name Set");

                    // 2. Get data from Firestore (Username & Stats)
                    String userId = updatedUser.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            tvUsername.setText("@" + (username != null ? username : "username"));

                            // Fetch Statistics (using long to handle Firestore numbers)
                            Long library = documentSnapshot.getLong("libraryCount");
                            Long read = documentSnapshot.getLong("readCount");
                            Long shelves = documentSnapshot.getLong("shelvesCount");

                            tvLibraryCount.setText(String.valueOf(library != null ? library : 0));
                            tvReadCount.setText(String.valueOf(read != null ? read : 0));
                            tvShelvesCount.setText(String.valueOf(shelves != null ? shelves : 0));
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Profile.this, "Error loading stats", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
}