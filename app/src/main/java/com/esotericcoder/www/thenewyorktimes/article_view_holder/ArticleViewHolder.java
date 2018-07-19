package com.esotericcoder.www.thenewyorktimes.article_view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.esotericcoder.www.thenewyorktimes.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView description;
    public TextView category;
    public ImageView thumbnail;

    public ArticleViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        description = itemView.findViewById(R.id.description);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        category = itemView.findViewById(R.id.category);
    }
}
