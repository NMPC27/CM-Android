package com.example.cinem;

import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SING_IN = 100;
    SignInButton btSignIn;
    GoogleSignInClient mSignInClient;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login();
    }

    private void login() {
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("505911816682-dgah878pcb4hfimprmheaeooq5bfvafq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this,gso);

        btSignIn=findViewById(R.id.bt_sign_in);

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SING_IN);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SING_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("ola", "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, authResult -> {
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    startActivity(new Intent(MainActivity.this,Menu.class).putExtra("user",user.getEmail()));
                    finish();
                })
                .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show());
    }

}