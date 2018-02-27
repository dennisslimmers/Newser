package com.newsapp.newsapplication;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.newsapp.newsapplication.config.Config;
import com.newsapp.newsapplication.renderer.ArticleRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeedActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        TextView status = this.findViewById(R.id.ConnectionStatus);
        if (!this.hasConnection()) {
            status.setText(getString(R.string.no_connection));
        } else {
            status.setVisibility(View.INVISIBLE);

            // Set action bar title
            if (this.getActionBar() != null)
                this.getActionBar().setTitle(Config.getHomeNewsSource());
        }
    }

    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public JSONArray Articles;

        private View rootView;

        public final String TAG = "MAIN";

        public PlaceholderFragment() { }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
            this.rootView = rootView;

            // Retrieve the data
            new NewsapiConnection().execute();

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((FeedActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }

        public class NewsapiConnection extends AsyncTask<Void, Void, String> {
            private final String TAG = "NEWSAPI";

            protected String doInBackground(Void... urls) {
                try {
                    URL url = new URL(Config.getFullApiUrl());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }

                        bufferedReader.close();
                        return stringBuilder.toString();
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e){
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }
            }

            protected void onPostExecute(String response) {
                if (response == null) {
                    Log.e(TAG, getString(R.string.api_error));
                } else {
                    try {
                        JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                        Articles = object.getJSONArray("articles");

                        Log.e(TAG, Articles.toString());
                        renderArticles(); // Print all the articles
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }


        public void renderArticles() {
            if (Articles.length() > 0) {
                LinearLayout layout = this.getLayout(); // Get the layout

                for (int i = 0; i < Articles.length(); i++) {
                    try {
                        JSONObject article = Articles.getJSONObject(i);
                        boolean lastArticle = i == (Articles.length() - 1);

                        ArticleRenderer ar = new ArticleRenderer(lastArticle);
                        ar.renderArticle(article, layout, this.rootView.getContext().getApplicationContext()); // Render the article
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        }

        public LinearLayout getLayout() {
            LinearLayout layout = new LinearLayout(this.rootView.getContext().getApplicationContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(20, 20,20, 0);
            layout.setLayoutParams(params);

            ScrollView scrollView = new ScrollView(this.rootView.getContext().getApplicationContext());
            scrollView.addView(layout);

            return layout;
        }
    }
}
