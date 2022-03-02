package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.content.ContentValues.TAG;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class Login extends AppCompatActivity {
    private SignInButton signInButtonGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private int RC_SIGN_IN = 1;
    private ImageView imageProfileFromGoogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageProfileFromGoogle = findViewById(R.id.profileImage);
        signInButtonGoogle = findViewById(R.id.button_login_google);
        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Bundle b = new Bundle();
        startActivityForResult(signInIntent, RC_SIGN_IN, b);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completeTask){
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount acc = completeTask.getResult(ApiException.class);

            if (acc != null) {
                firebaseGoogleAuth(acc);
            }
            else{
                System.out.println("acc is null!!");
                Log.w(TAG, "acc is null!!");
            }
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "in the catch part", e);
            System.out.println("in the catch part");

//            firebaseGoogleAuth(null);
        }
    }
    private void firebaseGoogleAuth(GoogleSignInAccount acc){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.w(TAG, "successful");
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI(user);

                    Intent intent = new Intent(getApplicationContext(),Profile.class);
                    if (user != null) {
                        intent.putExtra("ProfileName", user.getDisplayName());
                        intent.putExtra("ProfileImage", Objects.requireNonNull(user.getPhotoUrl()).toString());
                    }
                    else{
                        intent.putExtra("ProfileName", "");
                        intent.putExtra("ProfileImage", "");
                    }
                    startActivity(intent);
                }
                else {
                    Log.w(TAG, "bad");
                    updateUI(null);
                }
            }
        });
    }
    private void updateUI(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            Uri profilePic = account.getPhotoUrl();
            String profileName = account.getDisplayName();
            Picasso.get().load(profilePic).into(imageProfileFromGoogle);
        }
        else {
            //textViewUser.setText("");
            imageProfileFromGoogle.setImageResource(R.drawable.ic_profile);
        }
    }
}