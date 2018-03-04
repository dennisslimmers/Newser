package com.newsapp.newsapplication.controllers;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.newsapp.newsapplication.R;
import com.newsapp.newsapplication.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawerController implements Logger {
    private static void onDrawerItemClick(int position, AppCompatActivity activity) {

    }

    public void createMaterialDrawer(Toolbar toolbar, final AppCompatActivity activity) {
        Drawer drawer;
        drawer = new DrawerBuilder()
            .withActivity(activity)
            .withToolbar(toolbar)

            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    DrawerController.onDrawerItemClick(position, activity);

                    // do something with the clicked item :D
                    return true;
                }
            }).build();

        // Add the sources header
        drawer.addItem(new PrimaryDrawerItem().withName(R.string.sources).withSelectable(false));

        // Add all the news sources to the drawer
        for (int i = 0; i < getNewsSources().toArray().length; i++) {
            drawer.addItem((IDrawerItem) getNewsSources().toArray()[i]);
        }

        drawer.setSelection(1);
    }

    private static List<SecondaryDrawerItem> getNewsSources() {
        return Arrays.asList(
            new SecondaryDrawerItem ().withName(R.string.rtl_nieuws).withIdentifier(1),
            new SecondaryDrawerItem ().withName(R.string.the_verge).withIdentifier(2),
            new SecondaryDrawerItem ().withName(R.string.polygon).withIdentifier(3)
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
