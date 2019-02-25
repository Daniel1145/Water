package com.example.water;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePlantActivity extends AppCompatActivity {

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_plant);
        EditText plantName = findViewById(R.id.new_plant_name);
        EditText plantSpecies = findViewById(R.id.new_plant_species);
        EditText plantWaterSchedule = findViewById(R.id.new_plant_water_schedule);

        Plant p = getIntent().getParcelableExtra(MainActivity.EXTRA_PLANT);
        plantName.setText(p.getName());
        plantSpecies.setText(p.getSpecies());
        plantWaterSchedule.setText(Integer.toString(p.getWaterSchedule()));

        position = getIntent().getIntExtra(MainActivity.POSITION, 0);
    }

    public void onClick(View view){
        EditText nameEditText = findViewById(R.id.new_plant_name);
        String name = nameEditText.getText().toString();

        EditText waterScheduleEditText = findViewById(R.id.new_plant_water_schedule);
        String temp = waterScheduleEditText.getText().toString();

        EditText speciesEditText = findViewById(R.id.new_plant_species);
        String species = speciesEditText.getText().toString();

        int waterSchedule = Integer.parseInt(temp);
        Plant plant = new Plant(name, species, waterSchedule);

        Intent output = new Intent();
        output.putExtra(MainActivity.EXTRA_PLANT, plant);
        output.putExtra(MainActivity.POSITION, position);
        setResult(RESULT_OK, output);
        finish();
    }

    public void onClickDelete(View view){
        Intent output = new Intent();
        output.putExtra(MainActivity.POSITION, position);
        setResult(RESULT_OK, output);
        finish();
    }
}