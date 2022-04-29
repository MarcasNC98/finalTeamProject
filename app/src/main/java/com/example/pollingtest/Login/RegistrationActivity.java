package com.example.pollingtest.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pollingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    //EditText class called emailAddress
    private EditText emailAddress;
    //EditText class called password
    private EditText password;
    //TextView class called email_signIn
    private TextView email_signIn;
    //Button class called buttonRegister
    private Button buttonRegister;
    //FirebaseAuth class called newAuth
    private FirebaseAuth newAuth;
    //ProgressDialog class called newDialog
    private ProgressDialog newDialog;
    //Firebase database and reference classes for accessing database
    private FirebaseDatabase newDatabase;
    private DatabaseReference newReference;
    //EditText class for entering username
    private EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        //Returns an instance of FirebaseAuth and ties it to newAuth
        newAuth=FirebaseAuth.getInstance();
        //Assigns the input field for a username with the ID 'register_name' from 'activity_registration.xml' to userName
        userName=findViewById(R.id.register_name);
        //Assigns the input field for an email address with the ID 'register_email' from 'activity_registration.xml' to emailAddress
        emailAddress=findViewById(R.id.register_email);
        //Assigns the input field for creating a password with the ID 'register_password' from 'activity_registration.xml' to password
        password=findViewById(R.id.register_password);
        //Assigns the text field for redirecting a user to the sign in screen with the ID 'register_SignIn' from 'activity_registration.xml' to email_signIn
        email_signIn=findViewById(R.id.register_signIn);
        //Assigns the button for creating an account with the ID 'registerBtn' from 'activity_registration.xml' to buttonRegister
        buttonRegister=findViewById(R.id.registerBtn);

        //Returns an instance of FirebaseAuth and ties it to newAuth
        newAuth=FirebaseAuth.getInstance();

        //Reference to the firebase database
        newDatabase=FirebaseDatabase.getInstance("https://polling-3351e-default-rtdb.europe-west1.firebasedatabase.app/");
        newReference=newDatabase.getReference();

        //Creates and sets a new instance of ProgressDialog and assigns it to newDialog, this will display a progress messaging letting the user know the program is working on registering an account
        newDialog=new ProgressDialog(this);

        //Sets an onClickListener that will look for a click on the email_signIn text field
        email_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Will end the current activity allowing the user to go back
                finish();
            }
        });

        //Sets an onClickLister that will look for a click on the buttonRegister or 'create account' button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked, a string called newEmail and newPassword will be created that will get the text in the emailAddress and password fields,
                //convert them to strings and will use trim to remove any spaces at the beginning or end of the inputted data
                String name=userName.getText().toString().trim();
                String newEmail=emailAddress.getText().toString().trim();
                String newPassword=password.getText().toString().trim();
                //If the newEmail field is empty, an error will be shown in the emailAddress field stating that the field cannot be blank
                if (TextUtils.isEmpty(name)){
                    userName.setError("Cannot be blank");
                    return;
                }
                //If the newEmail field is empty, an error will be shown in the emailAddress field stating that the field cannot be blank
                if (TextUtils.isEmpty(newEmail)){
                    emailAddress.setError("Cannot be blank");
                    return;
                }
                //The same as above, an error will be shown if the password field is blank
                if (TextUtils.isEmpty(newPassword)){
                    password.setError("Cannot be blank");
                    return;
                }
                //A dialog message that will appear on screen when a user clicks the 'create account' button that informs the user that the page is loading
                newDialog.setMessage("Loading...");
                newDialog.show();
                //Firebase Authenticator newAuth that will create a user with an email and password using the newEmail and newPassword fields and will listen for this being completed
                newAuth.createUserWithEmailAndPassword(newEmail,newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //If the task is successful, a new activity is started that will get the HomePage class and redirect the user to the apps home page
                        if (task.isSuccessful()){
                            //The newDialog loading message is dismissed
                            newDialog.dismiss();
                            //Creates a string called uId and ties it to newUser.getUid that will retrieve the users generated ID
                            FirebaseUser newUser=newAuth.getCurrentUser();
                            String uId=newUser.getUid();
                            newReference.child("NewUsers").child(uId).child("Name").setValue(name);
                            //A toast dialog message will pop up on the screen informing the user that their account has been created successfully
                            Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_SHORT).show();
                            //Sends over the userID being created to ChooseHouseActivity, means I don't have to create a new instance of uId in that activity
                            Intent intent = new Intent(getApplicationContext(), ChooseHouseActivity.class);
                            intent.putExtra("uId", uId);
                            startActivity(intent);
                            //Will end the current activity allowing the user to go back
                            finish();
                        }else {
                            //if the task is unsuccessful for any reason, a toast message will pop up on screen informing the user that their account creation failed
                            Toast.makeText(getApplicationContext(),"Sign Up Failed",Toast.LENGTH_SHORT).show();

                            //Again, the newDialog loading message is dismissed
                            newDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}