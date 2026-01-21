package com.example.bookshelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
        // ZMENA: Teraz používame tvoj nový book_item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = bookList.get(position);

        if (currentBook == null) return;


        String url = currentBook.getImageUrl();

        if (holder.txtTitle != null) {

            if (url != null && !url.isEmpty()) {
                holder.txtTitle.setText("");
            } else {
                holder.txtTitle.setText(currentBook.getTitle());
            }
        }


        if (holder.imgCover != null) {
            Glide.with(holder.itemView.getContext())
                    .load(url != null && !url.isEmpty() ? url : null)
                    .placeholder(R.drawable.no_cover_available)
                    .error(R.drawable.no_cover_available)
                    .centerCrop()
                    .into(holder.imgCover);
        }

        if (holder.imgReadStatus != null) {
            if (currentBook.isRead()) {
                holder.imgReadStatus.setVisibility(View.VISIBLE);
            } else {
                holder.imgReadStatus.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle;
        ImageView imgReadStatus;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgBookItemCover);
            txtTitle = itemView.findViewById(R.id.nameBookItem);
            imgReadStatus = itemView.findViewById(R.id.imgReadStatus);

        }
    }
}