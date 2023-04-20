package com.example.androidpv_016.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidpv_016.R;
import com.example.androidpv_016.constants.URLs;
import com.example.androidpv_016.dto.category.CategoryItemDTO;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryCardViewHolder> {
    protected List<CategoryItemDTO> categories;

    public CategoryAdapter(List<CategoryItemDTO> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.category_view, parent, false);

        return new CategoryCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCardViewHolder holder, int position) {
        if (categories != null && position < categories.size()) {
            CategoryItemDTO item = categories.get(position);
            holder.setCategoryId(item.getId());
            holder.getCategoryName().setText(item.getName());
            holder.getCategoryDescription().setText(item.getDescription());

            String imageURL = URLs.BASE_IMAGE_URL + item.getImage();
            Glide.with(holder.getCategoryImage().getContext())
                    .load(imageURL)
                    .into(holder.getCategoryImage());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
