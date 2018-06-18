package com.cvvid.activities.employer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cvvid.R;
import com.cvvid.adaptors.employer.EmployerMembershipAdaptor;
import com.cvvid.models.employer.EmployerMembershipModel;

import java.util.ArrayList;

public class EmployerMembership extends AppCompatActivity {

    RecyclerView recyclerview;
    ArrayList<EmployerMembershipModel> membershipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_membership);

        recyclerview = findViewById(R.id.re);

        String[] id = {"1", "2"};
        String[] names = {"BASIC FREE", "PREMIUM"};
        String[] prices = {"$0", "$252.00"};

        membershipList = new ArrayList<>();


        for (int i = 0; i < names.length; i++) {

            membershipList.add(new EmployerMembershipModel(id[i], names[i], prices[i]));

        }

        EmployerMembershipAdaptor adapter = new EmployerMembershipAdaptor(this, membershipList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView.LayoutManager mLayoutManager = linearLayoutManager;

        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);
    }
}
