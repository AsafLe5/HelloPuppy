package com.kingslayer.hellopuppy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private ImageView imageProfile;
    private LoginButton facebookLogin;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageProfile = findViewById(R.id.profileImage);
        facebookLogin = findViewById(R.id.facebook_login_button);

        callbackManager = CallbackManager.Factory.create();

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String imageUrl = "https://graph.facebook.com/" + loginResult.getAccessToken().getUserId()
                        + "/picture?return_ssl_resources=1";
                Picasso.get().load(imageUrl).into(imageProfile);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(@NotNull FacebookException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}