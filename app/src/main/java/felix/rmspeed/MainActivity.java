package felix.rmspeed;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;

    Button btnQuickTest, btnLess, btnMore;
    VideoView vWVideoView;

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
                    vWVideoView.seekTo(vWVideoView.getCurrentPosition() - 1000);
                }
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vWVideoView.getCurrentPosition() < vWVideoView.getDuration())
                {
                    vWVideoView.seekTo(vWVideoView.getCurrentPosition() + 1000);
                }
            }
        });
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
    }
}
