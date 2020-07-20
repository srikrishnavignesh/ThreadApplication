package com.example.threadapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.threadapplication.user_validation.UserDetailsActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;


//the activity initially authenticates the user if not authenticated
//it checks the remote db for usr existence
//if user is not registered we prompt him to register
//if user is already registered we direct him to symposium activities

public class MainActivity extends AppCompatActivity{
    int AUTH_CODE=12;
    FirebaseUser user;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //try getting current user
        user=FirebaseAuth.getInstance().getCurrentUser();
        pb=(ProgressBar)findViewById(R.id.progress);

        //prompt usr for a google sign in
        startSignIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AUTH_CODE && resultCode==RESULT_OK)
        {
            user=FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null)
                checkForUserExistence();
        }
    }

    private void moveToUserDetails() {

        //getting user details activity
        Intent intent=new Intent(this, UserDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //default authentication activity provided by firebaseAuth is used
    //google is the only sign in allowed
    public void startSignIn() {

        //user is null move to firbaseAuth acitivity
        if(user==null) {
            List<AuthUI.IdpConfig> list = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false)
                    .setAvailableProviders(list).build(), AUTH_CODE);
        }
        else
            checkForUserExistence();
        //check to see if current user is new
    }


    private void checkForUserExistence() {
        pb.setVisibility(View.VISIBLE);

        //chck the remote db for usr existence
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference dr=db.getReference("Symposium/Participants");

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //if user has not registered direct him to register activty
                if(!dataSnapshot.hasChild(user.getUid()))
                {
                    pb.setVisibility(GONE);
                    moveToUserDetails();
                }
                else
                {
                    pb.setVisibility(GONE);

                    //if user already registered direct him to sympsium activities
                    Intent intent=new Intent(MainActivity.this,SymposiumEvents.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

