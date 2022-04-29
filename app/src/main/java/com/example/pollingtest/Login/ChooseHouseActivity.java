package com.example.pollingtest.Login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.pollingtest.Data.Info;
import com.example.pollingtest.GroceryList.GroceryActivity;
import com.example.pollingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChooseHouseActivity extends AppCompatActivity {

    private Button createButton;
    private Button joinButton;
    private FirebaseDatabase newDatabase;
    private DatabaseReference newReference;
    private String homeIDInput;
    private String houseID;
    private String uId;
    private LocalDate today;

    ArrayList<String> homes = new ArrayList<>();

    //https://www.youtube.com/watch?v=OvDZVV5CbQg
    //Wait for the async database then grab all homesID's
    public interface FirebaseCallBack{
        void onCallBack(ArrayList<String> homesIDs);
    }

    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_house);
        //Buttons to create or join a house
        createButton = findViewById(R.id.createHouse);
        joinButton = findViewById(R.id.joinHouse);
        //Database reference
        newDatabase = FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        newReference = newDatabase.getReference();
        //Gets the string passed through the intent from registration activity
        uId = getIntent().getStringExtra("uId");
        //Variable used to get todays date
        today = LocalDate.now();

        createButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            //When clicked, a houseID will be created and pushed to the database
            public void onClick(View view) {
                houseID = newReference.push().getKey();
                //Sets the check value to todays date, needs to be used in the chores activity
                newReference.child("Homes").child(houseID).child("check").setValue(today.toString());
                //Sets the join date to todays date
                newReference.child("Homes").child(houseID).child("tenants").child(uId).child("joinDate").setValue(today.toString());
                //Gives the user a home variable with the houseID, used to tie users to specific houses
                newReference.child("NewUsers").child(uId).child("home").setValue(houseID);
                //Moves the user to the grocery activity
                startActivity(new Intent(getApplicationContext(), GroceryActivity.class));
                //will end the current activity allowing the user to go back
                finish();
            }
        });


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //When clicked, the dialogBox view will be shown
            public void onClick(View view) {
                dialogBox();
            }
        });
    }

    private void dialogBox(){
        //Creates alert dialog on the homepage and assigns it to newDialog
        AlertDialog.Builder newDialog=new AlertDialog.Builder(ChooseHouseActivity.this);
        //Creates a layout inflater from HomePage
        LayoutInflater inflater=LayoutInflater.from(ChooseHouseActivity.this);
        //Inflates the 'input.xml' layout and assigns it to a view called newView
        View newView=inflater.inflate(R.layout.house_input,null);
        //Creates a new dialog box
        AlertDialog dialog=newDialog.create();
        //Sets this new dialog box to display newView aka the 'input.xml' layout
        dialog.setView(newView);
        //Assigns the field for a user to input the name of a grocery item with the ID input_text from 'input.xml' to the EditText text
        EditText code=newView.findViewById(R.id.input_code);
        //Assigns the button that a user clicks to submit the data they've entered with the ID submit_btn from 'input.xml' to the Button submitBtn
        Button joinHBtn=newView.findViewById(R.id.joinHouse_btn);
        //Shows the input dialog box
        dialog.show();

        //https://www.youtube.com/watch?v=OvDZVV5CbQg
        //Wait for the async database then grab all the homes and store them in an array list called "homes".
        getData(new FirebaseCallBack() {
            @Override
            public void onCallBack(ArrayList<String> homesIDs) {
                homes = homesIDs;
            }
        });

        //Creates an onClickLister to listen for when the submitBtn is clicked
        joinHBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCode=code.getText().toString().trim();
                homeIDInput = newCode;

                //Creates an error message if there is nothing entered in the text, amount or price fields that lets the user know nothing can be blank
                if (TextUtils.isEmpty(newCode)){
                    code.setError("Cannot be blank");
                    return;
                }

                //Loop through all the homes
                for(String home:homes){
                    //If the home input matches with one of the home Strings in the array
                    if (homeIDInput.equals(home)){
                        newReference.child("NewUsers").child(uId).child("home").setValue(home);
                        newReference.child("Homes").child(home).child("tenants").child(uId).child("joinDate").setValue(today.toString());
                        //Ends the current activity.
                        finish();
                        startActivity(new Intent(getApplicationContext(), GroceryActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "House doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }
                //The input dialog box is dismissed
                dialog.dismiss();
            }
        });
    }

    //Method to get the homeID data
    private void getData(final FirebaseCallBack firebaseCallBack){
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //homeId's ArrayList
                ArrayList<String> homeIDs = new ArrayList<>();
                //Goes through the database and adds the key of the children of 'homes' to the homeID's ArrayList
                for(DataSnapshot getHomesID: snapshot.child("Homes").getChildren()) {
                    homeIDs.add(getHomesID.getKey());
                }
                firebaseCallBack.onCallBack(homeIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }




}