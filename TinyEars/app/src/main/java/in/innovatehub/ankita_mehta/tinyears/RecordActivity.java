package in.innovatehub.ankita_mehta.tinyears;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends Activity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    //private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    //private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = null;

    private ImageButton mRecordImageButton = null;
    private ImageButton mPlayImageButton = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

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
        } catch (Exception e) {
            Log.e(LOG_TAG, "start() failed");
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
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
}
