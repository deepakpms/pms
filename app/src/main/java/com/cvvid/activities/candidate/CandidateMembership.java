package com.cvvid.activities.candidate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.adaptors.candidate.CandidateMembershipAdaptor;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.models.candidate.CandidateMembershipModel;
import com.cvvid.R;
import com.cvvid.models.candidate.ChatMessageCandidate;
import com.cvvid.models.candidate.MembershipItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class CandidateMembership extends AppCompatActivity {

    RecyclerView recyclerview;
    ArrayList<CandidateMembershipModel> membershipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_membership);

        Button btn_premium_action = (Button) findViewById(R.id.btn_premium_action);
        Button btn_acion_basic = (Button) findViewById(R.id.btn_acion_basic);

        btn_premium_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CandidateMembership.this ,CandidateBasicRegister.class);
                intent.putExtra("ispremium", false);
                startActivity(intent);
            }
        });

        btn_acion_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CandidateMembership.this ,CandidateBasicRegister.class);
                intent.putExtra("ispremium", true);
                startActivity(intent);
            }
        });
    }

}
