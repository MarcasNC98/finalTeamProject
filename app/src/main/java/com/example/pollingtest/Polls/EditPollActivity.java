package com.example.pollingtest.Polls;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pollingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditPollActivity extends AppCompatActivity {

    // creating variables for our edit text, firebase database,
    // database reference, Poll rv modal,progress bar.
    private TextInputEditText PollNameEdt, PollDescEdt, PollImgEdt, Option1Edt, Option2Edt, Option3Edt;
    private Button updatePollBtn, deletePollBtn;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String homeID, retrieveID, userID;
    private FirebaseAuth mAuth;
    private PollRVModal pollRVModal;
    private ProgressBar loadingPB;
    // creating a string for our Poll id.
    private String pollID;
    private Integer votes1, votes2, votes3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_poll);
        // initializing all our variables on below line.
        updatePollBtn = findViewById(R.id.idBtnVote);
        deletePollBtn = findViewById(R.id.idBtnDeletePoll);
        PollNameEdt = findViewById(R.id.idEdtPollName);
        PollDescEdt = findViewById(R.id.idEdtPollDescription);
        PollImgEdt = findViewById(R.id.idEdtPollImageLink);
        Option1Edt = findViewById(R.id.idEdtOption1);
        Option2Edt = findViewById(R.id.idEdtVoteOption2);
        Option3Edt = findViewById(R.id.idEdtOption3);
        loadingPB = findViewById(R.id.idPBLoading);
        votes1 = 0;
        votes2 = 0;
        votes3 = 0;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser newUser = mAuth.getCurrentUser();


        //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
        String uId = newUser.getUid();
        userID = uId;
        firebaseDatabase = FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference();
        // setting pollRVModal as the poll list
        pollRVModal = getIntent().getParcelableExtra("poll");
        // on below line we are initialing our database reference and we are adding a child as our Poll id.

        //if the modal is not empty set all the text fields as the data that was entered previously and is stored in the array list all ready
        if (pollRVModal != null) {
            // on below line we are setting data to our edit text from our modal class.
            PollNameEdt.setText(pollRVModal.getPollName());
            PollImgEdt.setText(pollRVModal.getPollImg());
            PollDescEdt.setText(pollRVModal.getPollDescription());
            Option1Edt.setText(pollRVModal.getOption1());
            Option2Edt.setText(pollRVModal.getOption2());
            Option3Edt.setText(pollRVModal.getOption3());
            pollID = pollRVModal.getPollId();
        }

        getData();

        // on below line we are adding click listener for our update Poll button.
        updatePollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pollName = PollNameEdt.getText().toString();
                String pollDesc = PollDescEdt.getText().toString();
                String pollImg = PollImgEdt.getText().toString();
                String option1 = Option1Edt.getText().toString();
                String option2 = Option2Edt.getText().toString();
                String option3 = Option3Edt.getText().toString();
                

                updatePoll(pollName, pollDesc, pollImg, option1, option2, option3, pollID);

            }
        });

        // adding a click listener for our delete Poll button.
        deletePollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deletePoll(pollID);
            }
        });
    }
// this method will delete a poll with the poll ID
    private void deletePoll(String pollID) {


        databaseReference = firebaseDatabase.getInstance().getReference("Homes").child(retrieveID).child("polls");
        databaseReference.child(pollID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditPollActivity.this, "Poll deleted..", Toast.LENGTH_SHORT).show();
                    // opening a new activity after updating our coarse.
                    startActivity(new Intent(EditPollActivity.this, PollActivity.class));

                } else {
                    Toast.makeText(EditPollActivity.this, "Poll failed to delete..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
//this method reads in all the new data and saves it in a hashmap with the corresponding location, then it is pushed to update the current pollID with this data
    private void updatePoll(String pollName, String pollDesc, String pollImg, String option1, String option2, String option3, String pollID) {

        loadingPB.setVisibility(View.VISIBLE);
        // on below line we are creating a map for
        // passing a data using key and value pair.
        Map<String, Object> map = new HashMap<>();
        map.put("pollName", pollName);
        map.put("pollDescription", pollDesc);
        map.put("pollImg", pollImg);
        map.put("option1", option1);
        map.put("option2", option2);
        map.put("option3", option3);
        map.put("pollId", pollID);
        databaseReference = firebaseDatabase.getInstance().getReference("Homes").child(retrieveID).child("polls");
        databaseReference.child(pollID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditPollActivity.this, "Poll Updated..", Toast.LENGTH_SHORT).show();
                    // opening a new activity after updating our coarse.
                    startActivity(new Intent(EditPollActivity.this, PollActivity.class));

                } else {
                    Toast.makeText(EditPollActivity.this, "Poll failed to Updated..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.

                homeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);

                // after getting the value we are setting
                // our value to our text view in below line.
                retrieveID = homeID;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


