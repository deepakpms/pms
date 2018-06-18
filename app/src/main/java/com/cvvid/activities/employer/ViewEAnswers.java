package com.cvvid.activities.employer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cvvid.R;
import com.cvvid.activities.candidate.ShowVideo;

public class ViewEAnswers extends AppCompatActivity {

    ProgressDialog pd;
    VideoView view;
    TextView questions;
    ProgressDialog progressDialog;
    ImageView close_activity;
    private String questiontxt;
    private String jobid;
    Button btn_edit;
    String URL = "";
    private Context mContext;
    private static final String TAG = "ShowVideo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_eanswers);

        view = (VideoView)findViewById(R.id.videoView);
        questions = (TextView)findViewById(R.id.questions);
        close_activity = (ImageView) findViewById(R.id.close_activity);

        pd = new ProgressDialog(ViewEAnswers.this);
        pd.setTitle("Video Streamming");
        pd.setMessage("Buffering...");
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        pd.show();

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        try{

            Intent intent = getIntent();
            String videourl = intent.getStringExtra("videourl");
            questiontxt = intent.getStringExtra("questions");
            jobid = intent.getStringExtra("jobid");

            System.out.println("VIDDD="+videourl);

            questions.setText(questiontxt);

            MediaController controller = new MediaController(ViewEAnswers.this);
            controller.setAnchorView(view);
            Uri vid = Uri.parse(videourl);
            view.setMediaController(controller);
            view.setVideoURI(vid);

        }catch(Exception e){
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            pd.dismiss();
        }
        view.requestFocus();
        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
// TODO Auto-generated method stub
                pd.dismiss();
                view.start();
            }
        });

        view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Error playing video",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error playing video");
                return true;
            }
        });

        close_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
