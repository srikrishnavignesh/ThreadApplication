package com.example.threadapplication.events_display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.threadapplication.R;

import java.util.ArrayList;


//an outer recycler with grid layout manager that displays events an inner recycler

//each holder also has a date and startTime that groups similar events and separates different events

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    ArrayList<EventSection> eventSectionList;
    Context context;
    RecyclerView.RecycledViewPool viewPool=new RecyclerView.RecycledViewPool();


    EventAdapter(Context context, ArrayList<EventSection> eventSectionList)
    {
        this.context=context;
        this.eventSectionList=eventSectionList;
    }

    @NonNull
    @Override
    public EventAdapter.EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = (View) inflater.inflate(R.layout.event_list, parent, false);
        return new EventHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventHolder holder, int position) {

        //title of an event is its day and startTime
        holder.titleText.setText(eventSectionList.get(position).title);

        //create a grid layout with 2 span
        GridLayoutManager manager=new GridLayoutManager(context,2);
        manager.setInitialPrefetchItemCount(eventSectionList.get(position).eventList.size());

        //another recycler is created inside each holder with its own adapter and manager
        SubItemAdapter adapter=new SubItemAdapter(context,eventSectionList.get(position).eventList);
        holder.recycler.setAdapter(adapter);
        holder.recycler.setLayoutManager(manager);

        holder.recycler.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return eventSectionList.size();
    }


    public class EventHolder extends RecyclerView.ViewHolder {

        //a title that displays date and startTime
        TextView titleText;

        //a recycler to create subItem adpater
        RecyclerView recycler;

        public EventHolder(@NonNull View itemView) {
            super(itemView);

            titleText=(TextView)itemView.findViewById(R.id.even_date_time);
            recycler=(RecyclerView)itemView.findViewById(R.id.recycler);
        }
    }
}
