package smile.ay27.memect;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.*;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String[] title = MemectApplication.appContext.getResources().getStringArray(R.array.catalog_title);
    public static final int DrawerItemCount = title.length;
    private static long back_pressed;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.left_drawer)
    LinearLayout drawerLeft;
    @InjectView(R.id.book_manager_toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @InjectView(R.id.drawer_activity_left_drawer)
    ListView drawerListView;

    private int currentFragment = 0;

    private ActionBarDrawerToggle drawerToggle;

    private DrawerListAdapter adapter;
    private AdapterView.OnItemClickListener drawerListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            drawerLayout.closeDrawer(drawerLeft);

            Fragment fragment = null;

            currentFragment = position;


            fragment = new MainFragment(position);

//            fragment = new ContentFragment(position);

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.drawer_activity_content_frame, fragment).commit();
                drawerListView.setItemChecked(position, true);
                setTitle(title[position]);
                drawerLayout.closeDrawer(drawerLeft);

                // the the color
                adapter.resetColor();
                view.setSelected(true);
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.textView.setTextColor(Color.WHITE);

                return;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        toolbar.setTitleTextColor(Color.WHITE);//设置标题颜色
        setSupportActionBar(toolbar);

        initViews();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerLayout.setDrawerListener(drawerToggle);

        adapter = new DrawerListAdapter(this);
        drawerListView.setAdapter(adapter);
        drawerListView.setOnItemClickListener(drawerListItemClickListener);
        drawerListView.performItemClick(adapter.getItem(0), 0, 0);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.clear();
//        if (currentFragment == 0) {
//            toolbar.inflateMenu(R.menu.main);
//            return true;
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    static class ViewHolder {
        @InjectView(R.id.drawer_list_item_text)
        TextView textView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    private class DrawerListAdapter extends BaseAdapter {

        private Context _context;
        private View[] items;

        private DrawerListAdapter(Context _context) {
            this._context = _context;

            items = new View[DrawerItemCount];
            for (int i = 0; i < DrawerItemCount; i++) {
                items[i] = makeItem(title[i]);
            }
        }

        private View makeItem(String title) {
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View root = inflater.inflate(R.layout.drawer_list_item, null);

            ViewHolder holder = new ViewHolder(root);
            holder.textView.setText(title);

            root.setTag(holder);

            return root;
        }

        public void resetColor() {
            for (int i = 0; i < items.length; i++) {
                ViewHolder holder = (ViewHolder) items[i].getTag();
                holder.textView.setTextColor(getResources().getColor(R.color.text_black_default_selector));
            }
        }

        @Override
        public int getCount() {
            return DrawerItemCount;
        }

        @Override
        public View getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                return items[i];

            return view;
        }
    }
}
