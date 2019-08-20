package felix.rmspeed;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;

    Button btnQuickTest, btnLess, btnMore, btnStart, btnEnd;
    VideoView vWVideoView;
    TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImportReferences();

        btnQuickTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                try{
                    startActivityForResult(intent, READ_REQUEST_CODE);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vWVideoView.getCurrentPosition() != 0)
                {
                    vWVideoView.pause();
                    vWVideoView.seekTo(vWVideoView.getCurrentPosition() - 100);
                }
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vWVideoView.getCurrentPosition() < vWVideoView.getDuration())
                {
                    vWVideoView.pause();
                    vWVideoView.seekTo(vWVideoView.getCurrentPosition() + 100);
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setText(String.valueOf(vWVideoView.getCurrentPosition()));
                CalculateTime();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEnd.setText(String.valueOf(vWVideoView.getCurrentPosition()));
                CalculateTime();
            }
        });
    }

    public void CalculateTime()
    {
        try
        {
            int val1 = btnStart.getText() != null ? Integer.valueOf((String) btnStart.getText()) : 0;
            int val2 = btnEnd.getText() != null ? Integer.valueOf((String) btnEnd.getText()) : 0;
            int resuklt = val2 - val1;

            txtTime.setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(resuklt)));
        }
        catch (Exception exception)
        {
            System.out.println("Exception:!!!: " + exception.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                vWVideoView.setVideoURI(uri);

                vWVideoView.start();

                MediaController mediaController = new MediaController(vWVideoView.getContext());
                vWVideoView.setMediaController(mediaController);
                mediaController.setAnchorView(vWVideoView);
            }
        }
    }

    private void ImportReferences() {
        btnQuickTest = findViewById(R.id.btnQuickTest);
        btnLess = findViewById(R.id.btn_less);
        btnMore = findViewById(R.id.btn_more);

        vWVideoView = findViewById(R.id.vView);

        btnStart = findViewById(R.id.btnStart);
        txtTime = findViewById(R.id.txtTime);
        btnEnd = findViewById(R.id.btnEnd);
    }
}
