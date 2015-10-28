package smile.ay27.memect.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.toolbox.ImageLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import smile.ay27.memect.R;
import smile.ay27.memect.network.ImageCacheManager;
import smile.ay27.memect.persistent.SerializableList;
import smile.ay27.memect.persistent.Utils;
import smile.ay27.memect.widget.CardsAnimationAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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

    private String title;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.inject(this);

        title = getIntent().getStringExtra("Title");
        String link = getIntent().getStringExtra("Link");
        position = getIntent().getIntExtra("Position", 0);

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
        adapter.loadFromCache();
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
//        Item item = adapter.getItem(position);
//        String[] data = new String[item.links.length+1];
//        data[0] = item.src;
//        for (int i = 0; i < item.links.length; i++) {
//            data[i+1] = item.links[i];
//        }
//        new AlertDialog.Builder(this)
//                .setItems(data, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String href = null;
//                        if (which == 0) {
//                            href = item.srcHref;
//                        }
//                        else {
//                            href = item.links[which-1];
//                        }
//
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(href));
//                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                        intent.addCategory(Intent. CATEGORY_DEFAULT);
//                        startActivity(intent);
//                    }
//                })
//                .show();
    }


    static class Item implements Serializable {
        String src;
        String srcHref;
        String[] keywords;
        String content;
        String picHref;
        String[] links;

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

            Element text = thread.getElementsByClass("text").get(0);
            content = text.text();
            Elements inLinks = text.select("a");
            content = fixUrl(content, inLinks);

            links = new String[inLinks.size()];
            for (int i = 0; i < inLinks.size(); i++) {
                links[i] = inLinks.get(i).attr("href");
            }

            Elements pics = thread.getElementsByClass("original_pic");
            if (pics.size() > 0) {
                picHref = pics.select("img").get(0).attr("src");
                if (!picHref.startsWith("http://")) {
                    picHref = "http:" + picHref;
                }
            }
        }

        private String fixUrl(String content, Elements links) {
            StringBuilder sb = new StringBuilder(content);
            int delta = 0;
            for (Element link : links) {
                int start = content.indexOf(link.text());
                sb.insert(delta+start, ' ');
                sb.insert(delta+start+link.text().length()+1, ' ');
                delta += 2;
            }
            return sb.toString();
        }
    }

    class DetailAdapter extends BaseAdapter {

        String link;
        Context context;
        SerializableList<Item> headlines;
        SerializableList<Item> dynamicList;

        public DetailAdapter(Context context, String link) {
            this.context = context;
            this.link = link;

            headlines = new SerializableList<>();
            dynamicList = new SerializableList<>();
        }

        public void refresh() {
            headlines = new SerializableList<>();
            dynamicList = new SerializableList<>();

            new AsyncTask<Void, Void, List>() {

                @Override
                protected void onPreExecute() {
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                protected List doInBackground(Void... params) {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(link).timeout(10000).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    Element container = doc.getElementById("container");

                    for (Element thread : container.getElementsByClass("thread")) {
                        if (thread.className().contains("headline")) {
                            headlines.add(new Item(thread));
                        } else {
                            dynamicList.add(new Item(thread));
                        }
                    }

                    Utils.saveObject(headlines, "Detail_Headline" + position + "_" + title);
                    Utils.saveObject(dynamicList, "Detail_Dynamic"+position+"_"+title);

                    return headlines;
                }

                @Override
                protected void onPostExecute(List list) {
                    super.onPostExecute(list);

                    if (list == null) {
                        Toast.makeText(context, "network timeout", Toast.LENGTH_LONG).show();
                    }

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
                return (Item) headlines.get(position);
            } else {
                return (Item) dynamicList.get(position - headlines.size());
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

            holder.imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailActivity.this, ImgDetailActivity.class);
                    intent.putExtra("picHref", item.picHref);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            return convertView;
        }

        public void loadFromCache() {

            headlines = (SerializableList<Item>) Utils.loadObject("Detail_Headline"+position+"_"+title);
            dynamicList = (SerializableList<Item>) Utils.loadObject("Detail_Dynamic"+position+"_"+title);

            if (headlines == null || dynamicList == null) {

                headlines = new SerializableList<>();
                dynamicList = new SerializableList<>();

                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        adapter.refresh();
                    }
                });
            }
            else {
                notifyDataSetInvalidated();
            }
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
