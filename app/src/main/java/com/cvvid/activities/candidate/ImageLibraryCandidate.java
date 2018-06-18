package com.cvvid.activities.candidate;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cvvid.R;
import com.cvvid.adaptors.candidate.ImageAdaptor;
import com.cvvid.adaptors.candidate.ProfilesViewsAdapter;
import com.cvvid.adaptors.candidate.SearchJobAdaptor;
import com.cvvid.apicall.AppSingleton;
import com.cvvid.common.SessionManager;
import com.cvvid.fragments.candidate.CBusinessCard;
import com.cvvid.models.candidate.ImageListModel;
import com.cvvid.models.candidate.ProfilesViewsModel;
import com.cvvid.models.candidate.SearchItemJobList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cvvid.apicall.LOCATION_URL.CONFIG_URL;

public class ImageLibraryCandidate extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmptyTextView;
    private ImageAdaptor adapter;
    private List<ImageListModel> imageList;
    private View view;
    private String username ="";
    // Session Manager Class
    SessionManager session;

    private String USER_ID;
    private String USER_TYPE;
    private String USER_NAME;
    private String PROFILE_ID;
    private static final String TAG = "RegisterActivity";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_library_candidate);

        getSupportActionBar().setTitle("CVVid Image Library");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvEmptyTextView = (TextView)findViewById(R.id.empty_view);

        imageList = new ArrayList<>();

        adapter = new ImageAdaptor(this, imageList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ImageLibraryCandidate.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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

        PROFILE_ID = user.get(SessionManager.PROFILE_ID);

        loadimages(USER_ID,USER_TYPE);

    }

    public void loadimages(final String USER_ID, final String USER_TYPE) {


        // Tag used to cancel the request
        String cancel_req_tag = "search";
        progressDialog.setMessage("loading..");

        showDialog();

        String url = "http://pdev.work/cvvidapi/api/medialibrary/id/"+USER_ID+"/type/"+USER_TYPE;

        final StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                Log.d(TAG, "SearchList Response: " + response.toString());

                try {

                    JSONObject jarray = new JSONObject(response);
                    // do your work with response object

                    JSONArray jUser = jarray.getJSONArray("data");
                    ImageListModel item;

                    for (int i = 0; i < jUser.length(); i++)
                    {
                        JSONObject c = jUser.getJSONObject(i);

                        item = new ImageListModel();

                        JSONArray parr = c.getJSONArray("photos");

                        JSONArray sarr = c.getJSONArray("sample");

                        System.out.println("---------start-------");

                        for (int pi = 0; pi < parr.length(); pi++)
                        {
                            JSONObject cp = parr.getJSONObject(pi);
                            item.setId(cp.getString("id"));
                            item.setUrl(cp.getString("url"));

                            imageList.add(new ImageListModel(cp.getString("id"), cp.getString("url")));

                        }

                        for (int si = 0;si < sarr.length(); si++)
                        {
                            JSONObject sp = sarr.getJSONObject(si);

                            item.setId(sp.getString("id"));
                            item.setUrl(sp.getString("url"));

                            imageList.add(new ImageListModel(sp.getString("id"), sp.getString("url")));
                        }

                    }

                    if(imageList.isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                        tvEmptyTextView.setVisibility(View.VISIBLE);
                    }else{
                        recyclerView.setVisibility(View.VISIBLE);
                        tvEmptyTextView.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();

                    System.out.println("count"+imageList.size());

                    hideDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
//
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
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
