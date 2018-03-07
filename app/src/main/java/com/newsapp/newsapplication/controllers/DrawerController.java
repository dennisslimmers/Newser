package com.newsapp.newsapplication.controllers;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.newsapp.newsapplication.MainActivity;
import com.newsapp.newsapplication.R;
import com.newsapp.newsapplication.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawerController implements Logger {
    private Drawer drawer;

    private void onDrawerItemClick(int position, AppCompatActivity activity) {
        // Set the toolbar title to the selected news source
        setToolbarTitle(position, activity);

        // Apply the new news source
        NewsApiController.setCurrentNewsSource(NewsApiController.NewsSource.values()[(position - 1)]);

        // Close the drawer
        closeDrawer();

        // clear the container layout, when the NewsRepository is called it will be recreated
        clearLayout(activity);

        // Fetch all the news articles with the new news source
        ((MainActivity)activity).fetchNewsArticles();
    }

    private void closeDrawer() {
        drawer.closeDrawer();
    }

    private static void clearLayout(AppCompatActivity activity) {
        LinearLayout containerLayout = (LinearLayout) activity.findViewById(R.id.mainLayout).findViewById(R.id.containerLayout);

        if (containerLayout != null)
            containerLayout.removeAllViews();
    }

    private static void setToolbarTitle(int position, AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        NewsApiController.NewsSource selectedSource = NewsApiController.NewsSource.values()[(position - 1)];

        toolbar.setTitle(NewsApiController.mapSourceToString(selectedSource).replace('-',  ' '));
    }

    public void createMaterialDrawer(Toolbar toolbar, final AppCompatActivity activity) {
        int newsSourceIndex = NewsApiController.NewsSource.getIndex(NewsApiController.getCurrentSource());

        drawer = new DrawerBuilder()
            .withActivity(activity)
            .withToolbar(toolbar)

            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    onDrawerItemClick(position, activity);
                    return true;
                }
            }).build();

        // Add the sources header
        drawer.addItem(new PrimaryDrawerItem().withName(R.string.sources).withSelectable(false));

        // Add all the news sources to the drawer
        for (int i = 0; i < getNewsSources().toArray().length; i++) {
            drawer.addItem((IDrawerItem) getNewsSources().toArray()[i]);
        }

        // Set drawer item selection
        drawer.setSelection(newsSourceIndex);
    }

    private static List<SecondaryDrawerItem> getNewsSources() {
        return Arrays.asList(
            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.RTL_NIEUWS)
                        .replace('-', ' ')
            ).withIdentifier(0),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.THE_VERGE)
                        .replace('-', ' ')
            ).withIdentifier(1),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.POLYGON)
                        .replace('-', ' ')
            ).withIdentifier(2),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.THE_GUARDIAN)
                        .replace('-', ' ')
            ).withIdentifier(3),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.CNN)
                        .replace('-', ' ')
            ).withIdentifier(4),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.NATIONAL_GEOGRAPHIC)
                        .replace('-', ' ')
            ).withIdentifier(5),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.TECH_RADAR)
                        .replace('-', ' ')
            ).withIdentifier(6),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.TECH_CRUNCH)
                        .replace('-', ' ')
            ).withIdentifier(7),

            new SecondaryDrawerItem ().withName(
                    NewsApiController.mapSourceToString(NewsApiController.NewsSource.NEW_YORK_TIMES)
                        .replace('-', ' ')
            ).withIdentifier(8)
        );
    }

    @Override
    public void dump(String message) {
        final String TAG = Thread.currentThread()
                .getStackTrace()[2]
                .getClassName();

        Log.e(TAG, message);
    }
}
