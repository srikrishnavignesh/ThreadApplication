package com.example.threadapplication.user_validation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.threadapplication.R;
import com.example.threadapplication.SymposiumEvents;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText name;
    EditText collegeName;
    EditText phoneNumber;
    Spinner yearSpinner;
    Spinner deptSpinner;
    Button createUser;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //get reference for each view and enable them for selection
        wirUpListeners();
    }

    private void wirUpListeners() {

        name=(EditText)findViewById(R.id.name);
        collegeName=(EditText)findViewById(R.id.college_name);
        phoneNumber=(EditText)findViewById(R.id.phone_number);
        yearSpinner=(Spinner)findViewById(R.id.year);
        deptSpinner=(Spinner)findViewById(R.id.dept);

        //spinner for dept in eng
        ArrayAdapter<CharSequence> deptAdapter=ArrayAdapter.createFromResource(this,R.array.dept,android.R.layout.simple_spinner_item);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(deptAdapter);

        //spinner for year in eng
        ArrayAdapter<CharSequence > yearAdapter=ArrayAdapter.createFromResource(this,R.array.year,android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        createUser=(Button)findViewById(R.id.create_usr);
        createUser.setOnClickListener(this);

        //set default selection
        yearSpinner.setSelection(0);
        deptSpinner.setSelection(0);

        //set support action bar
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        //validateInput fields
        if(validateInput())
        {

            //create a user object from data specified
            User newUser=new User(name.getText().toString().trim(),collegeName.getText().toString().trim(),
                    Integer.parseInt((String)yearSpinner.getSelectedItem()),(String)deptSpinner.getSelectedItem(),phoneNumber.getText().toString());

            //create a ref by UID which is unique for each firebase user
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/Participants/"+user.getUid());

            //set user data to firebase realtime database
            dr.setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                        //direct user to symposium activity
                        Intent intent=new Intent(UserDetailsActivity.this, SymposiumEvents.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //if error occurred prompt user to try again
                    Toast.makeText(UserDetailsActivity.this,"please try again an error occured",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //check for correct and relevant values in fields
    private boolean validateInput() {
        if(name.getText().toString().length()==0 || name.getText().toString().trim().length()==0)
        {
            name.setError("please enter a valid name");
            return false;
        }
        if(phoneNumber.getText().toString().length()==0 || phoneNumber.getText().toString().trim().length()==0 || phoneNumber.getText().toString().length()<=9)
        {
            phoneNumber.setError("please enter avaid name");
            return false;
        }
        if(collegeName.getText().toString().length()==0 || collegeName.getText().toString().trim().length()==0)
        {
            collegeName.setError("please enter a valid college_name");
            return false;
        }
        return true;
    }
}

