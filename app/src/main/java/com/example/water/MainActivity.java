package com.example.water;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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

    @Override
    protected void onStop() {
        Gson gson = new Gson();

        String jsonPlants = gson.toJson(plants);

        Date date = Calendar.getInstance().getTime();
        String jsonDate = gson.toJson(date);

        setDefaults("Plants", jsonPlants,this);
        setDefaults("Date", jsonDate, this);
        super.onStop();
    }

    private void getPrefs() {
        Gson gson = new Gson();
        String jsonPlants = getDefaults("Plants", this);
        plants = gson.fromJson(jsonPlants, new TypeToken<List<Plant>>(){}.getType());



        String jsonDate = getDefaults("Date", this);
        Date lastDate = gson.fromJson(jsonDate, Date.class);

        Date currDate = Calendar.getInstance().getTime();

        if (lastDate != null) {
            int daysPast = (int) TimeUnit.MILLISECONDS.toDays(currDate.getTime() - lastDate.getTime());

            for (Plant p:plants) {
                p.setDaysUntilWater(p.getDaysUntilWater()-daysPast);
            }
        }
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}
