package edu.uncc.mysocialapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uncc.mysocialapp.R;

public class EditUserProfile extends AppCompatActivity {

    private Long dobInMillis;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(EditUserProfile.this, MainActivity.class);
                //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(loginIntent);
                finish();
            }
        });
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        final EditText editFname = (EditText) findViewById(R.id.editFname);
        final EditText editLname = (EditText) findViewById(R.id.editLname);
        final EditText editEid = (EditText) findViewById(R.id.editEid);
        final EditText editDob = (EditText) findViewById(R.id.editDob);
        final EditText editPass = (EditText) findViewById(R.id.editPass);
        final EditText editRepass = (EditText) findViewById(R.id.editRepass);

        editFname.setText(getIntent().getExtras().get("userFname").toString());
        editLname.setText(getIntent().getExtras().get("userLname").toString());
        editEid.setText(getIntent().getExtras().get("userEid").toString());
        if(getIntent().getExtras().get("userDob")!= null){
            editDob.setText(getIntent().getExtras().get("userDob").toString());
        }
        Button editSave = (Button) findViewById(R.id.editSave);

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
                editDob.setText(sdf.format(myCalendar.getTime()));
                dobInMillis = myCalendar.getTimeInMillis();
            }

        };

        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd =  new DatePickerDialog(EditUserProfile.this, date, myCalendar
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

        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = editFname.getText().toString();
                String lname = editLname.getText().toString();
                String eid = editEid.getText().toString();
                String dob = editDob.getText().toString();
                String pass = editPass.getText().toString();
                String repass = editRepass.getText().toString();

               // final String dateOfBirth = dob.getText().toString();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -13);

                Long dobValidation = cal.getTimeInMillis();

                if(fname == null || fname.isEmpty()){
                    Toast.makeText(EditUserProfile.this, "Enter a First Name", Toast.LENGTH_SHORT).show();
                }else if(lname == null || lname.isEmpty()){
                    Toast.makeText(EditUserProfile.this, "Enter a Last Name", Toast.LENGTH_SHORT).show();
                }else if(eid == null || eid.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(eid).matches()){
                    Toast.makeText(EditUserProfile.this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
                }else if(dob == null || dob.isEmpty() ){
                    Toast.makeText(EditUserProfile.this, "Enter a Date of Birth", Toast.LENGTH_SHORT).show();
                }else if(dobInMillis != null && dobInMillis > dobValidation ){
                    Toast.makeText(EditUserProfile.this, "You must be 13+ to signup.", Toast.LENGTH_SHORT).show();
                }else if(pass == null || pass.isEmpty()){
                    Toast.makeText(EditUserProfile.this, "Enter a Password", Toast.LENGTH_SHORT).show();
                }else if(pass.length() < 8){
                    Toast.makeText(EditUserProfile.this, "Password - Minimum 8 Chars.", Toast.LENGTH_SHORT).show();
                }else if(repass == null || repass.isEmpty()){
                    Toast.makeText(EditUserProfile.this, "Re-enter the Password", Toast.LENGTH_SHORT).show();
                }else if(!repass.equals(pass)){
                    Toast.makeText(EditUserProfile.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                }else{

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fname+" "+lname).build();
                    mAuth.getCurrentUser().updateProfile(profileUpdates);
                    //mUser.updateEmail(eid);
                    mUser.updatePassword(pass);
                    usersReference.child(mAuth.getCurrentUser().getUid()).child("username").setValue(fname+" "+lname);
                    usersReference.child(mAuth.getCurrentUser().getUid()).child("userprofile").child("fname").setValue(fname);
                    usersReference.child(mAuth.getCurrentUser().getUid()).child("userprofile").child("lname").setValue(lname);
                    usersReference.child(mAuth.getCurrentUser().getUid()).child("userprofile").child("dob").setValue(dob);
                    usersReference.child(mAuth.getCurrentUser().getUid()).child("userprofile").child("email").setValue(eid);
                    Toast.makeText(EditUserProfile.this, "Changes saved.", Toast.LENGTH_SHORT).show();
                    finish();

                }

            }
        });
    }
}
