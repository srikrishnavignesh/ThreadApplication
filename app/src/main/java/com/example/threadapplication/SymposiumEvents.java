package com.example.threadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

//this is the activity that displays all events of a symposium
//we have used a view pager that creates two tabed activities
//In the first tab we list events
//In second tab we provide payment info

public class SymposiumEvents extends AppCompatActivity {
    TabLayout tabs;
    ViewPager2 pager;
    String pageNames[];
    Toolbar toolbar;
    Menu signOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symposium_events);
        wireUpListeners();
    }

    private void wireUpListeners() {

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //creating tabs
        pager=(ViewPager2)findViewById(R.id.view_pager);
        tabs=(TabLayout)findViewById(R.id.tabs);

        //1st  tab lists events
        //2nd tab deals with payment

        pageNames=new String[]{"Events","Id"};

        PagerAdapter adapter=new PagerAdapter(this);

        //adapter is associated with pager that provides fragments based on the tab selected
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs,pager,(tab,position)-> {
            tab.setText(pageNames[position]);
        }).attach();
    }

    // a menu that displays sign out options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.sign_out,menu);
        return true;
    }

    //sign out option selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //sign out and move user to sign in activity
        FirebaseAuth.getInstance().signOut();

        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        return true;
    }
}
