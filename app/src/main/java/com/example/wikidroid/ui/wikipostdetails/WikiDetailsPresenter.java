package com.example.wikidroid.ui.wikipostdetails;

import android.webkit.WebView;

public class WikiDetailsPresenter implements WikiDetailsPresenterInterface {
    private WebDetailsViewInterface webDetailsViewInterface;

    public WikiDetailsPresenter(WebDetailsViewInterface webDetailsViewInterface) {
        this.webDetailsViewInterface = webDetailsViewInterface;
    }

    @Override
    public void loadPostUrl(String url) {
        webDetailsViewInterface.loadUrl(url);
    }
}
