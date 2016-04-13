package com.williampan.popularmovies;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by William Pan on 12/04/2016.
 */
public class MovieFragment extends Fragment{

    private String[][] movieData;
    private GridViewAdapter gridViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Add this line for fragment to handle menu events
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menuItem
        inflater.inflate(R.menu.moviesort_menu,menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieData();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getMovieData();
            return true;
        }
        if (id == R.id.action_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieData = new String[][]{};
        gridViewAdapter = new GridViewAdapter(getActivity(),R.layout.grid_item_movie,getThumbnailURL(movieData));
        // Get reference to the gridView, and attach the adapter
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_moviegrid);
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Populate intenet with movie data
                String[] movieDetails = movieData[position];
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("title",movieDetails[0]);
                intent.putExtra("imageURL",movieDetails[1]);
                intent.putExtra("synopsis",movieDetails[2]);
                intent.putExtra("rating",movieDetails[3]);
                intent.putExtra("release",movieDetails[4]);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public ArrayList<String> getThumbnailURL(String[][] movieData){
        // Iterates through movieData returning all thumbnail URLs
        ArrayList<String> returnAL = new ArrayList<>();
        for(int i=0; i < movieData.length; i++){
            returnAL.add(movieData[i][1]);
        }
        return returnAL;
    }

    public class GetMovieInformation extends AsyncTask<String,Void,String[][]>{
        // Tag used for logging
        private final String LOG_TAG = GetMovieInformation.class.getSimpleName();
        ProgressDialog dialog;

        public GetMovieInformation(){
            dialog = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading Movie Data ...");
            dialog.show();
        }

        @Override
        protected String[][] doInBackground(String... strings) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJSONString = null;

            // If need to be sort by Rating
            final String MDB_BASE_URL;
            final String APPID_PARAM = "api_key";
            final String APPID = "205e8af7e05b1de528838375916e0223";
            if (strings[0].equals("P")) {
                MDB_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
            } else {
                MDB_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
            }

            try {
                // Construct the URL for the MovieDataBase query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // https://www.themoviedb.org/documentation/api

                Uri uriOut = Uri.parse(MDB_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, APPID)
                        .build();
                URL url = new URL(uriOut.toString());

                // Create the request to MovieDataBase, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJSONString = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            MovieDataJSONParser movieDataJSONParser = new MovieDataJSONParser();
            try {
                return movieDataJSONParser.getMovieDataFromJSON(movieJSONString);
            } catch (JSONException e) {
                Log.e(LOG_TAG,"Error ",e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(String[][] strings) {
            dialog.dismiss();
            super.onPostExecute(strings);
            if (strings != null) {
                movieData = strings;
                gridViewAdapter.clear();
                gridViewAdapter.addAll(getThumbnailURL(strings));
            }
        }
    }

    private void getMovieData(){
        GetMovieInformation getMovieInformation = new GetMovieInformation();
        // Get location from settings
        SharedPreferences locationPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortingMode = locationPref.getString(getString(R.string.pref_sorting_key),getString(R.string.pref_sorting_default));
        // Fetch with specified sorting mode
        getMovieInformation.execute(sortingMode);
    }
}
