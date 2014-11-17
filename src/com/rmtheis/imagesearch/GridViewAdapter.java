package com.rmtheis.imagesearch;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rmtheis.imagesearch.GoogleSearchResponseData.GoogleSearchResult;
import com.squareup.picasso.Picasso;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<GoogleSearchResult> searchResults;
    private PageListener listener;

    private static final int REQUEST_NEXT_PAGE_PADDING = 4;

    public GridViewAdapter(Context context, ArrayList<GoogleSearchResult> searchResults, PageListener listener) {
        this.context = context;
        this.searchResults = searchResults;
        this.listener = listener;
    }

    public interface PageListener {
        public void requestNextPage();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Use the thumbnail link URL to populate the image view.
        GoogleSearchResult searchResult = getItem(position);
        String url = searchResult.getThumbnailLink();

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
        .load(url) //
        .placeholder(R.drawable.ic_photo_black_48dp) //
        .error(R.drawable.ic_error_red_48dp) //
        .fit() //
        .tag(context) //
        .into(view);

        // If this item is the last item in the list, load another page of schedule data items 
        if (position >= (getCount() - 1 - REQUEST_NEXT_PAGE_PADDING)) {
            listener.requestNextPage();
        }  

        return view;
    }

    @Override 
    public int getCount() {
        return searchResults.size();
    }

    @Override 
    public GoogleSearchResult getItem(int position) {
        return searchResults.get(position);
    }

    @Override 
    public long getItemId(int position) {
        return position;
    }
}
