// Generated code from Butter Knife. Do not modify!
package smile.ay27.memect;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final smile.ay27.memect.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296321, "field 'drawerLayout'");
    target.drawerLayout = (android.support.v4.widget.DrawerLayout) view;
    view = finder.findRequiredView(source, 2131296327, "field 'drawerLeft'");
    target.drawerLeft = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2131296320, "field 'toolbar'");
    target.toolbar = (android.support.v7.widget.Toolbar) view;
    view = finder.findRequiredView(source, 2131296328, "field 'drawerListView'");
    target.drawerListView = (android.widget.ListView) view;
  }

  public static void reset(smile.ay27.memect.MainActivity target) {
    target.drawerLayout = null;
    target.drawerLeft = null;
    target.toolbar = null;
    target.drawerListView = null;
  }
}
