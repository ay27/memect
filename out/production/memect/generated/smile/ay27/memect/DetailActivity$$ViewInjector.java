// Generated code from Butter Knife. Do not modify!
package smile.ay27.memect;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class DetailActivity$$ViewInjector {
  public static void inject(Finder finder, final smile.ay27.memect.DetailActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296324, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131296325, "field 'swipeRefreshLayout'");
    target.swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) view;
    view = finder.findRequiredView(source, 2131296326, "field 'listView'");
    target.listView = (android.widget.ListView) view;
  }

  public static void reset(smile.ay27.memect.DetailActivity target) {
    target.toolbar = null;
    target.swipeRefreshLayout = null;
    target.listView = null;
  }
}
