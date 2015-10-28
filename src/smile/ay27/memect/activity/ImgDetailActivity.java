package smile.ay27.memect.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import smile.ay27.memect.R;
import smile.ay27.memect.network.ImageCacheManager;

/**
 * Created by ay27 on 15/10/28.
 */
public class ImgDetailActivity extends Activity {

    @InjectView(R.id.img_detail_img)
    SubsamplingScaleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_detail);
        ButterKnife.inject(this);

        String picHref = getIntent().getStringExtra("picHref");
        ImageCacheManager.loadImage(picHref, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    imageView.setImage(ImageSource.bitmap(response.getBitmap()));
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ImgDetailActivity.this, "error in loading image", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        imageView.setMinimumDpi(50);
        imageView.setMaxScale(2F);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
