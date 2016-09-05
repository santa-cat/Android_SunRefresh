package com.example.santa.sunrefresh;

/**
 * Created by santa on 16/6/24.
 */
public interface PullHandler {
    public void onRefreshBegin(final PullRefreshLayout layout);

    public void onRefreshFinshed();
}
