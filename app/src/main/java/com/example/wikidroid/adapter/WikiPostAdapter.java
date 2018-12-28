package com.example.wikidroid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wikidroid.R;
import com.example.wikidroid.pojo.WikiPost;

import java.util.ArrayList;

public class WikiPostAdapter extends RecyclerView.Adapter {

    private ArrayList<WikiPost> data;
    private Context context;

    public WikiPostAdapter(ArrayList<WikiPost> wikiPosts, Context context) {
        this.data = wikiPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new WikiViewHolder(LayoutInflater.from(context).inflate(R.layout.row_wiki_element, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        WikiViewHolder holder = (WikiViewHolder) viewHolder;
        WikiPost wikiPost = data.get(position);
        holder.tvTitle.setText(wikiPost.getTitle());
        holder.tvOverView.setText(wikiPost.getDescription());

        Glide.with(context).load(wikiPost.getThumbnailUrl()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class WikiViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView tvTitle;
        TextView tvOverView;
        CardView cardView;

        public WikiViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverView = itemView.findViewById(R.id.tvOverView);
        }
    }
}
