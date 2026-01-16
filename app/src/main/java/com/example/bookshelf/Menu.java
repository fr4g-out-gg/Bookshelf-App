package com.example.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Chronometer chronometer;
    private boolean isRunning = false;
    private long pauseOffset;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 2. Setup Timer UI
        chronometer = findViewById(R.id.readingChronometer);
        findViewById(R.id.btnStartTimer).setOnClickListener(v -> startTimer());
        findViewById(R.id.btnStopTimer).setOnClickListener(v -> stopTimer());

        // 3. Setup Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        // 4. Load User Data for Header
        setupNavigationHeader(navigationView);
    }

    private void setupNavigationHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView tvRealName = headerView.findViewById(R.id.nav_user_name);
        TextView tvHandle = headerView.findViewById(R.id.nav_user_handle);
        TextView tvEmail = headerView.findViewById(R.id.nav_user_email);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvRealName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            tvEmail.setText(user.getEmail());

            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            if (username != null) tvHandle.setText("@" + username);
                        }
                    });
        }
    }

    private void startTimer() {
        if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;
            Toast.makeText(this, "Reading started...", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopTimer() {
        if (isRunning) {
            chronometer.stop();
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            pauseOffset = 0;
            isRunning = false;

            // Calculate duration
            int totalSeconds = (int) (elapsedMillis / 1000);
            int totalMinutes = totalSeconds / 60;


            saveReadingSession(totalMinutes);


            chronometer.setBase(SystemClock.elapsedRealtime()); // Reset display
        }
    }

    private void saveReadingSession(int sessionMinutes) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        CollectionReference sessionsRef = userRef.collection("sessions");

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("date", com.google.firebase.Timestamp.now());
        sessionData.put("duration", sessionMinutes);

        db.runTransaction(transaction -> {
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            long currentTotal = 0;

            if (userSnapshot.exists() && userSnapshot.contains("ReadingTime")) {
                Object timeObj = userSnapshot.get("ReadingTime");
                if (timeObj instanceof Long) {
                    currentTotal = (Long) timeObj;
                }
            }

            // Save individual session log
            transaction.set(sessionsRef.document(), sessionData);

            // Update the main total
            transaction.update(userRef, "ReadingTime", currentTotal + sessionMinutes);

            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Saved " + sessionMinutes + " mins to profile!", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Log.e("ReadingSession", "Failed to save session", e);
            Toast.makeText(this, "Error saving session", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            startActivity(new Intent(Menu.this, Profile.class));
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(Menu.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}