package com.wag.project.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wag.project.R;
import com.wag.project.model.BadgeCountsDto;
import com.wag.project.model.ItemDto;

import java.util.ArrayList;
import java.util.List;

/**
 * The StackOverflow user item adapter class extends RecyclerView.Adapter
 */
public class StackOverflowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ItemDto> stackOverflowList;
    private final Context       context;
    private static final int     ITEM      = 0;
    private static final int     LOADING   = 1;
    private boolean isLoading = false;
    private static final String  EMPTY     = " ";

    /**
     * Constructor
     * @param context Activity context
     */
    public StackOverflowAdapter(@NonNull final Context context) {
        this.context = context;
        stackOverflowList = new ArrayList<>();
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return viewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater          inflater   = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View viewProgress = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewProgress);
                break;
        }
        return viewHolder;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param inflater The LayoutInflater
     * @return viewHolder
     */
    @NonNull
    private RecyclerView.ViewHolder getViewHolder(
      @NonNull final ViewGroup parent, @NonNull final LayoutInflater inflater
    ) {
        RecyclerView.ViewHolder viewHolder;
        final View              view = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new StackOverflowUser(view);
        return viewHolder;
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the item
     *                 at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemDto result = stackOverflowList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra(context.getString(R.string.user_link), result.getLink());
                context.startActivity(intent);
            }
        });

        switch (getItemViewType(position)) {
            case ITEM:

                final StackOverflowUser stackOverflowUser = (StackOverflowUser)holder;
                final BadgeCountsDto badgeCounts = result.getBadgeCounts();

                final String goldBadges = badgeCounts.getGold() + EMPTY + context.getString(R.string.gold_badges);
                final String silverBadges = badgeCounts.getSilver() + EMPTY + context.getString(R.string.silver_badges);

                stackOverflowUser.userName.setText(result.getDisplayName());
                stackOverflowUser.goldBadges.setText(goldBadges);
                stackOverflowUser.silverBadges.setText(silverBadges);

                /*
                  Using Glide to handle image loading and caching.
                 */
                stackOverflowUser.progressBar.setVisibility(View.VISIBLE);

                Glide.with(context).load(result.getProfileImage()).asBitmap().centerCrop()
                     .diskCacheStrategy(DiskCacheStrategy.ALL) //cache image
                     .into(new BitmapImageViewTarget(stackOverflowUser.avatar) {
                         @Override
                         protected void setResource(Bitmap resource) {
                             RoundedBitmapDrawable circularBitmapDrawable =
                               RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                             circularBitmapDrawable.setCircular(true);
                             stackOverflowUser.avatar.setImageDrawable(circularBitmapDrawable);
                         }

                         @SuppressWarnings("unchecked")
                         @Override
                         public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                             super.onResourceReady(drawable, anim);

                             //Save image to internal usage for offline use
                             //ImageUtils.saveImage(context, drawable, result.getDisplayName());

                             // image ready, hide progress
                             stackOverflowUser.progressBar.setVisibility(View.GONE);
                         }

                         @Override
                         public void onLoadFailed(Exception e, Drawable errorDrawable) {
                             super.onLoadFailed(e, errorDrawable);
                             // image error, hide progress
                             stackOverflowUser.progressBar.setVisibility(View.GONE);
                         }
                     });
                break;

            case LOADING:
                //Do Nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return stackOverflowList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == stackOverflowList.size() - 1 && isLoading) ? LOADING : ITEM;
    }

    /**
     * Add an item to the stackOverflowList list
     * @param item The item to add
     */
    private void add(@NonNull final ItemDto item) {
        stackOverflowList.add(item);
        notifyItemInserted(stackOverflowList.size() - 1);
    }

    /**
     * Add the StackOverflowList to the adapter
     * @param StackOverflowList is the list
     */
    public void addAll(@NonNull final List<ItemDto> StackOverflowList) {
        for (ItemDto item : StackOverflowList) {
            add(item);
        }
    }

    /**
     * Remove an item from the stackOverflowList list
     * @param item to remove
     */
    private void remove(@NonNull final ItemDto item) {
        int position = stackOverflowList.indexOf(item);
        if (position > -1) {
            stackOverflowList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Clear the adapter
     */
    public void clear() {
        isLoading = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    /**
     * Add loading footer
     */
    public void addLoadingFooter() {
        isLoading = true;
        add(new ItemDto());
    }

    /**
     * Remove loading footer
     */
    public void removeLoadingFooter() {
        isLoading = false;
        final int position = stackOverflowList.size() - 1;
        ItemDto   result   = getItem(position);

        if (result != null) {
            stackOverflowList.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Get stackOverflowList list item
     *
     * @param position the list item position
     * @return stackOverflowList List item
     */
    private ItemDto getItem(final int position) {
        return stackOverflowList.get(position);
    }

    /**
     * Content ViewHolder containing StackOverflow item elements
     */
    private class StackOverflowUser extends RecyclerView.ViewHolder {

        private final TextView    userName;
        private final TextView    goldBadges;
        private final TextView    silverBadges;
        private final ImageView   avatar;
        private final ProgressBar progressBar;

        private StackOverflowUser(View itemView) {
            super(itemView);

            avatar = (ImageView)itemView.findViewById(R.id.avatar);
            userName = (TextView)itemView.findViewById(R.id.user_name);
            goldBadges = (TextView)itemView.findViewById(R.id.gold_badge_count);
            silverBadges = (TextView)itemView.findViewById(R.id.silver_badge_count);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
        }
    }

    /**
     * LoadingViewHolder class
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
