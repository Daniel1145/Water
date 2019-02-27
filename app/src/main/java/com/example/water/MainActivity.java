package com.example.water;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_PLANT = "com.example.water.PLANT";
    public static final String POSITION = "com.example.water.POSITION";

    ArrayList<Plant> plants;
    PlantsAdapter adapter;
    RecyclerView rvPlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPrefs();
        if (plants == null) plants = new ArrayList<>();

        FloatingActionButton myFab = findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPlantActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Lookup the recyclerview in activity layout
        rvPlants = findViewById(R.id.rvPlants);
        // Create adapter passing in the sample user data
        adapter = new PlantsAdapter(plants, this);
        // Attach the adapter to the recyclerview to populate items
        rvPlants.setAdapter(adapter);
        // Set layout manager to position the items
        rvPlants.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvPlants.addItemDecoration(itemDecoration);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Plant plant = data.getParcelableExtra(EXTRA_PLANT);

            plants.add(0, plant);

            Gson gson = new Gson();
            String jsonPlants = gson.toJson(plants);
            setDefaults("Plants", jsonPlants, this);

            adapter.notifyDataSetChanged();
            rvPlants.scrollToPosition(0);
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Plant p = data.getParcelableExtra(EXTRA_PLANT);
            int position = data.getIntExtra(POSITION, 0);
            if (p != null) {
                plants.set(position, p);
                adapter.notifyItemChanged(position);
            } else {
                plants.remove(position);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // Saves the current state of the app and the current date before closing
    @Override
    protected void onStop() {
        Gson gson = new Gson();
        Gson dateGson = Converters.registerLocalDate(new GsonBuilder()).create();

        String jsonPlants = gson.toJson(plants);

        LocalDate date = LocalDate.now();
        String jsonDate = dateGson.toJson(date);

        setDefaults("Plants", jsonPlants,this);
        setDefaults("Date", jsonDate, this);
        super.onStop();
    }

    // Upon launching the app, loads the saved state of the app and
    // subtracts the number of days passed from each plant watering schedule
    private void getPrefs() {
        Gson gson = new Gson();
        Gson dateGson = Converters.registerLocalDate(new GsonBuilder()).create();

        String jsonPlants = getDefaults("Plants", this);
        plants = gson.fromJson(jsonPlants, new TypeToken<List<Plant>>(){}.getType());

        String jsonDate = getDefaults("Date", this);
        LocalDate lastDate = dateGson.fromJson(jsonDate, LocalDate.class);

        LocalDate currDate = LocalDate.now();

        if (lastDate != null) {
            int daysPast = Days.daysBetween(lastDate, currDate).getDays();

            for (Plant p:plants) {
                p.setDaysUntilWater(p.getDaysUntilWater()-daysPast);
            }
        }
    }

    // Stores a string into shared preferences
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Gets a string from shared preferences
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}
