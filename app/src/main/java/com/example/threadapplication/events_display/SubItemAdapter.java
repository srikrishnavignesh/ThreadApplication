package com.example.threadapplication.events_display;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.threadapplication.CodeEventsDetails;
import com.example.threadapplication.R;

import java.util.ArrayList;

//this adapter displays all event details
public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubViewHolder> {

    ArrayList<Events> list;
    Context context;
    //event current progress
    String prog[]={"Not Started","Started","Ends Soon","Ended"};
    public SubItemAdapter(Context context, ArrayList<Events> list) {

        this.list = list;
        this.context=context;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_list, parent, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {

        //event name
        holder.text.setText(list.get(position).name);
        holder.posText.setText(position+".");

        //current progress of the event
        holder.levelText.setText(list.get(position).getCurrentLevel()+"");
        holder.progText.setText(prog[list.get(position).getProgress()]);


        switch(list.get(position).getProgress()) {

            //each event gets a random color
            case 0: holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.not_started));
                    break;
            case 1: holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.in_progress));
                    break;
            case 2:holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.end_soon));
                    break;
            case 3:holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.ended));
                    break;

        }
        //event on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //move to codeEventDetails activity to display complete event details
                Intent intent=new Intent(context, CodeEventsDetails.class);
                intent.putExtra("event",list.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SubViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView posText;
        TextView levelText;
        TextView progText;
        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.event_name);
            posText=itemView.findViewById(R.id.evnt_pos);
            levelText=itemView.findViewById(R.id.event_level);
            progText=itemView.findViewById(R.id.event_progress);
        }
    }
}

