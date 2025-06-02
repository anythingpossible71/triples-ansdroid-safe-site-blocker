package com.example.triples_safesiteblocker;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.content.Context;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.Glide;

public class FaviconRequestListener implements RequestListener<Drawable> {
    private final String fallbackHost;
    private final ImageView faviconView;
    private final Context context;
    private boolean fallbackTried = false;

    public FaviconRequestListener(@Nullable String fallbackHost, ImageView faviconView, Context context) {
        this.fallbackHost = fallbackHost;
        this.faviconView = faviconView;
        this.context = context;
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
        if (!fallbackTried && fallbackHost != null) {
            fallbackTried = true;
            new Handler(Looper.getMainLooper()).post(() -> {
                Glide.with(context)
                        .load("https://www.google.com/s2/favicons?sz=64&domain_url=https://" + fallbackHost)
                        .placeholder(R.drawable.bg_favicon)
                        .error(R.drawable.no_fav_icon)
                        .into(faviconView);
            });
            return true;
        } else {
            faviconView.setImageResource(R.drawable.no_fav_icon);
            return true;
        }
    }

    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        return false;
    }
} 