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
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.content.ContentValues.TAG;
import com.facebook.FacebookSdk;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FacebookAuthProvider;


public class Login extends AppCompatActivity {
    private/* const */ int RC_SIGN_IN = 1; // add consts
    private SignInButton signInButtonGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;

    private ImageView imageProfile;
    private String ProfileNameString;
    private LoginButton facebookLogin;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView textViewUser;
    private AccessTokenTracker accessTokenTracker;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageProfile = findViewById(R.id.profileImage);
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
        // If the User already connected - go to profile.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getUid().toString()).child("Full name")
                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());
/*            FirebaseDatabase.getInstance().getReference().child("Dogs")
                    .child(FirebaseAuth.getInstance().getUid().toString());*/
            startActivity(new Intent(getApplicationContext(),Profile.class));
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        textViewUser = findViewById(R.id.text_user);
        facebookLogin = findViewById(R.id.facebook_login_button);
        facebookLogin.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(@NotNull FacebookException e) {
                Log.d(TAG, "onError");
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    updateUIFB(user);
                }
                else{
                    updateUIFB(null);
                }
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mFirebaseAuth.signOut();
                }
            }
        };
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
            //Uri profilePic = account.getPhotoUrl();
            //Picasso.get().load(profilePic).into(imageProfile);
            // Create a table for the user in Fbase
            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getUid().toString()).child("Full name")
                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());
/*            FirebaseDatabase.getInstance().getReference().child("Dogs")
                    .child(FirebaseAuth.getInstance().getUid().toString());*/
        }
        else {
            //textViewUser.setText("");
            //imageProfile.setImageResource(R.drawable.ic_profile);
        }
    }

    private void handleFacebookToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener
                (this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUIFB(user);
                            Intent intent = new Intent(getApplicationContext(),Profile.class);
                            intent.putExtra("ProfileName", ProfileNameString);
                            intent.putExtra("ProfileImage",photoUrl);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(Login.this, "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                            updateUIFB(null);
                        }
                    }
                });
    }

    private void updateUIFB(FirebaseUser user) {

        if(user != null){
            textViewUser.setText(user.getDisplayName());
            if(user.getPhotoUrl()!=null){
                //photoUrl = user.getPhotoUrl().toString();
                //ProfileNameString = user.getDisplayName();
                //photoUrl= photoUrl+"?type=large";
                //Picasso.get().load(photoUrl).into(imageProfile);
                // Create a table for the user in Fbase
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid().toString()).child("Full name")
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString());
/*                FirebaseDatabase.getInstance().getReference().child("Dogs")
                        .child(FirebaseAuth.getInstance().getUid().toString());*/
            }
        }

        else {
            textViewUser.setText("");
            imageProfile.setImageResource(R.drawable.ic_profile);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!= null){
            mFirebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}