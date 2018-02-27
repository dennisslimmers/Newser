package com.newsapp.newsapplication;

import android.app.Activity;
import com.newsapp.newsapplication.config.*;
import com.newsapp.newsapplication.renderer.ArticleRenderer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    public final String TAG = "MAIN";
    public JSONArray Articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        TextView status = this.findViewById(R.id.ConnectionStatus);
        if (!this.hasConnection()) {
            status.setText(getString(R.string.no_connection));
        } else {
            status.setVisibility(View.INVISIBLE);

            // Set action bar title
            if (this.getActionBar() != null)
                this.getActionBar().setTitle(Config.getHomeNewsSource());

            // Retrieve the data
            new NewsapiConnection().execute();
        }
    }

    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    public void renderArticles() {
        if (Articles.length() > 0) {
            LinearLayout layout = this.getLayout(); // Get the layout

            for (int i = 0; i < Articles.length(); i++) {
                try {
                    JSONObject article = Articles.getJSONObject(i);
                    boolean lastArticle = i == (Articles.length() - 1);

                    ArticleRenderer ar = new ArticleRenderer(lastArticle);
                    ar.renderArticle(article, layout, getApplicationContext()); // Render the article
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    public LinearLayout getLayout() {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(20, 20,20, 0);
        layout.setLayoutParams(params);

        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.addView(layout);

        setContentView(scrollView); // The ScrollView needs to be set as ContentView to be able to scroll
        return layout;
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
}
