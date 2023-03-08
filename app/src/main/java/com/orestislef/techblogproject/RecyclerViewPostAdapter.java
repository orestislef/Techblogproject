package com.orestislef.techblogproject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RecyclerViewPostAdapter extends RecyclerView.Adapter<RecyclerViewPostAdapter.NewsViewHolder> implements Filterable {
    Context mContext;
    List<PostItem> mData;
    List<PostItem> mDataFiltered;

    public RecyclerViewPostAdapter(Context mContext, List<PostItem> mData, boolean isDark) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFiltered = mData;
    }

    public RecyclerViewPostAdapter(Context mContext, List<PostItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFiltered = mData;

    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layout;
        layout = LayoutInflater.from(mContext).inflate(R.layout.item_news, viewGroup, false);
        return new NewsViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder newsViewHolder, int position) {

        // bind data here

        // we apply animation to views here
        // first lets create an animation for user photo
        newsViewHolder.img_user.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));

        // lets create the animation for the whole card
        // first lets create a reference to it
        // you ca use the previous same animation like the following

        // but i want to use a different one so lets create it ..
        newsViewHolder.container.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));


        newsViewHolder.tv_title.setText(mDataFiltered.get(position).getTitle());
        newsViewHolder.tv_content.setHtml(mDataFiltered.get(position).Excerpt, new HtmlHttpImageGetter(newsViewHolder.tv_content));
//        newsViewHolder.tv_content.setHtml(mDataFiltered.get(position).getContent());
        newsViewHolder.tv_date.setText(calculateTimeAgo(mDataFiltered.get(position).getDate()));
        if (mDataFiltered.get(position).image.equals("")) {
            newsViewHolder.img_user.setVisibility(View.GONE);
        } else {
            int finalPos = position;
            Glide.with(mContext)
                    .load(mDataFiltered.get(position)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    new Handler().post(() -> Glide.with(mContext)
                            .load(mDataFiltered.get(finalPos).image)
                            .into(newsViewHolder.img_user));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(newsViewHolder.img_user);
        }
        int finalPos = position;
        newsViewHolder.container.setOnClickListener(v ->
                newsViewHolder.tv_content.
                        setHtml(mDataFiltered.get(finalPos).Content,
                                new HtmlHttpImageGetter(newsViewHolder.tv_content)));
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    public String calculateTimeAgo(@NonNull String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
            return ago + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    mDataFiltered = mData;
                } else {
                    List<PostItem> lstFiltered = new ArrayList<>();
                    for (PostItem row : mData) {

                        if (row.getTitle().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    mDataFiltered = lstFiltered;
                }


                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {


                mDataFiltered = (List<PostItem>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {


        HtmlTextView tv_content;
        TextView tv_title, tv_date;
        ImageView img_user;
        RelativeLayout container;


        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_description);
            tv_date = itemView.findViewById(R.id.tv_date);
            img_user = itemView.findViewById(R.id.img_user);
        }
    }
}
