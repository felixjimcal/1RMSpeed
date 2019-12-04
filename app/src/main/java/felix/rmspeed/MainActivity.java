package felix.rmspeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    private static final int MIN_FRAME = 0;
    private static final String FILE_TYPE = "video/*";

    private int TOTAL_FRAMES = 0, START_FRAME = 0, END_FRAME = 0, VIDEO_DURATION = 0;

    private Button btnLoadVideo, btnLess, btnMore, btnStart, btnEnd;
    private ImageView imageView;
    private SeekBar seekBar;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImportReferences();

        btnLoadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(FILE_TYPE);

                try {
                    startActivityForResult(intent, READ_REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
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
                START_FRAME = seekBar.getProgress();
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                END_FRAME = seekBar.getProgress();
                CalculateTime();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int actualFrame = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtTime.setText(String.valueOf(progress));
                actualFrame = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SetFrame(actualFrame);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                SettingUpMedia(resultData);

                SettingUpSeekBar();

                SetFrame(MIN_FRAME);
            }
        }
    }

    private void SettingUpSeekBar() {
        TOTAL_FRAMES = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));
        seekBar.setMax(TOTAL_FRAMES);
    }

    private void SettingUpMedia(Intent resultData) {
        Uri uri = resultData.getData();
        mediaMetadataRetriever.setDataSource(getApplicationContext(), uri);

        VIDEO_DURATION = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("ShowToast")
    private void SetFrame(int frame) {
        try {
            seekBar.setProgress(frame);

            Bitmap bitmap = mediaMetadataRetriever.getFrameAtIndex(frame);
            imageView.setImageBitmap(bitmap);
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void AdjustThumbPositionInVideo(FPS frames) {
        int actualFrame = seekBar.getProgress();
        switch (frames) {
            case LESS:
                if (actualFrame != 0) {
                    actualFrame--;
                }
                break;

            case MORE:
                if (actualFrame < TOTAL_FRAMES) {
                    actualFrame++;
                }
                break;
        }

        SetFrame(actualFrame);
    }

    public void CalculateTime()
    {
        if (START_FRAME > END_FRAME || START_FRAME == END_FRAME) {
            txtTime.setText("Nope");
        } else {
            int frames = END_FRAME - START_FRAME;
            float result = ( VIDEO_DURATION * frames) / TOTAL_FRAMES;

            txtTime.setText(String.valueOf(result));
        }
    }

    private void ImportReferences() {
        btnLoadVideo = findViewById(R.id.btnLoadVideo);
        imageView = findViewById(R.id.imageView);
        mediaMetadataRetriever = new MediaMetadataRetriever();

        btnLess = findViewById(R.id.btn_less);
        btnMore = findViewById(R.id.btn_more);

        seekBar = findViewById(R.id.seekBar2);

        btnStart = findViewById(R.id.btnStart);
        txtTime = findViewById(R.id.txtTime);
        btnEnd = findViewById(R.id.btnEnd);
    }
}
