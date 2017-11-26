/*
Assignment      : Homework 04
File Name       : SplashActivity.java
Full Names      : Naishad Sure, Narahari Battala
Group           : 27
 */
package edu.uncc.mysocialapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
