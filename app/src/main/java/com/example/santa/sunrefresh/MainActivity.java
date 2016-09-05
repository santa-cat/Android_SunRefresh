package com.example.santa.sunrefresh;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SeaLayout mSeaLayout;
    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private HomeAdapter mAdapter;
    private PullRefreshLayout mPullrefreshlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mSunView = (SunView) findViewById(R.id.sunview);
//        mSeaView = (SeaView) findViewById(R.id.seaview);

        //bezier2 = (Bezier2) findViewById(R.id.bezier);
//        mSeaLayout = (SeaLayout) findViewById(R.id.sealayout);
//
//        Button button = (Button) findViewById(R.id.button);
//        if(button != null) {
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //bezier2.setMode(true);
//                    mSeaLayout.onUIRefreshing();
//                }
//            });
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //设置左上角返回箭头
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter = new HomeAdapter(this));

        mPullrefreshlayout = (PullRefreshLayout) findViewById(R.id.pullrefreshlayout);
        mPullrefreshlayout.setPullHandler(new PullHandler(){
            @Override
            public void onRefreshBegin(final PullRefreshLayout layout) {
                Log.d("DEBUG", "onRefreshBegin");

                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //从客户端获取数据

                        layout.refreshComplete();
                    }
                }, 4000);

            }

            @Override
            public void onRefreshFinshed() {
                //更新数据
                addData();
            }
        });
        //mRecyclerView.setItemAnimator(new );


    }

//    public void onStop(View view) {
//        //bezier2.setMode(false);
//        mSeaLayout.onUIRefreshed();
//
//    }
    protected void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i <= 'Z'; i++)
        {
            mDatas.add("" + (char) i);
        }
    }

    public void addData(){
        for (int i = 'D'; i < 'E'; i++)
        {
            mDatas.add("" + (char) i);
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private LayoutInflater mInflater;
        public HomeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);

        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.list_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.text);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, tv.getText(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate( R.menu.menu_content , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(MainActivity.this, "action_search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "action_settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bookmark:
                Toast.makeText(MainActivity.this, "action_bookmark", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_unfollow:
                Toast.makeText(MainActivity.this, "action_unfollow", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
