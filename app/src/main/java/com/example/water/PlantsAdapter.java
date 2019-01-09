package com.example.water;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;
        public CountDownTimer myCounter;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.plant_name);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }

    private List<Plant> mPlants;
    private HashMap<Button, CountDownTimer> counters;

    public PlantsAdapter(List<Plant> plants){
        mPlants = plants;
        counters = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.item_plant, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlantsAdapter.ViewHolder viewHolder, int position) {
        if (getItemCount() > 0) {
            // Get the data model based on position
            final Plant plant = mPlants.get(position);

            // Set item views based on your views and data model
            TextView textView = viewHolder.nameTextView;
            textView.setText(plant.getName());
            final Button button = viewHolder.messageButton;
            String daysUntilWater = Integer.toString(plant.getDaysUntilWater());
            button.setText(daysUntilWater);
            CountDownTimer cdt = viewHolder.myCounter;
            if (cdt == null) {
                cdt = new CountDownTimer(plant.getDaysUntilWater()*1000L, 1000L) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        plant.setDaysUntilWater((int)(millisUntilFinished/1000));
                        button.setText(Integer.toString((int)(millisUntilFinished/1000)));
                    }

                    @Override
                    public void onFinish() {
                        button.setText("NEEDS WATER");
                        button.setBackgroundColor(Color.RED);
                    }
                };
                counters.put(button, cdt);
                cdt.start();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPlants == null ? 0 : mPlants.size();
    }

}
