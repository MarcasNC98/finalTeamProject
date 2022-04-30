package com.example.pollingtest.Polls;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Date;

public class VotingActivity extends AppCompatActivity {

    private String Option1Edt, Option2Edt, Option3Edt;
    private PollRVModal pollRVModal;
    private TextView opt1, opt2, opt3;
    private String pollID, opti1, opti2, opti3;
    private String mHomeID, userID;
    private int votes1;
    private int votes2;
    private int votes3;
    private Button voteBtn1, voteBtn2, voteBtn3;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, DatabaseRef;
    private ProgressBar loadingPB;

    ArrayList<String> VoteCount = new ArrayList<>();

    public interface FirebaseCallBack {
        void onCallBack(ArrayList<String> Votes, String homeID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        // initializing all our variables.

        voteBtn1 = findViewById(R.id.idBtnVote1);
        voteBtn2 = findViewById(R.id.idBtnVote2);
        voteBtn3 = findViewById(R.id.idBtnVote3);
        opt1 = (TextView) findViewById(R.id.opt1);
        opt2 = (TextView) findViewById(R.id.opt2);
        opt3 = (TextView) findViewById(R.id.opt3);
        loadingPB = findViewById(R.id.idPBLoading);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser newUser = mAuth.getCurrentUser();

        //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
        String uId = newUser.getUid();
        userID = uId;

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        // on below line we are getting our modal class on which we have passed.
        pollRVModal = getIntent().getParcelableExtra("poll");

        if (pollRVModal != null) {
            // on below line we are setting data to our edit text from our modal class.
            opt1.setText(pollRVModal.getOption1());
            opt2.setText(pollRVModal.getOption2());
            opt3.setText(pollRVModal.getOption3());
            pollID = pollRVModal.getPollId();
            System.out.println(pollID + "this is poll ID");
        }
        // adding click listener for our vote buttons.

        getData(new FirebaseCallBack() {

            @Override
            public void onCallBack(ArrayList<String> Votes, String homeID) {

                mHomeID = homeID;
                VoteCount = Votes;
            }
        });

        voteBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getVotes1();

            }
        });
        voteBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getVotes2();

            }
        });
        voteBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getVotes3();
            }
        });

    }

    private void getVotes1() {
        votes1 = Integer.parseInt(VoteCount.get(VoteCount.size() - 3)) + 1;


        databaseReference = firebaseDatabase.getInstance().getReference("Homes").child(mHomeID).child("polls").child(pollID);

        loadingPB.setVisibility(View.VISIBLE);
        databaseReference.child("votes1").setValue(votes1);
        loadingPB.setVisibility(View.GONE);

        // on below line we are displaying a toast message.
        Toast.makeText(VotingActivity.this, "Votes Added..", Toast.LENGTH_SHORT).show();
        votes1 = Integer.parseInt(VoteCount.get(VoteCount.size() - 3)) + 1;
        votes2 = Integer.parseInt(VoteCount.get(VoteCount.size() - 2));
        votes3 = Integer.parseInt(VoteCount.get(VoteCount.size() - 1));
        opti1 = pollRVModal.getOption1();
        opti2 = pollRVModal.getOption2();
        opti3 = pollRVModal.getOption3();

        dialogBox(votes1, votes2, votes3, opti1, opti2, opti3);

    }

    private void getVotes2() {

        votes2 = Integer.parseInt(VoteCount.get(VoteCount.size() - 2)) + 1;


        databaseReference = firebaseDatabase.getInstance().getReference("Homes").child(mHomeID).child("polls").child(pollID);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        loadingPB.setVisibility(View.VISIBLE);
        databaseReference.child("votes2").setValue(votes2);

        loadingPB.setVisibility(View.GONE);
        // adding a map to our database.
        // on below line we are displaying a toast message.
        Toast.makeText(VotingActivity.this, "Votes Added..", Toast.LENGTH_SHORT).show();
        // opening a new activity after updating our votes.
        votes1 = Integer.parseInt(VoteCount.get(VoteCount.size() - 3)) ;
        votes2 = Integer.parseInt(VoteCount.get(VoteCount.size() - 2)) + 1;
        votes3 = Integer.parseInt(VoteCount.get(VoteCount.size() - 1));
        opti1 = pollRVModal.getOption1();
        opti2 = pollRVModal.getOption2();
        opti3 = pollRVModal.getOption3();

        dialogBox(votes1, votes2, votes3, opti1, opti2, opti3);

    }

    private void getVotes3() {

        votes3 = Integer.parseInt(VoteCount.get(VoteCount.size() - 1)) + 1;

        databaseReference = firebaseDatabase.getInstance().getReference("Homes").child(mHomeID).child("polls").child(pollID);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        loadingPB.setVisibility(View.VISIBLE);
        databaseReference.child("votes3").setValue(votes3);
        loadingPB.setVisibility(View.GONE);
        // adding a map to our database.
        // on below line we are displaying a toast message.
        votes1 = Integer.parseInt(VoteCount.get(VoteCount.size() - 3)) ;
        votes2 = Integer.parseInt(VoteCount.get(VoteCount.size() - 2)) ;
        votes3 = Integer.parseInt(VoteCount.get(VoteCount.size() - 1)) + 1;
        opti1 = pollRVModal.getOption1();
        opti2 = pollRVModal.getOption2();
        opti3 = pollRVModal.getOption3();

        dialogBox(votes1, votes2, votes3, opti1, opti2, opti3);

    }

    private void getData(final FirebaseCallBack firebaseCallBack) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
                ArrayList<String> Votes = new ArrayList<>();
                String homeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);

                for (DataSnapshot getVotes : snapshot.child("Homes").child(homeID).child("polls").child(pollID).getChildren()) {
                    Votes.add(getVotes.getValue().toString());

                }

                firebaseCallBack.onCallBack(Votes, homeID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                // Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dialogBox(int votes1, int votes2, int votes3, String opti1, String opti2, String opti3) {

        //Creates alert dialog on the homepage and assigns it to newDialog
        AlertDialog.Builder newDialog = new AlertDialog.Builder(VotingActivity.this);
        //Creates a layout inflater from HomePage
        LayoutInflater inflater = LayoutInflater.from(VotingActivity.this);
        //Inflates the 'input.xml' layout and assigns it to a view called newView
        View newView = inflater.inflate(R.layout.activity_results, null);

        //Creates a new dialog box
        AlertDialog dialog = newDialog.create();
        //Sets this new dialog box to display newView aka the 'input.xml' layout
        dialog.setView(newView);
        //initialising all our textviews
        Button bckBtn = newView.findViewById(R.id.bckBtn);
        TextView option1 = newView.findViewById(R.id.option1);
        TextView option2 =newView.findViewById(R.id.option2);
        TextView option3 = newView.findViewById(R.id.option3);
        TextView Count1 =newView.findViewById(R.id.voteCount1);
        TextView Count2 = newView.findViewById(R.id.voteCount2);
        TextView Count3 = newView.findViewById(R.id.voteCount3);
        //setting the text views with data
        Count1.setText(Integer.toString(votes1));
        Count2.setText(Integer.toString(votes2));
        Count3.setText(Integer.toString(votes3));
        option1.setText(opti1);
        option2.setText(opti2);
        option3.setText(opti3);

        bckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), PollActivity.class));
                finish();
            }
        });
        //Shows the input dialog box
        dialog.show();
        //Creates an onClickLister to listen for when the submitBtn is clicked

    }
}