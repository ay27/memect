package smile.ay27.memect;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.toolbox.ImageLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import smile.ay27.memect.network.ImageCacheManager;
import smile.ay27.memect.widget.CardsAnimationAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by ay27 on 15/10/23.
 */
public class DetailActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @InjectView(R.id.detail_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.detail_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.detail_list)
    ListView listView;

    private DetailAdapter adapter;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            adapter.refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.inject(this);

        String title = getIntent().getStringExtra("Title");
        String link = getIntent().getStringExtra("Link");

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        adapter = new DetailAdapter(this, link);
        CardsAnimationAdapter cardsAnimationAdapter = new CardsAnimationAdapter(adapter, listView);
        listView.setAdapter(cardsAnimationAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
                adapter.refresh();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    static class Item {
        String src;
        String srcHref;
        String[] keywords;
        String content;
        String picHref;

        public Item(String src, String srcHref, String[] keywords, String content, String picHref) {
            this.src = src;
            this.srcHref = srcHref;
            this.keywords = keywords;
            this.content = content;
            this.picHref = picHref;
        }

        public Item(Element thread) {
            Element screen_name = thread.getElementsByClass("link_screen_name").get(0);
            src = screen_name.text();
            srcHref = screen_name.attr("href");
            Elements kws = thread.getElementsByClass("keyword");
            keywords = new String[kws.size()];
            for (int i = 0; i < kws.size(); i++) {
                keywords[i] = kws.get(i).text();
            }

            content = thread.getElementsByClass("text").get(0).text();

            Elements pics = thread.getElementsByClass("original_pic");
            if (pics.size() > 0) {
                picHref = pics.select("img").get(0).attr("src");
                if (!picHref.startsWith("http://")) {
                    picHref = "http:" + picHref;
                }
            }
        }
    }

    class DetailAdapter extends BaseAdapter {

        String link;
        Context context;
        ArrayList<Item> headlines;
        ArrayList<Item> dynamicList;

        public DetailAdapter(Context context, String link) {
            this.context = context;
            this.link = link;

            headlines = new ArrayList<>();
            dynamicList = new ArrayList<>();
        }

        public void refresh() {
            headlines = new ArrayList<>();
            dynamicList = new ArrayList<>();

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(link).timeout(10000).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Element container = doc.getElementById("container");

                    for (Element thread : container.getElementsByClass("thread")) {
                        if (thread.className().contains("headline")) {
                            headlines.add(new Item(thread));
                        } else {
                            dynamicList.add(new Item(thread));
                        }
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    notifyDataSetInvalidated();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }.execute();
        }

        @Override
        public int getCount() {
            return headlines.size() + dynamicList.size();
        }

        @Override
        public Item getItem(int position) {
            if (position < headlines.size()) {
                return headlines.get(position);
            } else {
                return dynamicList.get(position - headlines.size());
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.every_day_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (holder.container != null) {
                holder.container.cancelRequest();
            }

            Item item = getItem(position);

            holder.srcTxv.setText(item.src);
            holder.contentTxv.setText(item.content);
            holder.setKeywords(item.keywords);

            if (item.picHref == null) {
                holder.imgView.setVisibility(View.GONE);
                return convertView;
            }

            holder.imgView.setVisibility(View.VISIBLE);
            holder.container = ImageCacheManager.loadImage(item.picHref, ImageCacheManager.getImageListener(
                    holder.imgView, null, null
            ));

            return convertView;
        }

        class ViewHolder {
            private final int[] bgs = new int[]{R.drawable.round1, R.drawable.round2, R.drawable.round3, R.drawable.round4, R.drawable.round5};
            @InjectView(R.id.every_day_item_src)
            TextView srcTxv;
            @InjectView(R.id.every_day_item_keyword_panel)
            LinearLayout keywordPanel;
            @InjectView(R.id.every_day_item_content)
            TextView contentTxv;
            @InjectView(R.id.every_day_item_img)
            ImageView imgView;
            ImageLoader.ImageContainer container;

            public ViewHolder(View v) {
                ButterKnife.inject(this, v);
            }

            public void setKeywords(String[] keywords) {
                keywordPanel.removeAllViews();
                int times = keywords.length > 4 ? 4 : keywords.length;
                for (int i = 0; i <times; i++) {
                    keywordPanel.addView(createKeywordView(keywords[i]), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }

            private TextView createKeywordView(String str) {
                TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.keyword, null).findViewById(R.id.keyword_txv);
                textView.setBackgroundResource(bgs[new Random().nextInt(5)]);
                textView.setText(str);
                return textView;
            }
        }
    }
}
