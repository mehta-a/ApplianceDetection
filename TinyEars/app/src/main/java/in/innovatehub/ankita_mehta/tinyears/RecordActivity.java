package in.innovatehub.ankita_mehta.tinyears;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecordActivity extends Activity {
    private VisualizerView visualizerView;
    private static final String LOG_TAG = "AudioRecordTest";

    private static final int REQUESTCODE_RECORDING = 109201;
    private Button mRecorderApp = null;

    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private ImageButton mRecordImageButton = null;
    private ImageButton mPlayImageButton = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    private ProgressBar mProgressBar = null;
    private TextView mTextViewLoad = null;
    private Button mShowStatsButton = null;

    private LinearLayout mLoadingSection = null;

    private Handler handler = new Handler();
    final Runnable updater = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1);
            if(mRecorder!=null) {
                int maxAmplitude = mRecorder.getMaxAmplitude();

                if (maxAmplitude != 0) {
                    visualizerView.addAmplitude(maxAmplitude);
                }
            }
            else{

            }
        }
    };

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.i("Completion Listener", "Song Complete");
                    stopPlaying();
                    mRecordImageButton.setEnabled(true);
                }
            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            mPlayImageButton.setImageResource(R.drawable.playicon);
            //  mStartPlaying = true;
        } else {
            mPlayImageButton.setImageResource(R.drawable.pauseicon);
            //   mStartPlaying = false;
        }
    }

    private void startRecording() {
        AudioRecordTest(String.valueOf(System.currentTimeMillis()));
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        try {
            mRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(LOG_TAG, "start() failed");
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
            mRecorder = null;
            mRecordImageButton.setImageResource(R.drawable.micicon);
            // mStartRecording = true;
        } else {
            mRecordImageButton.setImageResource(R.drawable.stopicon);
            // mStartRecording = false;
        }
    }

    public void AudioRecordTest(String text) {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tinyEars/";
        boolean exists = (new File(mFileName)).exists();
        if (!exists) {
            new File(mFileName).mkdirs();
        }
        mFileName += "audiorecordtest" + text + ".3gp";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mRecordImageButton = (ImageButton) findViewById(R.id.imageButton2);
        mPlayImageButton = (ImageButton) findViewById(R.id.imageButton3);

        mProgressBar = (ProgressBar) findViewById(R.id.downloadProgress);
        mTextViewLoad = (TextView) findViewById(R.id.loadingMessage);
        mShowStatsButton = (Button) findViewById(R.id.showMeStats);
        mLoadingSection = (LinearLayout) findViewById(R.id.loadStatsLinearLayout);
        mRecorderApp = (Button) findViewById(R.id.recorderApp);
        visualizerView = (VisualizerView) findViewById(R.id.visualizer);

        AudioRecordTest("00000");
        mRecordImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                onRecord(mStartRecording);
                if (mStartRecording) {
                    mRecordImageButton.setImageResource(R.drawable.stopicon);
                    mPlayImageButton.setEnabled(false);
                    //setText("Stop recording");
                } else {
                    mRecordImageButton.setImageResource(R.drawable.micicon);
                    mPlayImageButton.setEnabled(true);
                    //setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });

        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayImageButton.setImageResource(R.drawable.pauseicon);
                    mRecordImageButton.setEnabled(false);
                    //setText("Stop playing");
                } else {
                    mPlayImageButton.setImageResource(R.drawable.playicon);
                    mRecordImageButton.setEnabled(true);
                    //setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        //Calling recorder ...
        mRecorderApp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (isAvailable(getApplicationContext(), intent)) {
                    startActivityForResult(intent, REQUESTCODE_RECORDING);
                }
            }
        });
    }
    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        if (requestCode == REQUESTCODE_RECORDING) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = intent.getData();
                // make use of this MediaStore uri
                // e.g. store it somewhere

            }
            else {
                // react meaningful to problems
            }
        }
        else {
            super.onActivityResult(requestCode,
                    resultCode, intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updater);
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        handler.post(updater);
    }

    public boolean loadStats(View view){
        boolean successful = false;
        /*
        1.  Create the objects related to message sending to server.
        2. Open connection(if any)
        3. Read Data using input stream
        4. Open file output, save if any
        5. close connections
         */
        Thread myThread = new Thread(new LoadStatsThread());
        myThread.start();

        try {
            myThread.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (myThread.isAlive()){
            myThread.interrupt();
        }
        Toast.makeText(getApplicationContext(),"Load finished",Toast.LENGTH_SHORT);
        mLoadingSection.setVisibility(View.GONE);
        return false;
    }

    private class LoadStatsThread implements Runnable{

        @Override
        public void run() {
            //Do thread task here
            RecordActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingSection.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
