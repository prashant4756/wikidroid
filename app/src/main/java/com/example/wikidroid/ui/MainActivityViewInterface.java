package com.example.wikidroid.ui;

import com.example.wikidroid.pojo.WikiPost;

import io.realm.RealmResults;

public interface MainActivityViewInterface {
    void showToast(String str);
    void displayResult(RealmResults<WikiPost> wikiPostRealmResults);
    void displayError(String s);
    void showProgressBar();
    void hideProgressBar();
}
