package com.example.pollingtest.chores;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollingtest.GroceryList.GroceryActivity;
import com.example.pollingtest.Login.LoginActivity;
import com.example.pollingtest.Polls.PollActivity;
import com.example.pollingtest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class MainChoresActivity extends AppCompatActivity {

    private RecyclerView mUserRV;
    private FloatingActionButton mAddChoreFAB;
    private TextView mNoChoresTV;
    private TextView mNoChoresArrowTV;
    private Button grocery, chores, polls;
    private FirebaseAuth newAuth;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chores);

        mUserRV = (RecyclerView) findViewById(R.id.usersRecyclerView);
        mAddChoreFAB = (FloatingActionButton) findViewById(R.id.addChoresFAB);
        mNoChoresTV = (TextView) findViewById(R.id.noChoresTV);
        mNoChoresArrowTV = (TextView) findViewById(R.id.noChoresArrowTV);
        grocery = (Button) findViewById(R.id.chores_grocery_btn);
        chores = (Button) findViewById(R.id.chores_chores_btn);
        polls = (Button) findViewById(R.id.chores_polls_btn);
        newAuth=FirebaseAuth.getInstance();





        FirebaseDatabaseHelper firebaseDatabase = new FirebaseDatabaseHelper();

        firebaseDatabase.readData(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<User> users, String houseID, String check) {
                //Show/Hide contents based if there is data available or not
                findViewById(R.id.choresProgressBar).setVisibility(View.GONE);
                mNoChoresTV.setVisibility(View.VISIBLE);
                mNoChoresArrowTV.setVisibility(View.VISIBLE);
                mUserRV.setVisibility(View.GONE);
                for(User user : users){
                    if(!user.getChoreList().isEmpty()){
                        mNoChoresTV.setVisibility(View.GONE);
                        mNoChoresArrowTV.setVisibility(View.GONE);
                        mUserRV.setVisibility(View.VISIBLE);
                        break;
                    }
                }

                //Check if a new day has passed
                firebaseDatabase.checkPriority(users, houseID, check);

//                System.out.println(">>>> Users: "+ users + ", houseID: "+houseID+", check: "+check);

                new UsersRVConfig().setUsersConfig(mUserRV, MainChoresActivity.this, users, houseID);

                mAddChoreFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainChoresActivity.this, AddChoreActivity.class);
                        Bundle bundle = new Bundle();
                        intent.putExtra("houseID", houseID);
                        bundle.putParcelableArrayList("usersList", (ArrayList<? extends Parcelable>) users);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataUpdated() {

            }

            @Override
            public void DataDeleted() {

            }
        });

        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainChoresActivity.this, GroceryActivity.class));
                finish();
            }
        });

        polls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainChoresActivity.this, PollActivity.class));
                finish();
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // adding a click listener for option selected on below line.
        int id = item.getItemId();
        switch (id) {
            case R.id.idLogOut:
                // displaying a toast message on user logged out inside on click.
                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                // on below line we are signing out our user.
                newAuth.signOut();
                // on below line we are opening our login activity.
                Intent i = new Intent(MainChoresActivity.this, LoginActivity.class);
                startActivity(i);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // on below line we are inflating our menu
        // file for displaying our menu options.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
}