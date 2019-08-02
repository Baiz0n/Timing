package com.nothing.timing.timing.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nothing.timing.timing.R;
import com.nothing.timing.timing.bloc.data.GeneralAppData;
import com.nothing.timing.timing.bloc.data.IntentData;
import com.nothing.timing.timing.tools.ValueMaker;
import com.nothing.timing.timing.ui.HourSetter;

import java.util.ArrayList;

public class MainRecycle extends RecyclerView.Adapter<MainRecycle.ViewHolder> {

    private static final String TAG = MainRecycle.class.getSimpleName();

    private Context context;
    private ArrayList<String> objectives;
    private ArrayList<Integer> progresses;
    private ArrayList<Integer> goalHours;

    private boolean changeColor = true;

    public MainRecycle(Context context, ArrayList<String> objectives, ArrayList<Integer> progresses, ArrayList<Integer> goalHours) {

        this.objectives = objectives;
        this.progresses = progresses;
        this.goalHours = goalHours;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_adapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        Log.e(TAG, "Called view holder");

        if ( !( i >= objectives.size() ) ) {

            final String localObjective = objectives.get(i);
            final int localProgress = progresses.get(i);
            final int localGoalHours = goalHours.get(i);

            holder.objective.setText(localObjective);
            holder.comp.setText(ValueMaker.getCompletedOutOf(localProgress, localGoalHours) );
            holder.progressBar.setProgress(ValueMaker.getProgress(localProgress, localGoalHours));

            holder.parent.setOnTouchListener(GeneralAppData.changeListColor);

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, HourSetter.class);
                    intent.putExtra(IntentData.OBJECTIVE, localObjective); // string
                    intent.putExtra(IntentData.OUTOF, localProgress); // int
                    intent.putExtra(IntentData.TOTAL, localGoalHours); // int


                    context.startActivity(intent);
                }
            });
            colorSwitcher(holder);

        } else {

            holder.parent.removeAllViews();
            holder.parent.setMinimumHeight(230);
            holder.parent.setBackgroundResource(R.color.light_blue);
        }


    }

    @Override
    public int getItemCount() {
        return objectives.size()+1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView objective;
        ProgressBar progressBar;
        LinearLayout parent;
        TextView comp;
        TextView hours;

         ViewHolder(@NonNull View itemView) {
            super(itemView);

            objective = itemView.findViewById(R.id.objective);
            progressBar = itemView.findViewById(R.id.progress_bar);
            parent = itemView.findViewById(R.id.parent);
            comp = itemView.findViewById(R.id.completed_out_of);
            hours = itemView.findViewById(R.id.hours);
        }
    }

    private void colorSwitcher(ViewHolder holder) {

        if ( changeColor ) {

            changeColor = false;

        } else {

            holder.parent.setBackgroundResource(R.color.light_blue);
            changeColor = true;
        }
    }


} // end class
