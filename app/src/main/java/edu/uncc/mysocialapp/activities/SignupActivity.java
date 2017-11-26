package edu.uncc.mysocialapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.pojo.Post;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Long dobInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        Button signup = (Button) findViewById(R.id.signupButtonSignup);
        final EditText firstName = (EditText) findViewById(R.id.signupFirstname);
        final EditText lastName =(EditText)findViewById(R.id.signupLastname);
        final EditText email = (EditText) findViewById(R.id.signupEmail);
        final EditText dob = (EditText) findViewById(R.id.signupDOB);
        final EditText password =(EditText)findViewById(R.id.signupPassword);
        final EditText repassword = (EditText) findViewById(R.id.signupRePassword);

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dob.setText(sdf.format(myCalendar.getTime()));
                dobInMillis = myCalendar.getTimeInMillis();
            }

        };

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd =  new DatePickerDialog(SignupActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar cal = Calendar.getInstance();
               /* cal.set(1850,1,1);
                Long minTime = cal.getTimeInMillis();
                dpd.getDatePicker().setMinDate(minTime);*/
                cal = Calendar.getInstance(Locale.US);
                Long maxTime = cal.getTimeInMillis();
                dpd.getDatePicker().setMaxDate(maxTime);
                dpd.show();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = firstName.getText().toString();
                final String lname = lastName.getText().toString();
                final String eid = email.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                final String dateOfBirth = dob.getText().toString();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -13);

                Long dobValidation = cal.getTimeInMillis();

                if(fname == null || fname.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Enter a First Name", Toast.LENGTH_SHORT).show();
                }else if(lname == null || lname.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Enter a Last Name", Toast.LENGTH_SHORT).show();
                }else if(eid == null || eid.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(eid).matches()){
                    Toast.makeText(SignupActivity.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
                }else if(dateOfBirth == null || dateOfBirth.isEmpty() ){
                    Toast.makeText(SignupActivity.this, "Enter a Date of Birth", Toast.LENGTH_SHORT).show();
                }else if(dobInMillis > dobValidation ){
                    Toast.makeText(SignupActivity.this, "You must be 13+ to signup.", Toast.LENGTH_SHORT).show();
                }else if(pass == null || pass.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                }else if(pass.length() < 8){
                    Toast.makeText(SignupActivity.this, "Password - Minimum 8 Chars.", Toast.LENGTH_SHORT).show();
                }else if(repass == null || repass.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Re-enter the Password", Toast.LENGTH_SHORT).show();
                }else if(!repass.equals(pass)){
                    Toast.makeText(SignupActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fname+" "+lname).build();
                                        mAuth.getCurrentUser().updateProfile(profileUpdates);
                                        Log.d("SIGNUP", fname +" "+lname+" "+eid+" "+dateOfBirth+" "+password+" "+mAuth.getCurrentUser().getUid()+" "+mAuth.getCurrentUser().getDisplayName());
                                        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("username").setValue(fname+" "+lname);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("fname").setValue(fname);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("lname").setValue(lname);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("dob").setValue(dateOfBirth);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("userprofile").child("email").setValue(eid);
                                        //FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("posts").setValue(new ArrayList<Post>());
                                        Toast.makeText(SignupActivity.this, "Signed up successfuly", Toast.LENGTH_SHORT).show();
                                        Log.d("SIGNUP", "Successful");
                                        mAuth.signOut();
                                        Intent loginIntent = new Intent(SignupActivity.this, MainActivity.class);
                                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(loginIntent);
                                        finish();
                                    }else{
                                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }
}
