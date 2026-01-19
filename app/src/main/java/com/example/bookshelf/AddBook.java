package com.example.bookshelf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddBook extends AppCompatActivity {

    private EditText editTitle, editAuthor, editGenre, editDescription;
    private CheckBox checkRead;
    private ImageView imgCover;
    private Uri imageUri;
    private String shelfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        shelfName = getIntent().getStringExtra("CHOSEN_SHELF_NAME");

        // Inicializácia UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        imgCover = findViewById(R.id.img_book_cover);
        editTitle = findViewById(R.id.edit_book_title);
        editAuthor = findViewById(R.id.edit_book_author);
        editGenre = findViewById(R.id.edit_book_genre);
        editDescription = findViewById(R.id.edit_book_description);
        checkRead = findViewById(R.id.check_is_read);

        Button btnSelectImg = findViewById(R.id.btn_select_image);
        Button btnSave = findViewById(R.id.btn_save_book);

        // Launcher pre výber obrázka z galérie
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgCover.setImageURI(imageUri);
                    }
                }
        );

        btnSelectImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> uploadImageAndSaveBook());
    }

    private void uploadImageAndSaveBook() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            editTitle.setError("Názov je povinný");
            return;
        }

        // Ak používateľ vybral obrázok, najprv nahráme ten
        if (imageUri != null) {
            uploadToStorage(title);
        } else {
            // Ak nevybral obrázok, uložíme knihu s prázdnou URL
            saveBookToFirestore(title, "");
        }
    }

    private void uploadToStorage(String title) {
        String fileName = UUID.randomUUID().toString(); // Unikátne meno súboru
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("book_covers/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Máme URL obrázka, teraz uložíme knihu do Firestore
                    saveBookToFirestore(title, uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Chyba nahrávania obrázka", Toast.LENGTH_SHORT).show());
    }

    private void saveBookToFirestore(String title, String downloadUrl) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "Používateľ nie je prihlásený", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Get current values from UI
        String author = editAuthor.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String genre = editGenre.getText().toString().trim();
        boolean isRead = checkRead.isChecked();

        // 2. Create the Book object using the NEW constructor (including Timestamp)
        // Import com.google.firebase.Timestamp at the top
        com.google.firebase.Timestamp currentTime = com.google.firebase.Timestamp.now();

        Book book = new Book(
                title,
                author,
                description,
                genre,
                downloadUrl,
                isRead,
                currentTime // Added this to fix the null timestamp in your screenshot
        );

        // 3. Save to Firestore
        FirebaseFirestore.getInstance()
                .collection("users").document(uid)
                .collection("custom_shelves").document(shelfName)
                .collection("books")
                .add(book)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Kniha úspešne uložená!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("FirestoreError", "Error adding book", e);
                    Toast.makeText(this, "Chyba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}