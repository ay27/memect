// Generated code from Butter Knife. Do not modify!
package smile.ay27.memect;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainFragment$$ViewInjector {
  public static void inject(Finder finder, final smile.ay27.memect.MainFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296330, "field 'swipeRefreshLayout'");
    target.swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view;
    view = finder.findRequiredView(source, 2131296331, "field 'listView'");
    target.listView = (android.widget.ListView) view;
  }

  public static void reset(smile.ay27.memect.MainFragment target) {
    target.swipeRefreshLayout = null;
    target.listView = null;
  }
}
