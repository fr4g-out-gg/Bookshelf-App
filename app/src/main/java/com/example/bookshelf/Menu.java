package com.example.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Setup Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView tvRealName = headerView.findViewById(R.id.nav_user_name);
        TextView tvHandle = headerView.findViewById(R.id.nav_user_handle);
        TextView tvEmail = headerView.findViewById(R.id.nav_user_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            // 1. Set data available from Authentication
            tvRealName.setText(user.getDisplayName());
            tvEmail.setText(user.getEmail());

            // 2. Fetch the @username from Firestore
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            if (username != null) {
                                tvHandle.setText("@" + username);
                            }
                        }
                    });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle menu item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            signOutUser();
        } else if (id == R.id.nav_home) {
            // Stay here or refresh
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Menu.this, Profile.class);
            startActivity(intent);

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

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Menu.this, MainActivity.class));
        finish();
    }
}