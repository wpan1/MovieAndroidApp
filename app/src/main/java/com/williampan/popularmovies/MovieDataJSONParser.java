package com.williampan.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by William Pan on 12/04/2016.
 */
public class MovieDataJSONParser {

    private static final String LOG_TAG = MovieDataJSONParser.class.getSimpleName();

    public String[][] getMovieDataFromJSON(String movieJSONString) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_TITLE = "original_title";
        final String MDB_THUMBNAIL = "poster_path";
        final String MDB_THUMBNAILPATH = "http://image.tmdb.org/t/p/w185/";
        final String MDB_SYNOPSIS = "overview";
        final String MDB_RATING = "vote_average";
        final String MDB_RELEASE = "release_date";
        final int MDB_VALUES = 5;
        JSONObject movieJSON = new JSONObject(movieJSONString);
        JSONArray movieArray = movieJSON.getJSONArray(MDB_RESULTS);

        String[][] resultStrs = new String[movieArray.length()][MDB_VALUES];
        for(int i = 0; i < movieArray.length(); i++) {
            // Data about the movie
            String title;
            String thumbnail;
            String synopsis;
            String rating;
            String releaseDate;

            // Get the JSON data representig the Movie
            JSONObject movie = movieArray.getJSONObject(i);
            title = movie.getString(MDB_TITLE);
            thumbnail = MDB_THUMBNAILPATH + movie.getString(MDB_THUMBNAIL);
            synopsis = movie.getString(MDB_SYNOPSIS);
            rating = movie.getString(MDB_RATING);
            releaseDate = movie.getString(MDB_RELEASE);

            // Add resulting data to the resultStrs array
            String[] resultStr = new String[]{
                    title,
                    thumbnail,
                    synopsis,
                    rating,
                    releaseDate
            };
            resultStrs[i] = resultStr;
            //Log.v(LOG_TAG, Arrays.toString(resultStr));
        }
        return resultStrs;
    }
}
