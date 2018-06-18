package com.cvvid.fragments.employer;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.adaptors.candidate.SettingViewPagerAdapter;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.cvvid.common.DownloadImageTask;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.CAddressFragment;
import com.cvvid.fragments.candidate.CBusinessCard;
import com.cvvid.fragments.candidate.CCustomUrl;
import com.cvvid.fragments.candidate.CDetailsFragment;
import com.cvvid.fragments.candidate.CSecurityFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployerSettingFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SettingViewPagerAdapter madapter;
    private ImageView logout;
    private Context c;
    private String fposition = null;

    private String USER_ID;
    private String USER_NAME;
    private String USER_TYPE;

    // Session Manager Class
    SessionManager session;

    ProgressDialog progressDialog;

    public EmployerSettingFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance()
    {
        EmployerSettingFragment myFragment = new EmployerSettingFragment();
        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidate_setting, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        logout = (ImageView) view.findViewById(R.id.logout);


        tabLayout.setupWithViewPager(viewPager);

        madapter = new SettingViewPagerAdapter(getActivity().getSupportFragmentManager());
        madapter.AddFragmentPage(new EDetailsFragment(), "Details");
        madapter.AddFragmentPage(new EAddressFragment(), "Address");
        madapter.AddFragmentPage(new ESecurityFragment(), "Security");
        madapter.AddFragmentPage(new ECustomUrl(), "Custom URL");

        viewPager.setAdapter(madapter);

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getContext(),R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        Bundle bundle = getActivity().getIntent().getExtras();

        if (bundle  != null) {
            fposition = bundle.getString("position");
            viewPager.setCurrentItem(Integer.parseInt(fposition));
        }else{
            viewPager.setCurrentItem(Integer.parseInt("0"));
        }
//        fposition = getArguments().getString("position");
//        if(fposition != null && fposition.equals(""))
//           viewPager.setCurrentItem(Integer.parseInt(fposition));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               /* switch (position)
                case 0:*/
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

//        session
        session = new SessionManager(getContext());

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        getLoadData(USER_ID, view);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//logout
                session.logoutUser();
            }
        });


        return view;

    }

    private void getLoadData(final String USER_ID, final View view)
    {
        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String URL_FOR_DETAIL = LOCATION_URL.EMPLOYER_DETAIL + USER_ID;

        final StringRequest strReq = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "MyProfile Response: " + response.toString());

                hideDialog();
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");

                    JSONObject c = jUser.getJSONObject(0);

                    String cover_id = c.getString("cover_id");
                    String cover = "";
                    if(cover_id.equals("")){
                        cover = c.getString("coverdefault");
                    }else{
                        cover = c.getString("cover");
                    }

                    String photo_id = c.getString("photo_id");
                    String photo = "";
                    if(photo_id.equals("")){
                        photo = c.getString("photodefault");
                    }else{
                        photo = c.getString("photo");
                    }


                    String stripe_active = c.getString("stripe_active");
                    String upgrade = c.getString("upgrade");
                    String acctype="";
                    if(upgrade.equals("1")){
                        acctype = "Premium Account";
                    }else{
                        acctype = "Basic Account";
                    }

                    TextView empname = (TextView) view.findViewById(R.id.usertitle);
                    empname.setText(c.getString("name"));
                    TextView slugname = (TextView) view.findViewById(R.id.slugartist);
                    slugname.setText(acctype);

                    CircleImageView imageView = (CircleImageView) view.findViewById(R.id.profile_image);

                    new DownloadImageTask((ImageView)view.findViewById(R.id.profile_image))
                            .execute(photo);


//                    Picasso.with(getContext()).load(photo)
//                            .fit()
//                            .centerCrop()
//                            .placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile)
//                            .into(imageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }



}
