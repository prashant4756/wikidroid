package com.example.wikidroid.ui;

import com.example.wikidroid.pojo.WikiPost;

import java.util.ArrayList;

import io.realm.RealmResults;

public interface MainActivityViewInterface {
    void showToast(String str);
    void displayResult(ArrayList<WikiPost> wikiPostRealmResults);
    void displayError(String s);
    void showProgressBar();
    void hideProgressBar();
    void launchWikiDetails(int postID);
    void displayVisitedData(RealmResults<WikiPost> wikiPostRealmResults);
}
