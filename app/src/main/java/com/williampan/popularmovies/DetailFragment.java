package com.williampan.popularmovies;

/**
 * Created by William Pan on 12/04/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Rootview of fragment_detail
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // Get intenet from activity
        Intent intent = getActivity().getIntent();
        // Change Movie Title
        TextView titleTextView = (TextView)rootView.findViewById(R.id.movie_textview);
        titleTextView.setText(intent.getStringExtra("title"));
        // Change Plot Synopsis
        TextView forecastTextView = (TextView)rootView.findViewById(R.id.synopsis_textview);
        forecastTextView.setText(intent.getStringExtra("synopsis"));
        // Change User Rating
        TextView ratingTextView = (TextView)rootView.findViewById(R.id.userrating_textview);
        ratingTextView.setText("Rating: " + intent.getStringExtra("rating") + "/10");
        // Change Release Date
        TextView releaseTextView = (TextView)rootView.findViewById(R.id.releasedate_textview);
        releaseTextView.setText("Release Date: " + intent.getStringExtra("release"));
        // Change thumbnail
        ImageView thumbnailImageView = (ImageView) rootView.findViewById(R.id.thumbnail_imageview);
        Picasso.with(getActivity()).load(intent.getStringExtra("imageURL")).into(thumbnailImageView);

        return rootView;
    }
}