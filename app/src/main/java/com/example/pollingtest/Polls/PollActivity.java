package com.example.pollingtest.Polls;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollingtest.GroceryList.GroceryActivity;
import com.example.pollingtest.Login.LoginActivity;
import com.example.pollingtest.R;
import com.example.pollingtest.chores.MainChoresActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PollActivity extends AppCompatActivity implements PollRVAdapter.PollClickInterface {

    // creating variables for fab, firebase database,
    // progress bar, list, adapter,firebase auth,
    // recycler view and relative layout.
    private FloatingActionButton addPollFAB;
   private FirebaseDatabase firebaseDatabase;
   private DatabaseReference databaseReference;
    private RecyclerView pollRV;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private String homeID,userID;
    private ArrayList<PollRVModal> pollRVModalArrayList;
    private PollRVAdapter pollRVAdapter;
    private RelativeLayout homeRL;
    private Button grocery,chores,polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);
        // initializing all our variables.
        pollRV = findViewById(R.id.idRVPolls);
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBLoading);
        addPollFAB = findViewById(R.id.idFABAddPoll);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        grocery = (Button) findViewById(R.id.polls_grocery_btn);
        chores = (Button) findViewById(R.id.polls_chores_btn);
        polls = (Button) findViewById(R.id.polls_polls_btn);
        FirebaseUser newUser=mAuth.getCurrentUser();

        //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.
        String uId=newUser.getUid();
        userID=uId;

        pollRVModalArrayList = new ArrayList<>();

        firebaseDatabase=FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        // on below line we are getting database reference.
        databaseReference=firebaseDatabase.getReference();

        // on below line adding a click listener for our floating action button.
        addPollFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity for adding a Poll.
                Intent i = new Intent(PollActivity.this, AddPollActivity.class);
                startActivity(i);
            }
        });
        // on below line initializing our adapter class.
        pollRVAdapter = new PollRVAdapter(pollRVModalArrayList, this, this::onPollClick);
        // setting layout manager to recycler view on below line.
        pollRV.setLayoutManager(new LinearLayoutManager(this));
        // setting adapter to recycler view on below line.
        pollRV.setAdapter(pollRVAdapter);
        getData();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pollRVModalArrayList.clear();
                String userHomeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);
                    //for every poll in the current users house get the value and add it to the pollRVMOdal
                    for(DataSnapshot secondSnapshot : snapshot.child("Homes").child(userHomeID).child("polls").getChildren()){

                        PollRVModal PollRVModal = secondSnapshot.getValue(PollRVModal.class);
                        pollRVModalArrayList.add(PollRVModal);
                        loadingPB.setVisibility(View.GONE);

                    }

                pollRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollActivity.this, GroceryActivity.class));
                finish();
            }
        });

        chores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PollActivity.this, MainChoresActivity.class));
                finish();
            }
        });

    }
    private void getData(){

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID.

                homeID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                // Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onPollClick(int position) {
        // calling a method to display a bottom sheet on below line.
        displayBottomSheet(pollRVModalArrayList.get(position));
    }
    //this is a log out button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // adding a click listener for option selected on below line.
        int id = item.getItemId();
        switch (id) {
            case R.id.idLogOut:
                // displaying a toast message on user logged out inside on click.
                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                // on below line we are signing out our user.
                mAuth.signOut();
                // on below line we are opening our login activity.
                Intent i = new Intent(PollActivity.this, LoginActivity.class);
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
// this is a pop up bottom display that will show the tenants a preview of the poll description
    private void displayBottomSheet(PollRVModal modal) {
        // on below line we are creating our bottom sheet dialog.
        final BottomSheetDialog bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        // on below line we are inflating our layout file for our bottom sheet.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, homeRL);
        // setting content view for bottom sheet on below line.
        bottomSheetTeachersDialog.setContentView(layout);
        // on below line we are setting a cancelable
        bottomSheetTeachersDialog.setCancelable(false);
        bottomSheetTeachersDialog.setCanceledOnTouchOutside(true);
        // calling a method to display our bottom sheet.
        bottomSheetTeachersDialog.show();
        // on below line we are creating variables for
        // our text view and image view
        // and initialing them
        TextView pollNameTV = layout.findViewById(R.id.idTVPollName);
        TextView pollDescTV = layout.findViewById(R.id.idTVPollDesc);

        ImageView pollIV = layout.findViewById(R.id.idIVPoll);
        // on below line we are setting data to different views on below line.
        pollNameTV.setText(modal.getPollName());
        pollDescTV.setText(modal.getPollDescription());
        Picasso.get().load(modal.getPollImg()).into(pollIV);
        Button voteBtn = layout.findViewById(R.id.idBtnVoting);
        Button editBtn = layout.findViewById(R.id.idBtnEditPoll);

        // adding on click listener for our edit button.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are opening our EditPollActivity on below line.
                Intent i = new Intent(PollActivity.this, EditPollActivity.class);
                // on below line we are passing our Poll modal
                i.putExtra("poll", modal);
                startActivity(i);
            }

        });
        // adding on click listener for our vote button.
        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are opening our votingActivity on below line.
                Intent x = new Intent(PollActivity.this, VotingActivity.class);
                // on below line we are passing our Poll modal
                x.putExtra("poll", modal);
                startActivity(x);
            }
        });


    }
}