package com.example.pollingtest.chores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollingtest.R;

import java.util.List;

public class ChoresRVConfig {
    private Context mContext;

    private ChoresAdapter mChoresAdapter;
    public void setChoresConfig(RecyclerView recyclerView, Context context, List<ChoreWithID> chores, String houseID, String userID){
        mContext = context;
        mChoresAdapter = new ChoresAdapter(chores, houseID, userID);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mChoresAdapter);
    }


    //The View holder for the recyclerview
    class ChoreItemView extends RecyclerView.ViewHolder{
        private CheckBox mCheckbox;
        private ChoreWithID chore;//holds the chore object#

        public ChoreItemView(ViewGroup parent, String houseID, String userID) {
            super(LayoutInflater.from(mContext).inflate(R.layout.chore_item, parent, false));
            mCheckbox = (CheckBox) itemView.findViewById(R.id.itemCheckBox);

            mCheckbox.setOnLongClickListener(new View.OnLongClickListener() {//Implements on long click listener to open am edit menu
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(mContext, UpdateChoreActivity.class);
                    intent.putExtra("name", chore.getName()); //these putExtra methods are sending over the following values into the updateChoreActivity to be displayed.
                    intent.putExtra("priority", Integer.toString(chore.getPriority()));
                    intent.putExtra("date", chore.getDate());
                    intent.putExtra("id", chore.getId());
                    intent.putExtra("houseID", houseID);
                    intent.putExtra("userID", userID);
                    mContext.startActivity(intent);
                    return false;
                }
            });

            mCheckbox.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    if(mCheckbox.isChecked()){
                        new FirebaseDatabaseHelper().deleteChore(houseID, userID, chore.getId(), new FirebaseDatabaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<User> users, String houseID) {

                            }

                            @Override
                            public void DataIsInserted() {

                            }

                            @Override
                            public void DataUpdated() {

                            }

                            @Override
                            public void DataDeleted() {
//                                Toast.makeText(mContext, "Chore successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        //displays info into the row layout(chore_item.xml)
        public void bind(ChoreWithID chore){
            mCheckbox.setText(chore.getName());
            if(chore.getPriority()>10){
                mCheckbox.setBackgroundColor(Color.parseColor("#ff3636"));
                mCheckbox.setTextColor(Color.parseColor("#000000"));
            }
            else if(chore.getPriority()==10){
                mCheckbox.setBackgroundColor(Color.parseColor("#83FC73"));
                mCheckbox.setTextColor(Color.parseColor("#1f1f1f"));
            }
            else if(chore.getPriority()==9){
                mCheckbox.setBackgroundColor(Color.parseColor("#83FC73"));
            }
            this.chore=chore;
        }

    }


    //The adapter class for the recyclerview
    class ChoresAdapter extends RecyclerView.Adapter<ChoreItemView>{
        private List<ChoreWithID> mChoresList;
        private String houseID;
        private String userID;

        public ChoresAdapter(List<ChoreWithID> mChoresList, String houseID, String userID) {
            this.mChoresList = mChoresList;
            this.houseID = houseID;
            this.userID = userID;
        }

        @NonNull
        @Override
        public ChoreItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChoreItemView(parent, houseID, userID);
        }

        @Override
        public void onBindViewHolder(@NonNull ChoreItemView holder, int position) {
            holder.bind(mChoresList.get(position));//This will get the Chore object from the array and use the bind method to display it on the row layout(chore_item.xml)
        }

        @Override
        public int getItemCount() {
            return mChoresList.size();
        }
    }

}
