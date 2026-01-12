package com.example.bookshelf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEdit, usernameEdit;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEdit = findViewById(R.id.editRealName);
        usernameEdit = findViewById(R.id.editUsername);

        setContentView(R.layout.activity_profile);


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

    private void saveProfileChanges() {
        String newName = nameEdit.getText().toString().trim();
        String newUsername = usernameEdit.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null || newName.isEmpty() || newUsername.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }


            updateProfileTextData(user, newName, newUsername);

        Intent intent = new Intent(EditProfile.this, Profile.class);
        startActivity(intent);
    }

    private void updateProfileTextData(FirebaseUser user, String name, String username) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateFirestoreUsername(user.getUid(), username);
            }
        });
    }

    private void updateFirestoreUsername(String uid, String username) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    // This closes EditProfile and goes back to the Profile screen
                    finish();
                });
    }
}