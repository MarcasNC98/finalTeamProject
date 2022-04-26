package com.example.pollingtest.GroceryList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pollingtest.Data.Info;
import com.example.pollingtest.Polls.PollActivity;
import com.example.pollingtest.R;
import com.example.pollingtest.chores.MainChoresActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class GroceryActivity extends AppCompatActivity {



    //FloatingActionButton class called fab_btn
    private FloatingActionButton fab_btn;
    private Button pollBtn, choresBtn;
    //DatabaseReference class called newDatabase
    private FirebaseDatabase newDatabase;
    private DatabaseReference newReference;
    private String homeID,retrieveID,userID;
    //FirebaseAuth class called newAuth
    private FirebaseAuth newAuth;
    RecyclerView recyclerView;
    ArrayList<Info> list;
    NewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocerylistapp);
        recyclerView = findViewById(R.id.main_list);

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewAdapter(this, list);
        recyclerView.setAdapter(adapter);

        choresBtn= findViewById(R.id.choresShift);

        //Returns an instance of FirebaseAuth and ties it to newAuth
        newAuth=FirebaseAuth.getInstance();

        //Creates a FirebaseUser class called newUser and ties it to newAuth.getCurrentUser that will retrieve the current users credentials
        FirebaseUser newUser=newAuth.getCurrentUser();



        //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
        String uId=newUser.getUid();
        userID=uId;
        //Returns an instance of FirebaseDatabase, references the child node "Grocery List" and the user ID in this node and assigns it to newDatabase
        newDatabase=FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");

        newReference=newDatabase.getReference();


        //Assigns the Floating Action Button with the id of 'fab' from 'grocerylistapp.xml to fab_btn
        fab_btn=findViewById(R.id.fab);
        pollBtn=findViewById(R.id.pollBtn);
            getData();
            newReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for(DataSnapshot dataSnapshot:snapshot.child("Homes").getChildren()){

                        //Info info = dataSnapshot.getValue(Info.class);
                        //list.add(info);
                        for(DataSnapshot secondSnapshot : snapshot.child("Homes").child(dataSnapshot.getKey()).child("groceryList").getChildren()){
                            Info info = secondSnapshot.getValue(Info.class);
                            list.add(info);

                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        //Creates an onClickListener that listens for the floating action button being clicked
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            //When clicked, the dialogBox view will be shown
            public void onClick(View view) {
                dialogBox();
            }
        });
        pollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PollActivity.class));
            }
        });
        choresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainChoresActivity.class));
            }
        });
    }

    private void getData(){

        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.

                homeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);

                // after getting the value we are setting
                // our value to our text view in below line.
                retrieveID=homeID;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
               // Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Dialog box for inputting grocery data
    private void dialogBox(){

        //Creates alert dialog on the homepage and assigns it to newDialog
        AlertDialog.Builder newDialog=new AlertDialog.Builder(GroceryActivity.this);
        //Creates a layout inflater from HomePage
        LayoutInflater inflater=LayoutInflater.from(GroceryActivity.this);
        //Inflates the 'input.xml' layout and assigns it to a view called newView
        View newView=inflater.inflate(R.layout.input,null);

        //Creates a new dialog box
        AlertDialog dialog=newDialog.create();
        //Sets this new dialog box to display newView aka the 'input.xml' layout
        dialog.setView(newView);

        //Assigns the field for a user to input the name of a grocery item with the ID input_text from 'input.xml' to the EditText text
        EditText text=newView.findViewById(R.id.input_text);
        //Assigns the field for a user to input the amount of a grocery item with the ID input_amount from 'input.xml' to the EditText amount
        EditText amount=newView.findViewById(R.id.input_amount);
        //Assigns the field for a user to input the price of a grocery item with the ID input_price from 'input.xml' to the EditText price
        EditText price=newView.findViewById(R.id.input_price);
        //Assigns the button that a user clicks to submit the data they've entered with the ID submit_btn from 'input.xml' to the Button submitBtn
        Button submitBtn=newView.findViewById(R.id.submit_btn);

        //Creates an onClickLister to listen for when the submitBtn is clicked
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //When clicked, a string called newText, newAmount and newPrice will be created. They will get the information from the EditText fields and when convert them to strings. When converted, trim will remove whitespice from before and after the data.
                String newText=text.getText().toString().trim();
                String newAmount=amount.getText().toString().trim();
                String newPrice=price.getText().toString().trim();

                //Because the EditText fields have been converted to strings, they now need to be converted into their respective data types. The amount field is converted into an Integer and named conAmount
                int conAmount=Integer.parseInt(newAmount);
                //The price field is converted to a double and named conPrice
                double conPrice=Double.parseDouble(newPrice);

                //Creates an error message if there is nothing entered in the text, amount or price fields that lets the user know nothing can be blank.
                if (TextUtils.isEmpty(newText)){
                    text.setError("Cannot be blank");
                    return;
                }
                if (TextUtils.isEmpty(newAmount)){
                    amount.setError("Cannot be blank");
                    return;
                }
                if (TextUtils.isEmpty(newPrice)){
                    price.setError("Cannot be blank");
                    return;
                }

                //String called id that pushes a key to the Firebase database
                String id=newReference.push().getKey();


                //Sends the date that the data was pushed
                String newDate= DateFormat.getDateInstance().format(new Date());

                //Ties the info entered in the input dialog box to the variables in info.java
                Info info=new Info(newDate, newText,conAmount,conPrice,id);


                //Returns an instance of FirebaseAuth and ties it to newAuth
                newAuth=FirebaseAuth.getInstance();
                //Creates a FirebaseUser class called newUser and ties it to newAuth.getCurrentUser that will retrieve the current users credentials
                FirebaseUser newUser=newAuth.getCurrentUser();
                //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
                String uId=newUser.getUid();



                //hello
                //Sets the values in info to the id that is pushed to the database
                newReference.child("Homes").child(retrieveID).child("groceryList").child(id).setValue(info)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Toast message that informs the user that the grocery item has been added
                                    Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                //The input dialog box is dismissed
                dialog.dismiss();
            }
        });

        //Shows the input dialog box
        dialog.show();
    }
}