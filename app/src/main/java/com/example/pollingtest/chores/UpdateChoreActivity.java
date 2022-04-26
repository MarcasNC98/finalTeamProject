package com.example.pollingtest.chores;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pollingtest.R;

import java.time.LocalDate;
import java.util.List;

public class UpdateChoreActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button mUpdateBtn;
    private Button mCancelBtn;
    private EditText mChoreName;
    private int mChorePriority;
    private TextView mPriorityTV;
    private LocalDate newDate;

    private Spinner prioritySpinner;


    private String oldName;
    private String oldPriority;
    private String oldDate;
    private String choreID;
    private String houseID;
    private String userID;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_chore);

        oldName = getIntent().getStringExtra("name");
        oldPriority = getIntent().getStringExtra("priority");
        oldDate = getIntent().getStringExtra("date");
        choreID = getIntent().getStringExtra("id");
        houseID = getIntent().getStringExtra("houseID");
        userID = getIntent().getStringExtra("userID");

        mChoreName = (EditText) findViewById(R.id.choreNameED);
        mChoreName.setText(oldName);
        mPriorityTV = (TextView) findViewById(R.id.priorityTV);
        newDate = LocalDate.now();

        mCancelBtn = (Button) findViewById(R.id.cancelBtn);
        mUpdateBtn = (Button) findViewById(R.id.updateChoreBtn);

        //Spinner setup
        prioritySpinner = findViewById(R.id.prioritySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.prioritySpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
        prioritySpinner.setOnItemSelectedListener(this);

        if(Integer.parseInt(oldPriority)>10){//If chore has a higher priority than 10 it means it was not done and that it cannot be changed.
            prioritySpinner.setVisibility(View.GONE);
            mPriorityTV.setVisibility(View.GONE);
        }
        else {
            prioritySpinner.setSelection(getIndexSpinnerItem(Integer.parseInt(oldPriority)));
        }

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoreWithID chore = new ChoreWithID(mChoreName.getText().toString(),mChorePriority,newDate.toString(),choreID);//TODO: complete tutorial from here

                new FirebaseDatabaseHelper().updateChore(houseID, userID, chore.getId(), chore, new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<User> users, String houseID, String check) {

                    }

                    @Override
                    public void DataIsInserted() {

                    }

                    @Override
                    public void DataUpdated() {
                        Toast.makeText(UpdateChoreActivity.this, "Chore has been successfully updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void DataDeleted() {

                    }
                });
                finish();return;
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();return;
            }
        });

    }



    private int getIndexSpinnerItem(int i){
        int index =0;

        if(i==10){
            index = 0;
        }else if(i==9){
            index = 1;
        }else if(i==8){
            index = 2;
        }else if(i==7){
            index = 3;
        }else if(i==6){
            index = 4;
        }else if(i==5){
            index = 5;
        }
        return index;
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