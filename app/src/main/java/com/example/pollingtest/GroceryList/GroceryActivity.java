package com.example.pollingtest.GroceryList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.pollingtest.Login.LoginActivity;
import com.example.pollingtest.Polls.AddPollActivity;
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
    //Buttons for navigating to the other functionalities of the application
    private Button pollBtn, choresBtn;
    //FirebaseDatabase and DatabaseReference classes used to access the data inside Firebase
    private FirebaseDatabase newDatabase;
    private DatabaseReference newReference;
    //ID classes for the homeID and userID given to users and homes in the Firebase database. RetrieveID is used to differentiate between different users in different houses.
    private String homeID,retrieveID,userID;
    //FirebaseAuth class called newAuth
    private FirebaseAuth newAuth;
    //Public methods that tie my recyclerView and arraylist used to show the data retrieved from Firebase in the application
    RecyclerView recyclerView;
    ArrayList<Info> list;
    NewAdapter adapter;



    //Waits for the asynchronous Firebase method to complete before getting the retrieveID
    public interface NewCallback {
        void onCallBack(String retrieveID);
    }

    //Constructor that handles referencing the Firebase database
    public GroceryActivity(){
        //Returns an instance of FirebaseAuth and ties it to newAuth
        newAuth=FirebaseAuth.getInstance();
        //Creates a FirebaseUser class called newUser and ties it to newAuth.getCurrentUser that will retrieve the current users credentials
        FirebaseUser newUser=newAuth.getCurrentUser();
        //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
        String uId=newUser.getUid();
        userID=uId;
        //Returns an instance of FirebaseDatabase, references the child node "Grocery List" and the user ID in this node and assigns it to newDatabase
        newDatabase=FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        //newReference gets the reference in the database
        newReference=newDatabase.getReference();

        //getData method used to retrieve the homeID that was sent to the database
        getData(new GroceryActivity.NewCallback() {

            @Override
            public void onCallBack( String homeID) {
                retrieveID = homeID;

            }
        });
    }

    //Tried to add a function where you can press on an item to remove it from the database, marking it as purchased. Couldn't get the 'retrieveID' so the function doesn't work properly.
    public void deleteGrocery(String newId){
        newReference.child("Homes").child(retrieveID).child("groceryList").child(newId).setValue(null);
    }


    //Asynchronous method that retrieves the data from the firebase database
    private void getData(final NewCallback newCallback){
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //setting homeID as the value inserted in 'home' under a usersID in the database
                homeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);

                //callback waiting for the homeID to be retrieved
                newCallback.onCallBack(homeID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting the view as the grocerylistapp xml layout
        setContentView(R.layout.grocerylistapp);
        //Setting recyclerView as the layout in the main_list xml layoutfile
        recyclerView = findViewById(R.id.main_list);
        //Creating a new instance of list ArrayList
        list = new ArrayList<>();
        //Setting the recyclerview layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Setting adapter to be the ArrayList list
        adapter = new NewAdapter(this, list);
        //Setting the recyclerView as adapter
        recyclerView.setAdapter(adapter);
        //Button to navigate to chores
        choresBtn= findViewById(R.id.grocery_chores_btn);
        //Assigns the Floating Action Button with the id of 'fab' from 'grocerylistapp.xml to fab_btn
        fab_btn=findViewById(R.id.fab);
        //Button to navigate to polls
        pollBtn=findViewById(R.id.grocery_polls_btn);

            //Adding a valueEventListener for the databse reference
            newReference.addValueEventListener(new ValueEventListener() {
                @Override
                //Shows the grocery data stored in the database depending on if the data is stored in the house the user is a member of
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    String userHomeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);
                    for (DataSnapshot secondSnapshot : snapshot.child("Homes").child(userHomeID).child("groceryList").getChildren()) {
                        Info info = secondSnapshot.getValue(Info.class);
                        list.add(info);
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
        //OnClickListener that sends the user to the Poll functionality of the application
        pollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PollActivity.class));
                finish();
            }
        });
        //OnClickListener that sends the user to the Chores functionality of the application
        choresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainChoresActivity.class));
                finish();
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

                //Because the EditText fields have been converted to strings, they now need to be converted into their respective data types. The amount field is converted into an Integer and named conAmount
                int conAmount=Integer.parseInt(newAmount);
                //The price field is converted to a double and named conPrice
                double conPrice=Double.parseDouble(newPrice);

                //String called id that pushes a key to the Firebase database
                String id=newReference.push().getKey();

                //Sends the date that the data was pushed
                String newDate= DateFormat.getDateInstance().format(new Date());

                //Ties the info entered in the input dialog box to the variables in info.java
                Info info=new Info(newDate, newText,conAmount,conPrice,id);

                //Returns an instance of FirebaseAuth and ties it to newAuth
                newAuth=FirebaseAuth.getInstance();

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

    //Options menu used to log a user out of the application
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Adding an click listener for option selected on below line
        int id = item.getItemId();
        switch (id) {
            case R.id.idLogOut:
                //Displaying a toast message on user logged out inside on click
                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                //Signing out the user.
                newAuth.signOut();
                //Opening the login activity, sending the user back to log in screen
                Intent i = new Intent(GroceryActivity.this, LoginActivity.class);
                startActivity(i);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating the menu
        //Displaying the menu options.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
}