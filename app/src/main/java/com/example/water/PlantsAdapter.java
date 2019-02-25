package com.example.water;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> {

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void OnItemClick(View itemView);
    }

    private void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView nameTextView;
        TextView speciesTextView;
        Button messageButton;
        CountDownTimer myCounter;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.plant_name);
            speciesTextView = itemView.findViewById(R.id.plant_species);
            messageButton = itemView.findViewById(R.id.message_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemClick(itemView);
                    }
                }
            });
        }
    }

    private ArrayList<Plant> mPlants;
    private Context context;

    PlantsAdapter(ArrayList<Plant> plants, Context c){
        mPlants = plants;
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.item_plant, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlantsAdapter.ViewHolder viewHolder, final int position) {
        if (getItemCount() > 0) {
            // Get the data model based on position
            final Plant plant = mPlants.get(position);

            // Set item views based on your views and data model
            TextView nameTextView = viewHolder.nameTextView;
            nameTextView.setText(plant.getName());
            TextView speciesTextView = viewHolder.speciesTextView;
            speciesTextView.setText(plant.getSpecies());
            final Button button = viewHolder.messageButton;
            button.getBackground().clearColorFilter();

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
                        button.setText(R.string.needwater);
                        button.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    }
                };
                cdt.start();
                final CountDownTimer finalCdt = cdt;

                setOnItemClickListener(new PlantsAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(View itemView) {
                        finalCdt.cancel();
                        Plant p = mPlants.get(position);
                        Intent intent = new Intent(context, ChangePlantActivity.class);
                        intent.putExtra(MainActivity.EXTRA_PLANT, p);
                        intent.putExtra(MainActivity.POSITION, position);

                        ((Activity) context).startActivityForResult(intent, 2);
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalCdt.cancel();
                        viewHolder.myCounter = null;
                        plant.setDaysUntilWater(plant.getWaterSchedule());
                        button.getBackground().clearColorFilter();
                        notifyItemChanged(position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPlants == null ? 0 : mPlants.size();
    }

}
