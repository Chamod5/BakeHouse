package com.example.bakehouse.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.example.bakehouse.R;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<Bitmap> images;

    public ImagePagerAdapter(Context context, List<Bitmap> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_page, container, false);
        ImageView imageView = view.findViewById(R.id.imageViewPagerItem);
        imageView.setImageBitmap(images.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj) {
        container.removeView((View) obj);
    }
}
