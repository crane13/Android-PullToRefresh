/*
 * Copyright (c) 2015 NeuLion, Inc. All Rights Reserved.
 */
package com.handmark.pulltorefresh.samples;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CommonListViewActivity extends Activity
{
    
    private LinkedList<String> mListItems;
    private PullToRefreshListView mPullRefreshListView;
    private ArrayAdapter<String> mAdapter;
    
    private int pageIndex;
    private int pageSize = 10;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_list);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
            
            
        });

        mPullRefreshListView.setMode(Mode.BOTH);
        
        // Add an end-of-list listener
        mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
//                Toast.makeText(PullToRefreshListActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
            }
        });

        ListView actualListView = mPullRefreshListView.getRefreshableView();

        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListItems);

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
        soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);

        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        actualListView.setAdapter(mAdapter);
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        boolean isLoadMore;
        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mListItems.addFirst("Added after refresh...");
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshListView.onRefreshComplete();
            

            super.onPostExecute(result);
        }
    }
    GetDataTask dataTask;
    private void getData(boolean isLoadMore)
    {
        if(dataTask != null && !dataTask.isCancelled())
        {
            dataTask.cancel(true);
        }
        dataTask = new GetDataTask();
        dataTask.execute();
    }
    
    private OnRefreshListener2<ListView> onRefreshListener2 = new OnRefreshListener2<ListView>()
    {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
        {
            
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
        {
            // TODO Auto-generated method stub
            
        }
    };
    
    public void updateProgramList(LinkedList<String> arrStrings, int curPage, int totalCount)
    {
        if (mAdapter == null)
        {
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListItems);
            mPullRefreshListView.setAdapter(mAdapter);
        }
        else
        {
            if (curPage == 1)
            {
                mListItems.clear();
                mListItems.addAll(arrStrings);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                mListItems.addAll(arrStrings);
                mAdapter.notifyDataSetChanged();
            }
        }
//        mPullRefreshListView.setEmptyView(mNoProgramsText);

        mPullRefreshListView.onRefreshComplete();
        if (pageSize > 0)
        {
            if ((curPage * pageSize) < totalCount)
                mPullRefreshListView.setMode(Mode.BOTH);
            else
                mPullRefreshListView.setMode(Mode.PULL_FROM_START);
        }
        else
        {
            mPullRefreshListView.setMode(Mode.PULL_FROM_START);
        }

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm z");
        String lastUpdated = format.format(new Date(System
                .currentTimeMillis()));
        mPullRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(
                "Last Updated:" + lastUpdated);
    }
    
    private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler" };
}
