package com.example.water;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddPlantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
    }

    public void onClick(View view){
        EditText nameEditText = (EditText) findViewById(R.id.new_plant_name);
        String name = nameEditText.getText().toString();
        EditText waterScheduleEditText = (EditText) findViewById(R.id.new_plant_water_schedule);
        String temp = waterScheduleEditText.getText().toString();
        int waterSchedule = Integer.parseInt(temp);
        Plant plant = new Plant(name, waterSchedule);

        Intent output = new Intent();
        output.putExtra(MainActivity.EXTRA_PLANT, plant);
        setResult(RESULT_OK, output);
        finish();
    }
}
