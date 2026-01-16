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
    private TextView tvLibraryCount, tvReadCount, tvReadingTime;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Initialize UI Elements
        tvRealName = findViewById(R.id.profileRealName);
        tvUsername = findViewById(R.id.profileUsername);
        tvEmail = findViewById(R.id.profileEmail);

        tvLibraryCount = findViewById(R.id.countLibrary);
        tvReadCount = findViewById(R.id.countRead);
        tvReadingTime = findViewById(R.id.readingTime);

        // 3. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Removes default "Bookshelf" title to show your centered TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 4. Load Data Initially
        loadUserData();

        // 5. Setup Edit Profile Button
        Button editProfileBtn = findViewById(R.id.btn_edit_profile);
        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditProfile.class);

            // Passing current data as extras so EditProfile isn't empty
            intent.putExtra("CURRENT_NAME", tvRealName.getText().toString());

            // Cleaning the '@' from the username before sending
            String rawUsername = tvUsername.getText().toString().replace("@", "");
            intent.putExtra("CURRENT_USERNAME", rawUsername);

            startActivity(intent);
        });
    }

    // Handles the back button click in the Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Automatically reloads data when you come back from the Edit screen
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Reload user to ensure we have the latest DisplayName from Firebase Auth
            user.reload().addOnCompleteListener(task -> {
                FirebaseUser updatedUser = mAuth.getCurrentUser();

                if (updatedUser != null) {
                    // Update Auth-based fields
                    tvEmail.setText(updatedUser.getEmail());
                    tvRealName.setText(updatedUser.getDisplayName() != null ?
                            updatedUser.getDisplayName() : "No Name Set");

                    // Fetch Firestore-based fields (Username & Stats)
                    String userId = updatedUser.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);

                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Load Username
                            String username = documentSnapshot.getString("username");
                            tvUsername.setText("@" + (username != null ? username : "username"));

                            // Load Stats (Handling Longs to prevent crashes)
                            Long library = documentSnapshot.getLong("libraryCount");
                            Long read = documentSnapshot.getLong("readCount");
                            Long timeread = documentSnapshot.getLong("ReadingTime");

                            tvLibraryCount.setText(String.valueOf(library != null ? library : 0));
                            tvReadCount.setText(String.valueOf(read != null ? read : 0));
                            tvReadingTime.setText(String.valueOf(timeread != null ? timeread : 0));
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Profile.this, "Error syncing with database", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
}