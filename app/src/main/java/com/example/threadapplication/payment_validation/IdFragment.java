package com.example.threadapplication.payment_validation;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.threadapplication.R;
import com.example.threadapplication.user_validation.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class IdFragment extends Fragment implements View.OnClickListener {


    public IdFragment() {
    }
    User current;
    Payment payId;
    TextView name;
    TextView phoneNumber;
    TextView payment;
    Button payBtn;
    ImageView paymentImage;
    ProgressBar pb;
    FirebaseUser user;
    //current user db reference
    DatabaseReference dr;

    TextView progressText;
    SwipeRefreshLayout dataRefresh;
    public static final int UPI_PAYMENT=10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_id, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //gear up listeners
        wireUpListeners(view);

        //fetch payment info from remote db
        updateInfoFromDb();
    }

    private void updateInfoFromDb() {
        pb.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        //fetch current payment info
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current=dataSnapshot.getValue(User.class);

                //set them to the widgets
                setValues(current);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pb.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setValues(User current) {
        phoneNumber.setText("Phone_number :"+current.getPhoneNumber());
        name.setText("Name :"+current.getName());

        //widgets appear based on user payment info
        if(current.isPayment())
        {
            paymentImage.setImageResource(R.drawable.paid);
            payment.setText("Payment :Completed");
            payBtn.setVisibility(View.GONE);
        }
        else
        {
            paymentImage.setImageResource(R.drawable.no_pay);
            payment.setText("Payment :Not_completed");
            payBtn.setVisibility(View.VISIBLE);
        }
        pb.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);


    }

    private void wireUpListeners(View view) {
        name=(TextView) view.findViewById(R.id.name);
        phoneNumber=(TextView) view.findViewById(R.id.phone_number);
        payment=(TextView) view.findViewById(R.id.payment_label);
        payBtn=(Button) view.findViewById(R.id.pay);
        pb=(ProgressBar)view.findViewById(R.id.progress);
        paymentImage=(ImageView)view.findViewById(R.id.img_pay);

        //paybtn
        payBtn.setOnClickListener(this);
        user= FirebaseAuth.getInstance().getCurrentUser();

        //a reference of the current user if not available is created
        dr=FirebaseDatabase.getInstance().getReference("Symposium/Participants/"+user.getUid());

        progressText=(TextView)view.findViewById(R.id.progress_text);

    }
    @Override
    public void onClick(View v) {

        //check for connection availablity
        if(checkIsConnectionAvailable()) {

            //get payment reciever info from db
            getIdFromDb();
            if(payId!=null)
                payForSympo();
        }
        else
            Toast.makeText(getContext(),"please connect to a network",Toast.LENGTH_SHORT).show();
    }

    //get payment reciever info from firebase
    private void getIdFromDb() {


        DatabaseReference dr=FirebaseDatabase.getInstance().getReference("Symposium/PaymentReciever");

        //get payment receiver UPI ID
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                payId=dataSnapshot.getValue(Payment.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void payForSympo() {

        //create a uri with upiID,receiver name,amount and currency
        Uri uri= Uri.parse("upi://pay").buildUpon().appendQueryParameter("pa",payId.upiId).appendQueryParameter("pn",payId.name)
                .appendQueryParameter("tn",payId.note).appendQueryParameter("am",payId.amount)
                .appendQueryParameter("cu","INR").build();
        //create an intent
        Intent upiIntent=new Intent(Intent.ACTION_VIEW);
        upiIntent.setData(uri);

        //let user choose the app to pay with
        Intent chooser=Intent.createChooser(upiIntent,"pay with");
        if(chooser.resolveActivity(getActivity().getPackageManager())!=null)
            startActivityForResult(chooser,UPI_PAYMENT);
        else
            Toast.makeText(getContext(),"please install a upi payment application",Toast.LENGTH_SHORT).show();
    }
    private boolean checkIsConnectionAvailable()
    {
        ConnectivityManager connectManager=(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectManager!=null)
        {
            NetworkInfo netinfo=connectManager.getActiveNetworkInfo();
            if(netinfo!=null && netinfo.isConnected())
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case UPI_PAYMENT:
                            if(resultCode==RESULT_OK || requestCode==UPI_PAYMENT)
                            {
                                //get response and update them to the user and payment info in db
                                if(data!=null)
                                {
                                    String res=data.getStringExtra("response");
                                    upiDataPaymentOperation(res);
                                }
                                else
                                {

                                        String res="data is null";
                                        upiDataPaymentOperation(res);
                                }
                            }
                            else
                            {
                                        String res="result code not ok";
                                        upiDataPaymentOperation(res);

                            }

        }
    }

    private void upiDataPaymentOperation(String res) {
            progressText.setVisibility(View.VISIBLE);
            pb.setVisibility(View.VISIBLE);
            String status="";
            String approvalRefNo="";
            //response contains status,approvalrefno,txnref separated by &
            String response[]=res.split("&");
            for(int i=0;i<response.length;i++)
            {
                //separate key and their corresponding values
                String eqstr[]=response[i].split("=");
                if(eqstr.length>=2)
                {
                    if(eqstr[0].toLowerCase().equals("status"))
                        status=eqstr[1].toLowerCase();
                    if(eqstr[0].toLowerCase().equals("approvalrefno") ||
                        eqstr[0].toLowerCase().equals("txnref"))
                        approvalRefNo=eqstr[1].toLowerCase();
                }
            }

            //display response to user
            if (status.equals("success")) {
                Toast.makeText(getContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();
                updateDataToBase(true);
            }
            else {
                pb.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
    }

    void updateDataToBase(boolean payment)
    {
        //update payment info after a transaction to db
        dr.child("payment").setValue(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                current.setPayment(true);
                setValues(current);
                pb.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "unable to update database please connect to network", Toast.LENGTH_SHORT).show();
            }
        });

    }


}

