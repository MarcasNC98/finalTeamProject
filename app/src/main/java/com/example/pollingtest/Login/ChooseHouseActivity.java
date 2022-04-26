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
//    private FirebaseAuth newAuth;
//    private FirebaseUser newUser;
    private String uId;
    private LocalDate today;

    ArrayList<String> homes = new ArrayList<>();

    public interface FirebaseCallBack{
        void onCallBack(ArrayList<String> homesIDs);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_house);
        System.out.println("<><><><>> 1 On Create()");

        createButton = findViewById(R.id.createHouse);
        joinButton = findViewById(R.id.joinHouse);

        newDatabase = FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        newReference = newDatabase.getReference();

//        newAuth = FirebaseAuth.getInstance();//Returns an instance of FirebaseAuth and ties it to newAuth
//        newUser = newAuth.getCurrentUser();//Creates a FirebaseUser class called newUser and ties it to newAuth.getCurrentUser that will retrieve the current users credentials
        uId = getIntent().getStringExtra("uId");//Gets the string passed through the intent.

        today = LocalDate.now();

        createButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            //When clicked, the dialogBox view will be shown
            public void onClick(View view) {
                System.out.println("<><><><>> 2 CreateBtn.OnClick()");
                houseID = newReference.push().getKey();
                newReference.child("Homes").child(houseID).child("check").setValue(today.toString());
                newReference.child("Homes").child(houseID).child("tenants").child(uId).child("joinDate").setValue(today.toString());
                newReference.child("NewUsers").child(uId).child("home").setValue(houseID);

                startActivity(new Intent(getApplicationContext(), GroceryActivity.class));
                finish();//will end the current activity allowing the user to go back
            }
        });


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //When clicked, the dialogBox view will be shown
            public void onClick(View view) {
                System.out.println("<><><><>> 3 joinBtn.OnClick()");
                dialogBox();
            }
        });
    }

    private void dialogBox(){
        System.out.println("<><><><>> 4 dialogBox()");
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
        System.out.println("<><><><>> 4 dialogBox().dialogShow()");

        //https://www.youtube.com/watch?v=OvDZVV5CbQg
        getData(new FirebaseCallBack() {//Wait for the async database then grab all the homes and store them in an array list called "homes".
            @Override
            public void onCallBack(ArrayList<String> homesIDs) {
                homes = homesIDs;
            }
        });

        //Creates an onClickLister to listen for when the submitBtn is clicked
        joinHBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("<><><><>> 4 > 5 JoinHBtn.OnClick()");
                //When clicked, a string called newText, newAmount and newPrice will be created. They will get the information from the EditText fields and when convert them to strings. When converted, trim will remove whitespice from before and after the data.
                String newCode=code.getText().toString().trim();
                homeIDInput = newCode;
                System.out.println(">>>> homeIdInput: "+homeIDInput);


                //Creates an error message if there is nothing entered in the text, amount or price fields that lets the user know nothing can be blank.
                if (TextUtils.isEmpty(newCode)){
                    code.setError("Cannot be blank");
                    return;
                }


                for(String home:homes){//loop through all the homes
                    if (homeIDInput.equals(home)){//if the home input matches with one of the home String in the array
                        newReference.child("NewUsers").child(uId).child("home").setValue(home);
                        newReference.child("Homes").child(home).child("tenants").child(uId).child("joinDate").setValue(today.toString());
                        finish();//end the current activity.
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

    private void getData(final FirebaseCallBack firebaseCallBack){
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
                ArrayList<String> homeIDs = new ArrayList<>();
                for(DataSnapshot getHomesID: snapshot.child("Homes").getChildren()) {
                    homeIDs.add(getHomesID.getKey());
                }
                firebaseCallBack.onCallBack(homeIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                // Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }




}