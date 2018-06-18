package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cvvid.R;
import com.cvvid.adaptors.candidate.SCategoryAdaptor;
import com.cvvid.common.SessionManager;
import com.cvvid.models.candidate.SkillsCategoryItem;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.apicall.LOCATION_URL;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class AddCExperience extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmptyTextView;
    private SCategoryAdaptor adapter;
    private List<SkillsCategoryItem> scViewList;
    private View view;
    private String username ="";
    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cexperience);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvEmptyTextView = (TextView)findViewById(R.id.empty_view);

        scViewList = new ArrayList<>();
        adapter = new SCategoryAdaptor(AddCExperience.this, scViewList);
       // adapter = new SCategoryAdaptor(this, scViewList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new AddCExperience.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //        session
        session = new SessionManager(this);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();



        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        loadSessionData();

        loadData(USER_ID);


        initCollapsingToolbar();
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(username);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public void loadSessionData()
    {



        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // user_id
        USER_ID = user.get(SessionManager.USER_ID);
        // type
        USER_TYPE = user.get(SessionManager.USER_TYPE);
        // username
        USER_NAME = user.get(SessionManager.USER_NAME);

        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("Searching..");

        showDialog();

        String URL_FOR_DETAIL = LOCATION_URL.CANDIDATE_DETAIL + USER_ID;

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

                    String slug = c.getString("slug");

                    TextView candidatename = (TextView) findViewById(R.id.usernametxt);
                    candidatename.setText(c.getString("forenames")+" "+c.getString("surname"));
                    TextView slugname = (TextView) findViewById(R.id.slugurl);
                    slugname.setText("http://cvvid.com/"+c.getString("slug"));

                    String cover_id = c.getString("cover_id");
                    String cover = "";
                    if(cover_id.equals("")){
                        cover = c.getString("coverdefault");
                    }else{
                        cover = c.getString("cover");
                    }

                    username = c.getString("forenames")+" "+c.getString("surname");

                    ImageView coverphoto = (ImageView)findViewById(R.id.coverphoto);

                    Picasso.with(AddCExperience.this).load(cover)
                            .fit()
                            .centerCrop()
                            .error(R.drawable.defaultprofile)
                            .into(coverphoto);

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.print(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(AddCExperience.this,error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(this).addToRequestQueue(strReq, cancel_req_tag);

    }

    public void loadData(String profile_id) {

        //Creating a string request
        String URL_FOR_DETAIL = CONFIG_URL+"/getskillcategory";;

        final StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL_FOR_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject j = null;
                try {

                    JSONObject jarray = new JSONObject(response);

                    JSONArray jUser = jarray.getJSONArray("data");
                    SkillsCategoryItem item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new SkillsCategoryItem();

                        item.setId(c.getString("id"));
                        item.setName(c.getString("name"));

                        scViewList.add(new SkillsCategoryItem(c.getString("id"), c.getString("name")));

                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(AddCExperience.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
