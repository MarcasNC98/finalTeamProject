package com.example.pollingtest.chores;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.example.pollingtest.chores.pq.MyPriorityQueue;
import com.example.pollingtest.chores.pq.PQInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<User> users = new ArrayList<>();
    private FirebaseAuth newAuth;
    private String userID; //TODO: will be replace by getting the current user that has been logged in


    public interface DataStatus{
        void DataIsLoaded(List<User> users, String houseID, String check);
        void DataIsInserted();
        void DataUpdated();
        void DataDeleted();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FirebaseDatabaseHelper(){
        mDatabase = FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        mReference = mDatabase.getReference();
        newAuth=FirebaseAuth.getInstance();
        FirebaseUser newUser=newAuth.getCurrentUser();
        userID=newUser.getUid();
    }


    public void readData(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {//House/chores
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();//Clears the previous users from the list to replace them with the new ones

                String houseID = snapshot.child("NewUsers").child(userID).child("home").getValue(String.class);//Stores the Id of the house the user is part of. It doesn't have to be an object it can also be a variable and it wil behave the same.
                DataSnapshot tenantsSnapshot = snapshot.child("Homes").child(houseID).child("tenants");//I made a variable so that I would not have to provide all the children each time.

                String check = snapshot.child("Homes").child(houseID).child("check").getValue(String.class);//variable that stores the date of which the last update on the priorities has been made.

                for(DataSnapshot UserNode : tenantsSnapshot.getChildren()){//Loops through all the children (users) of the chores parent in the database.
                    User user = new User();
                    PQInterface mPQ = new MyPriorityQueue();//Creates instance of the priority queue. I need a new priority queue for each user to only stores one users chores.

                    //Getting the name from the users list
                    String userId = UserNode.getKey();//gets the user ID
                    String userName = snapshot.child("NewUsers").child(userId).child("Name").getValue(String.class);//grabs the name of the user e.g. Fabian; Mark; James.

                    user.setId(userId);
                    user.setName(userName);//Sets the user name
                    users.add(user); //Adds to the user list

                    for(DataSnapshot keyNode : tenantsSnapshot.child(UserNode.getKey()).child("chores").getChildren()){//loops through all the child nodes of the users to grab all the chores of the user.
                        if(keyNode.child("priority").exists() && keyNode.child("date").exists() && keyNode.child("date").exists()){//checks to see if the chore the necessary children. Used to prevent the app from crashing.
                            Chore chore = keyNode.getValue(Chore.class);//creates a new chore object for each user.
                            String cName = chore.getName();
                            int cPriority = chore.getPriority();
                            String cDate=chore.getDate();
                            ChoreWithID choreWithID = new ChoreWithID(cName,cPriority,cDate,keyNode.getKey());


                            int priority = keyNode.child("priority").getValue(Integer.class);//gets the priority of the chore.
                            mPQ.enqueue(priority, choreWithID);//Adds the chore and the priority to the priority queue to be arranged.
                        }
                    }
                    user.setChoreList((ArrayList<ChoreWithID>) mPQ.getChores());//sets the list of chores with the chores that have been arranged based on their priority.
                }
                dataStatus.DataIsLoaded(users, houseID, check);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addChore(String houseID,String userID, Chore chore, final DataStatus dataStatus){
        String choreID = mReference.child("Homes").child(houseID).child("tenants").child(userID).child("chores").push().getKey();
        mReference.child("Homes").child(houseID).child("tenants").child(userID).child("chores").child(choreID).setValue(chore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataIsInserted();
            }
        });

    }

    public void updateChore(String houseID, String userID, String choreID, Chore chore, final DataStatus dataStatus){
        mReference.child("Homes").child(houseID).child("tenants").child(userID).child("chores").child(choreID).setValue(chore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataUpdated();
            }
        });
    }


    public void deleteChore(String houseID, String userID, String choreID, final DataStatus dataStatus){//TODO: Make it so you can only delete your own chores
        mReference.child("Homes").child(houseID).child("tenants").child(userID).child("chores").child(choreID).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dataStatus.DataDeleted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkPriority(List<User> users, String houseID, String check){
        //Check the chores date and update the priorities.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate today = LocalDate.now();//Current date
        String mCheck = check;
        LocalDate lastCheck = LocalDate.parse(check, formatter);

        if(!lastCheck.isEqual(today)){//If the app has not updated the chores today
            mReference.child("Homes").child(houseID).child("check").setValue(today.toString());//Update the check variable on the database to let the app know that it has been updated today and to not continue with this if statement.
            System.out.println(">>>>>> Today: "+today+", ast check: "+ lastCheck);

            for(User user:users){//loop through all the users
                for(ChoreWithID chore: user.getChoreList()){//Get the list of chores from each user.
                    if(chore.getPriority()<11){//Safety net. Will stop the loop from setting the priority any higher than 11.
                        String choreDate = chore.getDate();//Gets the date of which the chore was created.
                        LocalDate parsedChoreDate = LocalDate.parse(choreDate, formatter);//parse the String choreDate into a LocalDate variable to be compared later.

                        int daysBehind = 0;//This variable is used in the while loop to check how many days ago has a chore been made.
                        while(parsedChoreDate.isBefore(today)){//loop for each day the chore was made until it reaches today's date.
                            parsedChoreDate = parsedChoreDate.plusDays(1);//Add one day to the parsedChoreDate
                            daysBehind++;
                        }
                        //Priorities:  Undone=11; ASAP=10; Today=9; Tomorrow=8; Two days from now=7; 3 Days from now=6; More than 3 days=5
                        int newPriority = chore.getPriority()+daysBehind;//Set newPriority variable that will add the previous priority a chore had with the number of days it was behind. E.g. If a chore was created yesterday with priority 9(Today), now the priority will be 10(ASAP)

                        mReference.child("Homes").child(houseID).child("tenants").child(user.getId()).child("chores").child(chore.getId()).child("priority").setValue(newPriority);//Assigns the new priority
                    }
                }
            }
        }
    }



}
