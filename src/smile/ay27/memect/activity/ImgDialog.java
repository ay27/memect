package smile.ay27.memect.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
 * Created by ay27 on 15/10/29.
 */
public class ImgDialog extends Dialog {

    private final String picHref;
    SubsamplingScaleImageView imageView;

    public ImgDialog(Context context, String picHref) {
        super(context, R.style.full_screen_dialog);
        this.picHref = picHref;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.img_detail);

        imageView = (SubsamplingScaleImageView) findViewById(R.id.img_detail_img);

        ImageCacheManager.loadImage(picHref, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    imageView.setImage(ImageSource.bitmap(response.getBitmap()));
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "error in loading image", Toast.LENGTH_LONG).show();
                cancel();
            }
        });

        imageView.setMaxScale(3F);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }


}
