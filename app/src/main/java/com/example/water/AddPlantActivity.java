package com.example.water;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddPlantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
    }

    public void onClick(View view){
        EditText waterScheduleEditText = findViewById(R.id.new_plant_water_schedule);
        String temp = waterScheduleEditText.getText().toString();
        if (temp.matches("") || temp.matches("0")){
            Toast.makeText(this, "Please enter the plant's watering schedule", Toast.LENGTH_SHORT).show();
        } else {
            int waterSchedule = Integer.parseInt(temp);

            EditText nameEditText = findViewById(R.id.new_plant_name);
            String name = nameEditText.getText().toString();

            EditText speciesEditText = findViewById(R.id.new_plant_species);
            String species = speciesEditText.getText().toString();


            Plant plant = new Plant(name, species, waterSchedule);

            Intent output = new Intent();
            output.putExtra(MainActivity.EXTRA_PLANT, plant);
            setResult(RESULT_OK, output);
            finish();
        }
    }
}
