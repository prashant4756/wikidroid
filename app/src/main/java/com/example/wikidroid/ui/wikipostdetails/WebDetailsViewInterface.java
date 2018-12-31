package com.example.wikidroid.ui.wikipostdetails;

public interface WebDetailsViewInterface {
    void loadUrl(String url);
    void displayError(String s);
    void showProgressBar();
    void hideProgressBar();
}
