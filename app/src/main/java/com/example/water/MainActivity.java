package com.example.water;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_PLANT = "com.example.water.PLANT";
    public static final String PREF_ID = "com.example.water.PREF_ID";

    SharedPreferences myPrefs;

    ArrayList<Plant> plants;
    PlantsAdapter adapter;
    RecyclerView rvPlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPrefs = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);

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
        adapter = new PlantsAdapter(plants);
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

            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            Gson gson = new Gson();
            String jsonPlants = gson.toJson(plants);
            prefsEditor.putString("Plants", jsonPlants);
            prefsEditor.commit();

            adapter.notifyItemInserted(0);
            rvPlants.scrollToPosition(0);
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        Gson gson = new Gson();

        String jsonPlants = gson.toJson(plants);

        Date date = Calendar.getInstance().getTime();
        String jsonDate = gson.toJson(date);

        prefsEditor.putString("Plants", jsonPlants);
        prefsEditor.putString("Date", jsonDate);
        prefsEditor.commit();
        super.onStop();
    }

    private void getPrefs() {
        Gson gson = new Gson();
        String jsonPlants = myPrefs.getString("Plants", "");
        plants = gson.fromJson(jsonPlants, new TypeToken<List<Plant>>(){}.getType());

        String jsonDate = myPrefs.getString("Date", "");
        Date lastDate = gson.fromJson(jsonDate, Date.class);

        Date currDate = Calendar.getInstance().getTime();

        if (lastDate != null) {
            int daysPast = (int) TimeUnit.MILLISECONDS.toDays(currDate.getTime() - lastDate.getTime());

            for (Plant p:plants) {
                p.setDaysUntilWater(p.getDaysUntilWater()-daysPast);
            }
        }
    }
}
