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
    private static final int MIN_VIDEO_THUMB_POSITION = 0;
    private static int VIDEO_FPS = 100;
    private static final String FILE_TYPE_TO_FIND = "video/*";

    private Button btnLoadVideo, btnLess, btnMore, btnStart, btnEnd;
    private VideoView videoView;
    private TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImportReferences();

        btnLoadVideo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(FILE_TYPE_TO_FIND);

                try
                {
                    startActivityForResult(intent, READ_REQUEST_CODE);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this, "There are no file explorer clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdjustThumbPositionInVideo(FPS.LESS);
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdjustThumbPositionInVideo(FPS.MORE);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStart.setText(String.valueOf(videoView.getCurrentPosition()));
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEnd.setText(String.valueOf(videoView.getCurrentPosition()));
                CalculateTime();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null)
            {
                SetVideoOnVideoView(resultData);

                SetVideoBar();
            }
        }
    }

    private void SetVideoBar() {
        MediaController mediaController = new MediaController(videoView.getContext());
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }

    private void SetVideoOnVideoView(Intent resultData) {
        Uri uri = resultData.getData();
        videoView.setVideoURI(uri);
        videoView.start();
    }

    private void AdjustThumbPositionInVideo(FPS frames)
    {
        if(videoView.getCurrentPosition() > MIN_VIDEO_THUMB_POSITION && videoView.getCurrentPosition() < videoView.getDuration())
        {
            videoView.pause();

            int position = videoView.getCurrentPosition();

            switch(frames)
            {
                case LESS:
                    position -= VIDEO_FPS;
                    break;

                case MORE:
                    position += VIDEO_FPS;
                    break;
            }

            try
            {
                videoView.seekTo(position);
            }
            catch (Exception exception)
            {
                System.out.println("Exception:!!!: " + exception.getMessage());
            }
        }
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

    private void ImportReferences() {
        btnLoadVideo = findViewById(R.id.btnLoadVideo);

        btnLess = findViewById(R.id.btn_less);
        btnMore = findViewById(R.id.btn_more);

        videoView = findViewById(R.id.vView);

        btnStart = findViewById(R.id.btnStart);
        txtTime = findViewById(R.id.txtTime);
        btnEnd = findViewById(R.id.btnEnd);
    }
}
