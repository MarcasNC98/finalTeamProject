package com.example.pollingtest;

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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PollRVAdapter.PollClickInterface {

    // creating variables for fab, firebase database,
    // progress bar, list, adapter,firebase auth,
    // recycler view and relative layout.
    private FloatingActionButton addPollFAB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView pollRV;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private ArrayList<PollRVModal> pollRVModalArrayList;
    private PollRVAdapter pollRVAdapter;
    private RelativeLayout homeRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initializing all our variables.
        pollRV = findViewById(R.id.idRVPolls);
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBLoading);
        addPollFAB = findViewById(R.id.idFABAddPoll);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pollRVModalArrayList = new ArrayList<>();
        // on below line we are getting database reference.
        databaseReference = firebaseDatabase.getReference("polls");
        // on below line adding a click listener for our floating action button.
        addPollFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity for adding a Poll.
                Intent i = new Intent(MainActivity.this, AddPollActivity.class);
                startActivity(i);
            }
        });
        // on below line initializing our adapter class.
        pollRVAdapter = new PollRVAdapter(pollRVModalArrayList, this, this::onPollClick);
        // setting layout malinger to recycler view on below line.
        pollRV.setLayoutManager(new LinearLayoutManager(this));
        // setting adapter to recycler view on below line.
        pollRV.setAdapter(pollRVAdapter);
        // on below line calling a method to fetch Polls from database.
        getPolls();
    }

    private void getPolls() {
        // on below line clearing our list.
        pollRVModalArrayList.clear();
        // on below line we are calling add child event listener method to read the data.
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // on below line we are hiding our progress bar.
                loadingPB.setVisibility(View.GONE);
                // adding snapshot to our array list on below line.
                pollRVModalArrayList.add(snapshot.getValue(PollRVModal.class));
                // notifying our adapter that data has changed.
                pollRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when new child is added
                // we are notifying our adapter and loading progress bar
                // visibility as gone.
                loadingPB.setVisibility(View.GONE);
                pollRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // notifying our adapter when child is removed.
                pollRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // notifying our adapter when child is moved.
                pollRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPollClick(int position) {
        // calling a method to display a bottom sheet on below line.
        displayBottomSheet(pollRVModalArrayList.get(position));
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
                mAuth.signOut();
                // on below line we are opening our login activity.
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
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
        // our text view and image view inside bottom sheet
        // and initialing them with their ids.
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
                Intent i = new Intent(MainActivity.this, EditPollActivity.class);
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
                Intent x = new Intent(MainActivity.this, VotingActivity.class);
                // on below line we are passing our Poll modal
                x.putExtra("poll", modal);
                startActivity(x);
            }
        });
    }
}