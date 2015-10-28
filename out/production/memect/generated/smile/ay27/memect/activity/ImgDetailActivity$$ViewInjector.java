// Generated code from Butter Knife. Do not modify!
package smile.ay27.memect.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class ImgDetailActivity$$ViewInjector {
  public static void inject(Finder finder, final smile.ay27.memect.activity.ImgDetailActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296336, "field 'imageView'");
    target.imageView = (com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView) view;
  }

  public static void reset(smile.ay27.memect.activity.ImgDetailActivity target) {
    target.imageView = null;
  }
}
