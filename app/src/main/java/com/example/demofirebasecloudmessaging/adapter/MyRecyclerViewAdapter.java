package com.example.demofirebasecloudmessaging.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demofirebasecloudmessaging.OnLoadMoreListener;
import com.example.demofirebasecloudmessaging.R;
import com.example.demofirebasecloudmessaging.models.HeroProfile;

import java.util.ArrayList;

/**
 *
 */


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "RV";
    final GridLayoutManager gridLayoutManager;
    ArrayList<HeroProfile> heroProfiles;
    Context context;
    private boolean isLoading;
    private int visibleElementsOnScreen = 3;// element visible on  screen
    private int lastElement,// it holds the index of last element
            totalElementCount;// total element present on the list
    private OnLoadMoreListener loadMoreListener;

    public MyRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<HeroProfile> heroProfiles, Context context) {
        this.heroProfiles = heroProfiles;
        this.context = context;
        //getting layout manager from recycler view
        gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        setProgressBarInfinite(recyclerView);

    }

    public int getTotalElementCount() {
        totalElementCount = gridLayoutManager.getItemCount();
        return totalElementCount;
    }

    public int getLastElement() {
        lastElement = gridLayoutManager.findLastVisibleItemPosition();
        return lastElement;
    }

    public void setProgressBarInfinite(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && getTotalElementCount() <= (getLastElement() + visibleElementsOnScreen)) {
                    if (loadMoreListener != null) {
                        loadMoreListener.loadMoreData();
                    }
                    isLoading = true;
                }

            }
        });
    }

    // setting up the loading listner to the adapter
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void clear() {
        heroProfiles.clear();
        notifyDataSetChanged();
        setLoaded();
    }

    @Override
    public int getItemViewType(int position) {
        return heroProfiles.get(position) == null ? 1 : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new MyHolder(LayoutInflater.from(context)
                        .inflate(R.layout.hero_profile_holder, null));
            case 1:
                return new LoadingViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.loading, null));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            Log.d(TAG, "onBindViewHolder: MyHolder");
            MyHolder holder1 = (MyHolder) holder;
            HeroProfile heroProfile = heroProfiles.get(position);
            holder1.name.setText(heroProfile.getName());
        } else if (holder instanceof LoadingViewHolder) {
            Log.d(TAG, "onBindViewHolder: LoadingHolder");
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;

        }
        // HeroProfile heroProfile = heroProfiles.get()

// Glide.with(context).load(heroProfile.getImageUrl())
//                 .placeholder(R.drawable.defaultprofile)
//                 .into(holder1.profileImage);

    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemCount() {
        return heroProfiles == null ? 0 : heroProfiles.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.hero_img);
            name = itemView.findViewById(R.id.hero_name);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_circular);
        }
    }
}
