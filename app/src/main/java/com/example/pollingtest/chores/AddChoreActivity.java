package com.example.pollingtest.chores;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pollingtest.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddChoreActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button mAddBtn;
    private Button mCancelBtn;
    private EditText mChoreName;
    private int mChorePriority;
    private LocalDate date;
    private String houseID;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chore);

        mChoreName = (EditText) findViewById(R.id.choreNameED);
        date = LocalDate.now();

        mCancelBtn = (Button) findViewById(R.id.cancelBtn);
        mAddBtn = (Button) findViewById(R.id.updateChoreBtn);

        houseID = getIntent().getStringExtra("houseID");

        ArrayList<User> users = getIntent().getParcelableArrayListExtra("usersList");

        //Spinner setup
        Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.prioritySpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(this);



        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: create the algorithm in here.

                //calculate the total priorities
                ArrayList<User> prioritiesList = new ArrayList<>();
                for(User user: users){
                    int totalPriority = 0;
                    List<ChoreWithID> chores = user.getChoreList();
                    for(ChoreWithID chore: chores){ //calculate the total priority for each user
                        totalPriority += chore.getPriority();
                    }
                    user.setTotalPriority(totalPriority);

                    addToList(prioritiesList, user, user.getTotalPriority());//Sort the list
                }
                //find the lowest and the highest
                User userLowestPriorityTotal = prioritiesList.get(0);
                User userHighestPriorityTotal = prioritiesList.get(prioritiesList.size()-1);


                //find if everyone has the same amount of chores
                boolean equalChoresAmount = true;
                for(User user :users){
                    if(users.get(0).getChoreList().size()!=user.getChoreList().size()){//Get the size of the first user and compare it with the size of the rest. if they don't match then not all users have the same amount of chores.
                        equalChoresAmount=false;
                        break;
                    }
                }

                //Adding the chore
                String userID;
                if(mChorePriority == 10 || mChorePriority == 9 || !equalChoresAmount) {
                    userID = userLowestPriorityTotal.getId();
                    Chore chore = new Chore(mChoreName.getText().toString(), mChorePriority, date.toString());//Creates the new chore with the values in the current activity.
                    new FirebaseDatabaseHelper().addChore(houseID, userID, chore, new FirebaseDatabaseHelper.DataStatus() {//TODO Always update the username in here to one that exists in order to add chores.
                        @Override
                        public void DataIsLoaded(List<User> users, String houseID, String check) {

                        }

                        @Override
                        public void DataIsInserted() {
                            Toast.makeText(AddChoreActivity.this, "Chore successfully added!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DataUpdated() {

                        }

                        @Override
                        public void DataDeleted() {

                        }
                    });
                }
                else{
                    userID = userHighestPriorityTotal.getId();
                    Chore chore = new Chore(mChoreName.getText().toString(), mChorePriority, date.toString());//Creates the new chore with the values in the current activity.
                    new FirebaseDatabaseHelper().addChore(houseID, userID, chore, new FirebaseDatabaseHelper.DataStatus() {//TODO Always update the username in here to one that exists in order to add chores.
                        @Override
                        public void DataIsLoaded(List<User> users, String houseID, String check) {

                        }

                        @Override
                        public void DataIsInserted() {
                            Toast.makeText(AddChoreActivity.this, "Chore successfully added!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void DataUpdated() {

                        }

                        @Override
                        public void DataDeleted() {

                        }
                    });
                }

                finish();
                return;
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;//stop executing anything else
            }
        });
    }

    //method used to add and sort items in  an array list.
    public void addToList(ArrayList<User> list, User user, int num){
        boolean found = false;
        int position = 0;

        while (position < list.size() && !found) {
            list.get(position);

            if (list.get(position).getTotalPriority() < num) {
                position = position + 1;
            } else {
                found = true;
            }
        }

        if (position == list.size()) {
            list.add(user);
        } else {
            list.add(position, user);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i==0){
            mChorePriority = 10;
        }else if(i==1){
            mChorePriority = 9;
        }else if(i==2){
            mChorePriority = 8;
        }else if(i==3){
            mChorePriority = 7;
        }else if(i==4){
            mChorePriority = 6;
        }else if(i==5){
            mChorePriority = 5;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}