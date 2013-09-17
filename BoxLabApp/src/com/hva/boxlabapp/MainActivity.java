package com.hva.boxlabapp;

import com.hva.boxlabapp.utils.TabListenerImpl;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        Tab tabLanding = bar.newTab()
                .setText(getText(R.string.fragment_schedule))
                .setTabListener(new TabListenerImpl<FragmentSchedule>(
                        this, "schedule", FragmentSchedule.class));

        Tab tabExercises = bar.newTab()
        		.setText(getText(R.string.fragment_exercises))
        		.setTabListener(new TabListenerImpl<FragmentExercises>(
        				this, "exercises", FragmentExercises.class));
        Tab tabLibrary = bar.newTab()
        		.setText(getText(R.string.fragment_library))
        		.setTabListener(new TabListenerImpl<FragmentLibrary>(
        				this, "library", FragmentLibrary.class));
        
        bar.addTab(tabLanding);
        bar.addTab(tabExercises);
        bar.addTab(tabLibrary);
    }
    
    // Actionbar extra buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        int itemId = item.getItemId();
		if (itemId == R.id.action_overflow) {
			View menuItemView = findViewById(R.id.action_overflow);
			PopupMenu popup = new PopupMenu(this, menuItemView);
			popup.inflate(R.menu.popup);
			popup.show();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
}
