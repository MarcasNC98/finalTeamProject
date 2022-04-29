package com.example.pollingtest.Login;

import android.app.ProgressDialog;
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

import com.example.pollingtest.GroceryList.GroceryActivity;
import com.example.pollingtest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;


    Button googleLoginBtn;
    //EditText class called emailAddress
    private EditText emailAddress;
    //EditText class called password
    private EditText password;
    //Textview class called email_signUp
    private TextView email_signUp;
    //Button class called buttonLogin
    private Button buttonLogin;
    //FirebaseAuth class called newAuth
    private FirebaseAuth newAuth;
    //ProgressDialog class called newDialog
    private ProgressDialog newDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Tried to add google login system, could not get it working properly with the application
//        googleLoginBtn = findViewById(R.id.googleLoginBtn);
//
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);

        //Returns an instance of FirebaseAuth and ties it to newAuth
        newAuth = FirebaseAuth.getInstance();
        //Assigns the input field for an email address with the ID 'login_email' from 'activity_main.xml' to emailAddress
        emailAddress = findViewById(R.id.login_email);
        //Assigns the input field for a password with the ID 'login_password' from 'activity_main.xml' to password
        password = findViewById(R.id.login_password);
        //Assigns the text field for redirecting a user to the registration screen with the ID 'register' from 'activity_main.xml' to email_signUp
        email_signUp = findViewById(R.id.register);
        //Assigns the button for logging in with the ID 'loginBtn' from 'activity_main.xml' to buttonLogin
        buttonLogin = findViewById(R.id.loginBtn);

        //Creates and sets a new instance of ProgressDialog and assigns it to newDialog, this will display a progress messaging letting the user know the program is working on logging in
        newDialog = new ProgressDialog(this);

        //Sets an onClickListener that will look for a click on the buttonLogin button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked, a string called newEmail and newPassword will be created that will get the text in the emailAddress and password fields,
                //convert them to strings and will use trim to remove any spaces at the beginning or end of the inputted data
                String newEmail = emailAddress.getText().toString().trim();
                String newPassword = password.getText().toString().trim();
                //If the newEmail field is empty, an error will be shown in the emailAddress field stating that the field cannot be blank
                if (TextUtils.isEmpty(newEmail)) {
                    emailAddress.setError("Cannot be blank");
                    return;
                }
                //The same as above, an error will be shown if the password field is blank
                if (TextUtils.isEmpty(newPassword)) {
                    password.setError("Cannot be blank");
                    return;
                }
                //A dialog message that will appear on screen when a user clicks the 'login' button that informs the user that the page is loading
                newDialog.setMessage("Loading...");
                newDialog.show();
                //Firebase Authenticator newAuth that will sign in the user using the newEmail and newPassword fields and will listen for this being completed
                newAuth.signInWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //If the task is successful, a new activity is started that will get the HomePage class and redirect the user to the apps home page
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), GroceryActivity.class));
                            finish();
                            //A toast dialog message will pop up on the screen informing the user that they have been logged in successfully
                            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                            //The newDialog loading message is dismissed
                            newDialog.dismiss();
                        } else {
                            //if the task is unsuccessful for any reason, a toast message will pop up on screen informing the user that their login attempt failed
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            //Again, the newDialog loading message is dismissed
                            newDialog.dismiss();
                        }
                    }
                });
            }
        });

        //Sets an onClickListener that will look for a click on the email_signUp text field
        email_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked, an activity will start that will get the RegistrationPage java class and the user will be redirected to the registration screen where they can create an account
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });


        //Remnants of google sign in code that I couldn't get working.
//        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                signIn();
//            }
//        });
//
//
//
//    }

//    void signIn(){
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent,1000);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1000){
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//
//            try {
//                task.getResult(ApiException.class);
//                navigateHomePage();
//            } catch (ApiException e) {
//                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//    void navigateHomePage(){
//        finish();
//        startActivity(new Intent(getApplicationContext(),GroceryActivity.class));
//    }


    }
}