package com.example.pollingtest.chores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollingtest.R;

import java.util.List;

public class UsersRVConfig {
    private Context mContext;

    private UserAdapter mUserAdapter;
    public void setUsersConfig(RecyclerView recyclerView, Context context, List<User> users, String houseID){
        mContext = context;
        mUserAdapter = new UserAdapter(users, houseID);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mUserAdapter);
    }


    //The View holder for the recyclerview
    class UserItemView extends RecyclerView.ViewHolder{
        private TextView mUserName;
        private RecyclerView choresRV;
        private TextView emptyView;
        private String mHouseID;

        public UserItemView(ViewGroup parent, String houseID) {
            super(LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false));
            mUserName = (TextView) itemView.findViewById(R.id.userNameTV);
            choresRV = (RecyclerView) itemView.findViewById(R.id.choresRV);
            emptyView = (TextView) itemView.findViewById(R.id.emptyView);
            mHouseID = houseID;
        }

        //displays info into the row layout(chore_item.xml)
        public void bind(User user){
            mUserName.setText(user.getName());

            if(user.getChoreList().isEmpty()){//Ref: https://www.youtube.com/watch?v=E7FEVV74jz0
                mUserName.setVisibility(View.GONE);
                choresRV.setVisibility(View.GONE);
            }
            else{
                //Calls upon the chores rv config to print data
                ChoresRVConfig mChoresRVConfig = new ChoresRVConfig();
                mChoresRVConfig.setChoresConfig(choresRV,mContext, user.getChoreList(), mHouseID, user.getId());
            }

        }
    }

    //The adapter class for the recyclerview
    class UserAdapter extends RecyclerView.Adapter<UserItemView> {
        private List<User> mUserList;
        private String houseID;

        public UserAdapter(List<User> mUserList, String houseID) {
            this.mUserList = mUserList;
            this.houseID = houseID;
        }

        @NonNull
        @Override
        public UserItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserItemView(parent, houseID);
        }

        @Override
        public void onBindViewHolder(@NonNull UserItemView holder, int position) {
            holder.bind(mUserList.get(position));
        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }
    }
}
