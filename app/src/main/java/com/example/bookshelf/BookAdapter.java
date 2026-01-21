package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = bookList.get(position);

        if (currentBook == null) return;

        // Nastavíme len text (názov knihy)
        if (holder.txtTitle instanceof Button) {
            ((Button) holder.txtTitle).setText(currentBook.getTitle());
        } else if (holder.txtTitle instanceof TextView) {
            ((TextView) holder.txtTitle).setText(currentBook.getTitle());
        }

    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        View txtTitle;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            // Priraďujeme len ID pre text/tlačidlo
            txtTitle = itemView.findViewById(R.id.btnBookItem);
        }
    }
}