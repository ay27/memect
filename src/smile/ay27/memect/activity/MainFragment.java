package smile.ay27.memect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import smile.ay27.memect.MemectApplication;
import smile.ay27.memect.R;
import smile.ay27.memect.persistent.SerializableList;
import smile.ay27.memect.persistent.Utils;
import smile.ay27.memect.widget.CardsAnimationAdapter;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ay27 on 15/10/23.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final static String[] links = MemectApplication.appContext.getResources().getStringArray(R.array.catalog_link);
    private final int position;
    @InjectView(R.id.main_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.main_content_list)
    ListView listView;
    private ContentLoaderAdapter adapter;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            adapter.refresh();
        }
    };

    public MainFragment(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 这一句一定不能省，不然menu不能出来
        setHasOptionsMenu(true);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = onStep1SetUpListView(inflater, container);
        onStep2SetUpAdapter();
        return view;
    }

    public View onStep1SetUpListView(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

//        View listEnd = inflater.inflate(R.layout.list_end, null);
//        listView.addFooterView(listEnd);

        swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        return view;
    }

    public void onStep2SetUpAdapter() {
        adapter = new ContentLoaderAdapter(getActivity());
        CardsAnimationAdapter cardsAnimationAdapter = new CardsAnimationAdapter(adapter, listView);
        listView.setAdapter(cardsAnimationAdapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("Link", item.href);
        intent.putExtra("Title", item.title);
        intent.putExtra("Position", this.position);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

//        swipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(true);
//                adapter.refresh();
//            }
//        });

        adapter.loadFromCache();
    }

    static class Item implements Serializable {
        String title;
        String content;
        String href;

        public Item(String title, String content, String href) {
            this.title = title;
            this.content = content;
            this.href = href;
        }
    }

    class ContentLoaderAdapter extends BaseAdapter {

        private SerializableList items;
        private Context context;

        public ContentLoaderAdapter(Context context) {
            items = new SerializableList();
            this.context = context;
        }

        public void loadFromCache() {
            items = (SerializableList) Utils.loadObject("mainFragment" + position);
            if (items == null) {
                items = new SerializableList();
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        adapter.refresh();
                    }
                });
            } else {
                notifyDataSetInvalidated();
                Log.i("mainFragment", "load from cache");
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Item getItem(int position) {
            return (Item) items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.main_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleTxv.setText(((Item) items.get(position)).title);
            holder.contentTxv.setText(((Item) items.get(position)).content);

            return convertView;
        }

        public void refresh() {
            new AsyncTask<Void, Void, ArrayList>() {
                public static final String TAG = "request_data";

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                protected ArrayList doInBackground(Void... params) {
                    items = new SerializableList();

                    Document doc = null;
                    try {
                        doc = Jsoup.connect(links[position]).timeout(10000).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    Element index = doc.getElementById("index");
                    for (Element li : index.getElementsByTag("li")) {
                        Elements links = li.select("a");
                        if (links.size() < 2) continue;

                        String date = links.get(0).text();
                        String href = links.get(1).attr("href");

                        Elements contents = li.select("ul").select("li");
                        StringBuilder contentBuilder = new StringBuilder();
                        for (Element line : contents) {
                            contentBuilder.append(" - ").append(line.text()).append("\n");
                        }
                        items.add(new Item(date, contentBuilder.toString(), href));

                    }

                    Utils.saveObject(items, "mainFragment" + position);

                    return items;
                }


                @Override
                protected void onPostExecute(ArrayList arrayList) {
                    super.onPostExecute(arrayList);
                    if (arrayList == null) {
                        Toast.makeText(context, "network timeout", Toast.LENGTH_LONG).show();
                    }
                    notifyDataSetInvalidated();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }.execute();
        }


        class ViewHolder {
            @InjectView(R.id.list_item_title)
            TextView titleTxv;
            @InjectView(R.id.list_item_content)
            TextView contentTxv;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }

    }
}
