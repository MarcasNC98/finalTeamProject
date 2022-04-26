package com.example.pollingtest.chores;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollingtest.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainChoresActivity extends AppCompatActivity {

    private RecyclerView mUserRV;
    private FloatingActionButton mAddChoreFAB;
    private TextView mNoChoresTV;
    private TextView mNoChoresArrowTV;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chores);

        mUserRV = (RecyclerView) findViewById(R.id.usersRecyclerView);
        mAddChoreFAB = (FloatingActionButton) findViewById(R.id.addChoresFAB);
        mNoChoresTV = (TextView) findViewById(R.id.noChoresTV);
        mNoChoresArrowTV = (TextView) findViewById(R.id.noChoresArrowTV);

        new FirebaseDatabaseHelper().readData(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<User> users, String houseID) {
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


    }
}