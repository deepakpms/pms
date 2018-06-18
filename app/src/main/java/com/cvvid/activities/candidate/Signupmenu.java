package com.cvvid.activities.candidate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;

import com.cvvid.activities.institution.InstituteRegister;
import com.cvvid.R;
import com.cvvid.activities.employer.EmployerMembership;

public class Signupmenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupmenu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        CardView candidate = (CardView) findViewById(R.id.candidateregister);
        CardView employer = (CardView) findViewById(R.id.employerregister);
        CardView institute = (CardView) findViewById(R.id.instituteregister);


        candidate.setOnClickListener(
                new CardView.OnClickListener(){
                    @Override
                    public void onClick(android.view.View view) {
                        // navigate signup activity
                        Intent intent = new Intent(Signupmenu.this, CandidateMembership.class);
                        startActivity(intent);
                    }

                }
        );

        employer.setOnClickListener(
                new CardView.OnClickListener(){
                    @Override
                    public void onClick(android.view.View view) {
                        // navigate signup activity
                        Intent intent = new Intent(Signupmenu.this, EmployerMembership.class);
                        startActivity(intent);
                    }

                }
        );

        institute.setOnClickListener(
                new CardView.OnClickListener(){
                    @Override
                    public void onClick(android.view.View view) {
                        // navigate signup activity
                        Intent intent = new Intent(Signupmenu.this, InstituteRegister.class);
                        startActivity(intent);
                    }

                }
        );


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
