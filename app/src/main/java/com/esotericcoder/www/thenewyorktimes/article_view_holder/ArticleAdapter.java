package com.esotericcoder.www.thenewyorktimes.article_view_holder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.esotericcoder.www.thenewyorktimes.R;
import com.esotericcoder.www.thenewyorktimes.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private List<Article> articles = new ArrayList<>(); // Cached copy of words
    private Context mContext;
    private String thumbnailUrl = null;

    public ArticleAdapter(Context context) {
        this.mContext = context;
    }

    public void addData(List<Article> newArticles) {
        articles.addAll(newArticles);
        notifyDataSetChanged();
    }

    public void clearData(){
        articles = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        thumbnailUrl = articles.get(position).getThumbnail();
        if(thumbnailUrl != null){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ArticleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_has_image, parent, false));
        } else {
            return new ArticleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_no_image, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        final String BASE_IMAGE_URL = "https://www.nytimes.com/";
        final Article article = articles.get(position);
        holder.title.setText(article.getHeadline().getMain());
        holder.description.setText(article.getSnippet());
        holder.category.setText(article.getNewsDesk());
        if(thumbnailUrl != null){
            Glide.with(mContext)
                    .load(BASE_IMAGE_URL + thumbnailUrl)
                    .override(500, 300)
                    .into(holder.thumbnail);
        }
        holder.itemView.setTag(article);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
                String url = article.getWebUrl();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);

                int requestCode = 100;

                PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                        requestCode,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                // set toolbar color
                builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorAccent));

                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_stat_name);

                // Map the bitmap, text, and pending intent to this icon
                // Set tint to be true so it matches the toolbar color
                builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

                // set toolbar color and/or setting custom actions before invoking build()
                // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                CustomTabsIntent customTabsIntent = builder.build();
                // and launch the desired Url with CustomTabsIntent.launchUrl()
                customTabsIntent.launchUrl(mContext, Uri.parse(url));
            }
        });
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (articles != null)
            return articles.size();
        else return 0;
    }
}
