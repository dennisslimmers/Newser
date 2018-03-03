package com.newsapp.newsapplication;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.newsapp.newsapplication.config.*;
import com.newsapp.newsapplication.controllers.ArticleController;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MAIN";
    public JSONArray Articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        if (this.hasConnection()) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            this.createMaterialDrawer(toolbar);

            // Retrieve the data
            new NewsapiRepository().execute();
        }
    }

    private void createMaterialDrawer(Toolbar toolbar) {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.temp);

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                    item1, new DividerDrawerItem(),
                    item2, new SecondaryDrawerItem().withName(R.string.temp)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                }).build();
    }

    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    public LinearLayout getLayout() {
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.mainLayout);
        LinearLayout containerLayout = new LinearLayout(layout.getContext());

        layout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.setOrientation(LinearLayout.VERTICAL);

        ScrollView.LayoutParams params = new ScrollView.LayoutParams(
            ScrollView.LayoutParams.MATCH_PARENT,
            ScrollView.LayoutParams.WRAP_CONTENT
        );
        layout.setLayoutParams(params);

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        containerParams.setMargins(20,20,20,0);
        containerLayout.setLayoutParams(containerParams);

        layout.addView(containerLayout);
        return containerLayout;
    }

    public void renderArticles() {
        if (Articles.length() > 0) {
            LinearLayout layout = this.getLayout(); // Get the layout

            for (int i = 0; i < Articles.length(); i++) {
                try {
                    JSONObject article = Articles.getJSONObject(i);
                    boolean lastArticle = i == (Articles.length() - 1);

                    ArticleController ar = new ArticleController(lastArticle);
                    ar.renderArticle(article, layout, getApplicationContext()); // Render the article
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    public class NewsapiRepository extends AsyncTask<Void, Void, String> {
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
