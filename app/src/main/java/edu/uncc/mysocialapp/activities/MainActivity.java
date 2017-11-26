package edu.uncc.mysocialapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.pojo.Post;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    SignInButton googleSignInButton;
    private FirebaseAuth mAuth;
    Button signInButton;
    TextView signUpLink;
    ProgressBar loginLoader;
    public static final String REQUEST_SENT = "requestSent";
    public static final String REQUEST_RECEIVED = "requestReceived";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //ImageView logout = (ImageView) findViewById(R.id.logout);
        loginLoader = (ProgressBar) findViewById(R.id.loginLoader);
        loginLoader.setIndeterminate(true);
        loginLoader.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Log.d("MAIN", "User Details -- "+auth.getCurrentUser().getDisplayName());
            Log.d("MAIN", "User Details -- "+auth.getCurrentUser().getEmail());
            Log.d("MAIN", "User Details -- "+auth.getCurrentUser().getUid());
            Intent homeIntent = new Intent(MainActivity.this, Home_Activity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            // User is logged in
        }

        final EditText username = (EditText) findViewById(R.id.loginUsername);
        final EditText password = (EditText) findViewById(R.id.loginPassword);
        signInButton = (Button) findViewById(R.id.loginButtonLogin);
        signUpLink = (TextView) findViewById(R.id.loginSignupLink);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String eid = username.getText().toString();
                String pass = password.getText().toString();

                if (eid == null || eid.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(eid).matches()) {
                    Toast.makeText(MainActivity.this, "Enter a valid Email.", Toast.LENGTH_SHORT).show();
                } else if (pass == null || pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter Password.", Toast.LENGTH_SHORT).show();
                } else {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    loginLoader.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    loginLoader.setVisibility(View.INVISIBLE);
                                    if (task.isSuccessful()) {
                                        Log.d("LOGIN", "LoggedIn Successfuly");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent homeIntent = new Intent(MainActivity.this, Home_Activity.class);
                                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(homeIntent);
                                        finish();

                                    } else {
                                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    });
                }
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInButton = (SignInButton) findViewById(R.id.loginGoogleLogin);
        googleSignInButton.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        for (int i = 0; i < googleSignInButton.getChildCount(); i++) {
            View v = googleSignInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("Sign In with Google");
                return;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GSIGNIN", "handleSignInResult : " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            firebaseAuthWithGoogle(acct);
            //Toast.makeText(this, "Logged in as " + acct.getDisplayName() + " " + acct.getEmail(), Toast.LENGTH_SHORT).show();

        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            loginLoader.setVisibility(View.INVISIBLE);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GSIGNIN", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        loginLoader.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            Log.d("GSIGNIN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("username").setValue(mAuth.getCurrentUser().getDisplayName());

                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("username").setValue(user.getDisplayName().split(" ")[0]+" "+user.getDisplayName().split(" ")[1]);
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("fname").setValue(user.getDisplayName().split(" ")[0]);
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("lname").setValue(user.getDisplayName().split(" ")[1]);
                            //FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("dob").setValue(dateOfBirth);
                            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("email").setValue(user.getEmail());


                            Toast.makeText(MainActivity.this, "Signed In as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, Home_Activity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homeIntent);
                            finish();
                        } else {
                            Log.w("GSIGNIN", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loginLoader.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
