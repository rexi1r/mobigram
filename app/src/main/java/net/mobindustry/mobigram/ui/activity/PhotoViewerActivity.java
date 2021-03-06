package net.mobindustry.mobigram.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.mobindustry.mobigram.R;
import net.mobindustry.mobigram.utils.Const;
import net.mobindustry.mobigram.utils.ImageLoaderHelper;
import net.mobindustry.mobigram.utils.Utils;

public class PhotoViewerActivity extends Activity {
    int gif = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_viewer_activity);

        ImageView imageView = (ImageView) findViewById(R.id.photo_image_view);
        ImageView back = (ImageView) findViewById(R.id.photo_view_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String path = getIntent().getStringExtra("file_path");
        gif = getIntent().getIntExtra("gif", 0);
        if (gif == 1) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(500, 500);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            Glide.with(this).load(path).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
        } else {
            if (path == null) {
                int id = getIntent().getIntExtra("file_id", 0);
                Utils.photoFileLoader(id, imageView, this);
            } else {
                ImageLoaderHelper.displayImage(Const.IMAGE_LOADER_PATH_PREFIX + path, imageView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
