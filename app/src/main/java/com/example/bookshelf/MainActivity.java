package com.example.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText; // Added
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginAuth";

    // New variables for Email/Password
    private EditText emailField, passwordField;
    private Button emailLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button registerBtn = findViewById(R.id.btn_register);

        registerBtn.setOnClickListener(v -> {
            // Intent to open a RegisterActivity (you'll need to create this activity)
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        Button forgotPasswordBtn = findViewById(R.id.btn_forgot_password);
        forgotPasswordBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();

        // 1. Google Login Setup (Your original code)
        Button googleLoginBtn = findViewById(R.id.btn_google_login);
        googleLoginBtn.setOnClickListener(v -> startGoogleLogin());

        // 2. Email/Password Setup (New code)
        emailField = findViewById(R.id.emailEditText);       // Match these IDs in your XML
        passwordField = findViewById(R.id.passwordEditText);
        emailLoginBtn = findViewById(R.id.btn_email_login);  // New button for email login

        emailLoginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginWithEmail(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // New method for Email/Password login
    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        goToDashboard();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Login Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            goToDashboard();
        }
    }

    private void goToDashboard() {
        Intent intent = new Intent(MainActivity.this, Menu.class);
        startActivity(intent);
        finish();
    }

    // --- Google Login Implementation (Unchanged) ---
    private void startGoogleLogin() {
        CredentialManager credentialManager = CredentialManager.create(this);
        String webClientId = "944844549652-rdrsklnq1f5os93i1l7impavg7h89btj.apps.googleusercontent.com";

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(this, request, null, Runnable::run,
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, androidx.credentials.exceptions.GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        firebaseAuthWithGoogle(result);
                    }

                    @Override
                    public void onError(androidx.credentials.exceptions.GetCredentialException e) {
                        Log.e(TAG, "Google Login Failed", e);
                    }
                });
    }

    private void firebaseAuthWithGoogle(GetCredentialResponse result) {
        try {
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
            String idToken = googleIdTokenCredential.getIdToken();

            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Získame aktuálneho používateľa z Firebase Auth
                    com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        checkAndCreateUserInFirestore(user);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Google Auth Failed.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error handling credential", e);
        }
    }

    private void checkAndCreateUserInFirestore(com.google.firebase.auth.FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();

        // Skontrolujeme, či už používateľ v databáze existuje, aby sme mu nepremazali štatistiky
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().exists()) {
                // Používateľ ešte nemá profil, vytvoríme ho
                Map<String, Object> userMap = new HashMap<>();

                // Vygenerujeme username z mena v Google účte alebo z emailu
                String rawName = user.getDisplayName();
                String username = (rawName != null) ? rawName.replace(" ", "_").toLowerCase() : "user_" + userId.substring(0, 5);

                userMap.put("username", username);
                userMap.put("libraryCount", 0);
                userMap.put("readCount", 0);
                userMap.put("email", user.getEmail());
                userMap.put("uid", userId);

                db.collection("users").document(userId).set(userMap)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "User profile created for Google login");
                            goToDashboard();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error creating user profile", e);
                            goToDashboard(); // Ideme do dashboardu aj pri chybe, aby sme userovi nezablokovali appku
                        });
            } else {
                // Používateľ už existuje, len pokračujeme
                goToDashboard();
            }
        });
    }
}