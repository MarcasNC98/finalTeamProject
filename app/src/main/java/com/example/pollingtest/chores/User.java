package com.example.pollingtest.chores;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {//I used parcelable to pass the List of chores into the AddChoreActivity class.
    private String name;
    private List<ChoreWithID> choreList;
    private String id;
    private int totalPriority;

    //Constructors
    public User() {
        choreList = new ArrayList<>();
        name = "";
        id = "";
        totalPriority = 0;
    }

    public User(String name, ChoreWithID chore, String id) {
        this.name = name;
        this.choreList.add(chore);
        this.id = id;
    }

    protected User(Parcel in) {
        name = in.readString();
        id = in.readString();
        choreList = in.createTypedArrayList(ChoreWithID.CREATOR); //Reference: https://medium.com/@jlf426551/saving-a-nested-arraylist-object-to-a-bundle-in-android-6307abcf5429
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    //Method used for testing
    public void printChores(){
        if(choreList!=null){
            for(int i = 0; i< choreList.size(); i++){
                System.out.println(">> Chores: "+ choreList.get(i).getName());
            }
        }
        else{
            System.out.println("The Chore list is empty/null!");
        }

    }

    //I realized that when data wipes from the array of chores in the database helper they wipe in here too. Because of that, I have created this method which allows the program to add a chore.
    public void addToChores(ChoreWithID chore){
        choreList.add(chore);
    }

    //This method is used to get the chores from the priority queue.
    public void setChoreList(ArrayList<ChoreWithID> choreArrayList){
        for(ChoreWithID chore:choreArrayList){
            addToChores(chore);
        }
    }


    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChoreWithID> getChoreList() {
        return choreList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalPriority() {
        return totalPriority;
    }

    public void setTotalPriority(int totalPriority) {
        this.totalPriority = totalPriority;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeTypedList(choreList);
    }
}
