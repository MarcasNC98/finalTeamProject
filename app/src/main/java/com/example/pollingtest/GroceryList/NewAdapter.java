package com.example.pollingtest.GroceryList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pollingtest.Data.Info;
import com.example.pollingtest.R;

import java.util.ArrayList;

//Recycler view that gets the data from the Firebase and then displays it in the application in line with my info_output xml layout.
public class NewAdapter extends RecyclerView.Adapter<NewAdapter.NewViewHolder> {
    //Stores the context of the GroceryActivity
    Context context;
    //ArrayList of objects that stores grocery information
    ArrayList<Info> list;

    //Constructor needed to create a recyclerView
    public NewAdapter(Context context, ArrayList<Info> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating a variable v that takes the layout that the grocery data will be displayed in line with
        View v = LayoutInflater.from(context).inflate(R.layout.info_output,parent,false);
        return new NewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position) {
        //Calls the bind method from the class NewViewHolder
        holder.bind(list.get(position));
    }

    @Override
    //Returns the size of the ArrayList so the recyclerview knows how many items to display
    public int getItemCount() {
        return list.size();
    }

    public static class NewViewHolder extends RecyclerView.ViewHolder{
        //Variables for storing information that is being displayed from the ArrayList of objects Info
        TextView date,text, amount, price;
        //Cardview that is used to detect a press on an item in the grocery list.
        CardView newCardView;
        //An info object used for when an onClickEvent happens
        Info info;
        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            //Preparing the text fields where the information will be displayed
            newCardView = itemView.findViewById(R.id.mainCardView);
            date = itemView.findViewById(R.id.outputDate);
            text = itemView.findViewById(R.id.outputText);
            amount = itemView.findViewById(R.id.outputAmount);
            price = itemView.findViewById(R.id.outputPrice);

            //Looks for a press on the cardView
            newCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Runs a deleteGrocery method created in GroceryActivity
                    new GroceryActivity().deleteGrocery(info.getId());
                }
            });

        }
        //Used to write data to the text fields where info will be displayed in CardView
        public void bind(Info info){
            date.setText(info.getDate());
            text.setText(info.getText());
            amount.setText(Integer.toString(info.getAmount()));
            price.setText(Double.toString(info.getPrice()));
            //Assigning the info object to the class info variable
            this.info=info;
        }



    }
}
