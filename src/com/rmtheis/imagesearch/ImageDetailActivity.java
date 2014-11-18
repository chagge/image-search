package com.rmtheis.imagesearch;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.rmtheis.imagesearch.GoogleSearchResponseData.GoogleSearchResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageDetailActivity extends Activity {

    private static final String TAG = ImageDetailActivity.class.getSimpleName();

    public static final String SEARCH_RESULT_EXTRA = "com.rmtheis.imagesearch.search_result_extra";

    private GoogleSearchResult searchResult;
    private ImageViewTouch mImageViewTouch;
    private Target mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_detail);
        setTitle(R.string.image_detail);
        setProgressBarIndeterminateVisibility(true);

        mImageViewTouch = (ImageViewTouch) findViewById(R.id.image);
        mImageViewTouch.setDisplayType(DisplayType.FIT_TO_SCREEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            searchResult = (GoogleSearchResult) extras.getSerializable(SEARCH_RESULT_EXTRA);
        } else {
            return;
        }

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                setProgressBarIndeterminateVisibility(false);
                mImageViewTouch.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(getBaseContext(), "Unable to view image",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "onBitmapFailed()");
            }

            @Override
            public void onPrepareLoad(Drawable arg0) {
                // Do nothing
            }

        };

        Picasso.with(this) //
        .load(searchResult.getLink()) //
        .into(mTarget);
    }

    /** Launch browser to open the context link */
    private void openContextLink() {
        if (searchResult == null) {
            Log.e(TAG, "Null searchResult in openContextLink");
            return;
        }

        try {
            Uri uri = Uri.parse(searchResult.getContextLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Log.e(TAG, "Could not launch browser");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_more) {
            openContextLink();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
