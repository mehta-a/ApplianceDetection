package in.innovatehub.ankita_mehta.tinyears;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "tinyEars.MainActivity";
    ImageButton mGoRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Inside on create.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoRecordButton = (ImageButton) findViewById(R.id.imageButton);
        mGoRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0){
                Log.d(TAG, "Inside on create, Navigating to Record Activity!");
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);
            }
        });
    }
}
