package com.example.wikidroid.ui.wikipostdetails;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wikidroid.R;
import com.example.wikidroid.ui.MainActivityPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity implements WebDetailsViewInterface{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private WikiDetailsPresenter wikiDetailsPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        setUpViews();
        setupMVP();
        fetchUrl("postID");
    }

    private void fetchUrl(String postID) {
        wikiDetailsPresenter.getPostUrl(webView, postID);
    }

    private void setupMVP(){
        wikiDetailsPresenter = new WikiDetailsPresenter(this);
    }

    private void setUpViews() {
        setSupportActionBar(toolbar);
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void displayError(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
