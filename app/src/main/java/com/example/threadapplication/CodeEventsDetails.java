package com.example.threadapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.threadapplication.events_display.Events;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

//this class displays all events separately with their details
//this class is also used by users to subscribe to events for getting event notifications
public class CodeEventsDetails extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    TextView eventName;
    Events evnt;
    TextView eventDesc;
    AppCompatSeekBar eventProgress;
    TextView eventLevel;
    TextView eventContact;
    TextView eventVenue;
    ViewGroup eventRoot;
    Toolbar toolbar;
    TextView evntStartTime;
    TextView evntEndTime;
    String uid;
    SwitchCompat notificationSwitch;
    ProgressBar bar;
    TextView evntDate;
    boolean subscribed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_events_details);

        //gear up widgets
        wireUpListeners();

        //get corresponding event for displaying them
        evnt=(Events)getIntent().getSerializableExtra("event");

        //get current user
        uid=FirebaseAuth.getInstance().getUid();

        //check if user has subscribed for the current event
        if(savedInstanceState==null)
            getIfSubscribed();

        //fill widgets with event details
        fillUpWidgets();

        notificationSwitch.setOnCheckedChangeListener(this);
    }

    private void getIfSubscribed() {
        bar.setVisibility(View.VISIBLE);

        //get all participants who are subscribed to the current event
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Symposium/" + evnt.getName() + "/subscribers");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if user has subscribed for current event
                if(dataSnapshot.hasChild(uid)) {

                    //check the  notification bar
                    notificationSwitch.setOnCheckedChangeListener(null);
                    notificationSwitch.setChecked(true);
                    notificationSwitch.setOnCheckedChangeListener(CodeEventsDetails.this);
                }
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bar.setVisibility(View.GONE);
            }
        });
    }

    //fills event details on the widgets
    private void fillUpWidgets() {
        switch(evnt.getProgress()) {
            case 0: eventRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.not_started));
                break;
            case 1: eventRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.in_progress));
                break;
            case 2:eventRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.end_soon));
                break;
            case 3:eventRoot.setBackgroundColor(ContextCompat.getColor(this,R.color.ended));
                break;

        }

        evntStartTime.setText(evnt.getStartTime());
        evntEndTime.setText(evnt.getEndTime());
        evntDate.setText(evnt.getDate());
        eventName.setText(evnt.getName());
        eventDesc.setText(evnt.getDesc());
        eventContact.setText(evnt.getContact());
        eventProgress.setProgress(evnt.getProgress());
        eventLevel.setText(evnt.getCurrentLevel()+"");
        eventVenue.setText(evnt.getVenue());


        eventProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(evnt.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void wireUpListeners() {
        eventName=(TextView)findViewById(R.id.event_header);
        eventDesc=(TextView)findViewById(R.id.event_desc);
        eventProgress=(AppCompatSeekBar) findViewById(R.id.progress);
        eventLevel=(TextView)findViewById(R.id.current_level);
        eventVenue=(TextView)findViewById(R.id.venue);
        eventContact=(TextView)findViewById(R.id.contact_info);
        eventRoot=findViewById(R.id.root);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Events");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notificationSwitch=(SwitchCompat) findViewById(R.id.notification_switch);
        bar=(ProgressBar)findViewById(R.id.progress_bar);
        evntStartTime=findViewById(R.id.start_time);
        evntEndTime=findViewById(R.id.end_time);
        evntDate=findViewById(R.id.date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //when user presses back button
        switch (item.getItemId())
        {
            case android.R.id.home:finish();
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        bar.setVisibility(View.VISIBLE);

        //when user opts for suscription
        if(isChecked) {

            //add current user to subscribed list
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Symposium/" + evnt.getName() + "/subscribers");
            dr.child(uid).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {

                //we have to create the topic if not present
                // subscribe the current user for it
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseMessaging.getInstance().subscribeToTopic(evnt.getName()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CodeEventsDetails.this, "subscribed successfully", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.GONE);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CodeEventsDetails.this, "connection error", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.GONE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CodeEventsDetails.this, "connection error", Toast.LENGTH_LONG).show();
                    bar.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            //if user wishes to unsubscribe

            //remove user name from the subscribed list
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Symposium/" + evnt.getName() + "/subscribers");
            dr.child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {


                //unsubscribe them to the current topic
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(evnt.getName()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CodeEventsDetails.this, "UnSubscribed Successfully", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.GONE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CodeEventsDetails.this, "connection error", Toast.LENGTH_LONG).show();
                    bar.setVisibility(View.GONE);
                }
            });
        }
    }

    //save the subscription details
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("subscription",notificationSwitch.isActivated());
    }
}
