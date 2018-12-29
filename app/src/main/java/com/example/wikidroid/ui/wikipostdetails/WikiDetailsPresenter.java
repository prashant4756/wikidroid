package com.example.wikidroid.ui.wikipostdetails;

import android.webkit.WebView;

public class WikiDetailsPresenter implements WikiDetailsPresenterInterface {
    private WebDetailsViewInterface webDetailsViewInterface;

    public WikiDetailsPresenter(WebDetailsViewInterface webDetailsViewInterface) {
        this.webDetailsViewInterface = webDetailsViewInterface;
    }

    @Override
    public void getPostUrl(WebView webView, String postID) {
        //fetch from local/ if not present get from api
        String url = "http://www.google.com";
        webDetailsViewInterface.loadUrl(url);
    }
}
