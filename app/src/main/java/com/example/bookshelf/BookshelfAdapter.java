package com.example.bookshelf;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfAdapter.ViewHolder> {

    private List<String> shelfList;

    public BookshelfAdapter(List<String> shelfList) {
        this.shelfList = shelfList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelf, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String shelfName = shelfList.get(position);

        holder.shelfButton.setText(shelfName);

        // Po kliknutí na poličku sa otvorí jej detail
        holder.shelfButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChosenShelf.class);
            intent.putExtra("CHOSEN_SHELF_NAME", shelfName);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return shelfList != null ? shelfList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button shelfButton;

        public ViewHolder(View itemView) {
            super(itemView);
            shelfButton = itemView.findViewById(R.id.btnShelfItem);
        }
    }
}