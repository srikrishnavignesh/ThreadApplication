package com.example.threadapplication.events_display;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.threadapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    RecyclerView recycler;
    public EventsFragment() {
        // Required empty public constructor
    }
    ArrayList<Events> eventList;
    EventAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*sending fcm token to recieve notifications for events*/
        sendFcmToken();

        //fetch all live-events from firebase
        fetchEventList();

        //setup recycler to show events list to user
        setUpAdapterAndRecycler(view);
    }

    private void setUpAdapterAndRecycler(View view) {
        //recycler
        recycler=(RecyclerView)view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchEventList() {

        //get connected with remote db
        eventList=new ArrayList<Events>();
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Events");

        //fetch all events from db
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                    eventList.add(ds.getValue(Events.class));

                //create a async task that sorts events in symposium based on time
                new SortAsyncTask(eventList).execute();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFcmToken() {
        //send fcm token to db
        String uid=FirebaseAuth.getInstance().getUid();

        //this is used to send push notifications to the user
        DatabaseReference dr= FirebaseDatabase.getInstance().getReference("Symposium/Participants/"+uid+"/fcm");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                if(instanceIdResult!=null) {
                    dr.setValue(instanceIdResult.getToken());
                }
            }
        });
    }
    class SortAsyncTask extends AsyncTask<Void,Void,ArrayList<EventSection>> implements Comparator<Events> {

        ArrayList<Events> list;
        SortAsyncTask(ArrayList<Events> list)
        {
            this.list=list;
        }

        @Override
        public int compare(Events evnt1, Events evnt2) {

            //extract time from date1
            String dt1 = evnt1.getDate() + " " + evnt1.getStartTime();

            //extract time from date2
            String dt2 = evnt2.getDate() + " " + evnt2.getStartTime();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            //convert them as millisecs from UNIX EPOCH ie.1970
            Calendar cl1 = Calendar.getInstance();
            Calendar cl2 = Calendar.getInstance();
            try {
                cl1.setTime(format.parse(dt1));
                cl2.setTime(format.parse(dt2));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //based on millisec return the event with earlier start time
            return Long.compare(cl1.getTimeInMillis(), cl2.getTimeInMillis());

        }

        @Override
        protected ArrayList<EventSection> doInBackground(Void... voids) {

            //sort the event list
            Collections.sort(list,this);

            ArrayList<EventSection> eventSectionList=new ArrayList<EventSection>();

            int i=0;
            while(i<list.size())
            {
                //fetch date and time from sorted events list
                String title="Day:"+list.get(i).getDate()+"    Time:"+list.get(i).getStartTime();
                eventSectionList.add(new EventSection(title));

                //group all events that fall on the same date and startTime to the user
                int k=i;

                while(k<list.size() && list.get(i).getDate().equals(list.get(k).getDate()) &&
                        list.get(i).getStartTime().equals(list.get(k).getStartTime()))
                {
                    eventSectionList.get(eventSectionList.size()-1).eventList.add(list.get(k));
                    k++;
                }
                i=k;
            }

            //return sorted list
            return eventSectionList;
        }

        @Override
        protected void onPostExecute(ArrayList<EventSection> eventSections) {
            super.onPostExecute(eventSections);

            //update events adapter
            adapter=new EventAdapter(EventsFragment.this.getContext(),eventSections);
            recycler.setAdapter(adapter);
        }
    }
}
