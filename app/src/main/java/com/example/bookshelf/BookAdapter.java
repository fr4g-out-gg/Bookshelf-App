package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelf, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = bookList.get(position);

        // 1. Safety Check: If Firestore failed to map the book, skip this item
        if (currentBook == null) return;

        // 2. Set Title (Handles both TextView and Button types)
        if (holder.txtTitle instanceof TextView) {
            ((TextView) holder.txtTitle).setText(currentBook.getTitle());
        } else if (holder.txtTitle instanceof Button) {
            ((Button) holder.txtTitle).setText(currentBook.getTitle());
        }

        // 3. Load Image with Glide
        String url = currentBook.getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(url != null && !url.isEmpty() ? url : null)
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .centerCrop()
                .into(holder.imgCover);
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        View txtTitle; // Changed to View to prevent ClassCastException

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgBookItemCover);
            txtTitle = itemView.findViewById(R.id.btnBookItem);
        }
    }
}