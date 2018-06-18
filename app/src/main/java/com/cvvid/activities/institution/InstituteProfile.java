package com.cvvid.activities.institution;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cvvid.R;
import com.cvvid.activities.employer.EmployerProfile;
import com.cvvid.activities.employer.PostedJobs;
import com.cvvid.common.BottomNavigationViewHelper;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.employer.CandidateSearchFragment;
import com.cvvid.fragments.employer.EmployerMessageFragment;
import com.cvvid.fragments.employer.EmployerProfileFragment;
import com.cvvid.fragments.employer.EmployerSettingFragment;
import com.cvvid.fragments.institute.InstituteProfileFragment;
import com.cvvid.fragments.institute.InstituteSettingFragment;

import java.util.HashMap;

public class InstituteProfile extends AppCompatActivity {

    private FrameLayout mMainFrame;

    private FragmentManager fragmentManager;

    private static final String TAG_FRAGMENT_ONE = "fragment_one";
    private static final String TAG_FRAGMENT_TWO = "fragment_two";
    private static final String TAG_FRAGMENT_THREE = "fragment_three";
    private static final String TAG_FRAGMENT_FOUR = "fragment_four";

    private Fragment currentFragment;
    private Context context;
    InstituteProfile activity;
    SessionManager session;
    String USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_profile);

        context = this;

        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);//Dont forgot this line

        // instantiate the fragment manager
        fragmentManager = getSupportFragmentManager();

//        currentFragment = CandidateProfileFragment.newInstance();

        Bundle bundle = getIntent().getExtras();

        if (bundle  != null) {

            if(bundle.getString("issetting").equals("1")) {

                navigation.setSelectedItemId(R.id.action_settings);

                Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_FOUR);
                if (fragment == null) {
                    fragment = EmployerSettingFragment.newInstance();
                }
                replaceFragment(fragment, TAG_FRAGMENT_FOUR);

            }
            else {
                Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
                if (fragment == null) {
                    fragment = InstituteProfileFragment.newInstance();
                }
                replaceFragment(fragment, TAG_FRAGMENT_ONE);
            }
        }
        else {
            Fragment fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
            if (fragment == null) {
                fragment = InstituteProfileFragment.newInstance();
            }
            replaceFragment(fragment, TAG_FRAGMENT_ONE);
        }

        //        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);


        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_profile:
                                removeintent();
                                // I'm aware that this code can be optimized by a method which accepts a class definition and returns the proper fragment
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_ONE);
                                if (fragment == null) {
                                    fragment = InstituteProfileFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_ONE);

                                return true;
                            case R.id.action_message:
                                removeintent();
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_TWO);
                                if (fragment == null) {
                                    fragment = EmployerMessageFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_TWO);

                                return true;
                            case R.id.action_users:
                                removeintent();
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_THREE);
                                if (fragment == null) {
                                    fragment = CandidateSearchFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_THREE);

                                return true;
                            case R.id.action_settings:
                                removeintent();
                                fragment = fragmentManager.findFragmentByTag(TAG_FRAGMENT_FOUR);
                                if (fragment == null) {
                                    fragment = InstituteSettingFragment.newInstance();
                                }
                                replaceFragment(fragment, TAG_FRAGMENT_FOUR);

                                return true;
                        }
                        return true;
                    }
                });

    }

    private void removeintent()
    {
        getIntent().removeExtra("position");
        getIntent().removeExtra("issetting");
    }

    private void replaceFragment(@NonNull Fragment fragment, @NonNull String tag) {

        if (!fragment.equals(currentFragment)) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_frame, fragment, tag)
                    .commit();
            currentFragment = fragment;
        }else {
            System.out.println("not dis");
        }
    }

}
