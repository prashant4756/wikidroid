package com.example.wikidroid.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.example.wikidroid.API;
import com.example.wikidroid.dao.WikiPostDao;
import com.example.wikidroid.network.NetworkClient;
import com.example.wikidroid.pojo.WikiPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.RealmResults;

public class MainActivityPresenter implements MainActivityPresenterInterface{

    private static final String TAG = "MainActivityPresenter";
    MainActivityViewInterface mainActivityViewInterface;
    private WikiPostDao wikiPostDao;

    public MainActivityPresenter(MainActivityViewInterface mainActivityViewInterface) {
        this.mainActivityViewInterface = mainActivityViewInterface;
        this.wikiPostDao = new WikiPostDao();
    }

    @SuppressLint("CheckResult")
    @Override
    public void getResultsBasedOnQuery(SearchView searchView) {
        getObservableQuery(searchView)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        if(s == null || s.isEmpty()){
                            RealmResults<WikiPost> wikiPosts = wikiPostDao.getVisitedWikiPost();
                            if(wikiPosts.size() > 0)
                                mainActivityViewInterface.showBannerText("Showing visited pages");
                            else
                                mainActivityViewInterface.showBannerText("You haven't visited any pages yet!");
                            mainActivityViewInterface.displayVisitedData(wikiPosts);
                            return false;
                        }else{
                            mainActivityViewInterface.showProgressBar();
                            return true;
                        }
                    }
                })
                .debounce(2, TimeUnit.SECONDS)
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public Observable<String> apply(@NonNull String queryString) throws Exception {
                        return NetworkClient.getRetrofit().create(API.class)
                                .getWikiResultsFromQuery(queryString);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver());

    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchPostUrl(int postId) {
        String wikiPostUrl = wikiPostDao.getPostUrl(postId);
        if(wikiPostUrl != null)
            mainActivityViewInterface.launchWikiDetails(wikiPostUrl);

        else {
            mainActivityViewInterface.showProgressBar();
            NetworkClient.getRetrofit().create(API.class)
                .getPageUrl(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        //response
                        Log.d(TAG, "urlFetch : "+s);
                        try {
                            mainActivityViewInterface.hideProgressBar();
                            String url = wikiPostDao.updatePostUrl(postId, new JSONObject(s));
                            if(url != null)
                                mainActivityViewInterface.launchWikiDetails(url);
                            else
                                mainActivityViewInterface.displayError("Unable to fetch details right now!");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"Error"+e.getMessage());
                        mainActivityViewInterface.hideProgressBar();
                        mainActivityViewInterface.displayError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"URL fetch completed");
                    }
                });
        }
    }

    @Override
    public void setVisitedData() {
        RealmResults<WikiPost> wikiPosts = wikiPostDao.getVisitedWikiPost();
        if(wikiPosts.size() > 0)
            mainActivityViewInterface.showBannerText("Showing visited pages");
        else
            mainActivityViewInterface.showBannerText("You haven't visited any pages yet!");
        mainActivityViewInterface.displayVisitedData(wikiPosts);
    }

    private Observable<String> getObservableQuery(SearchView searchView){

        final PublishSubject<String> publishSubject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                publishSubject.onNext(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                publishSubject.onNext(newText);
                return true;
            }
        });

        return publishSubject;
    }

    public DisposableObserver<String> getObserver(){
        return new DisposableObserver<String>() {

            @Override
            public void onNext(@NonNull String stringResponse) {
                Log.d(TAG,"OnNext : "+stringResponse);
                try {

                    ArrayList<WikiPost> results = wikiPostDao.updateLocalFromJson(new JSONObject(stringResponse));
                    mainActivityViewInterface.hideProgressBar();
                    mainActivityViewInterface.showBannerText("Showing results from wikipedia");
                    mainActivityViewInterface.displayResult(results);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mainActivityViewInterface.hideProgressBar();
                    mainActivityViewInterface.displayError(e.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                mainActivityViewInterface.hideProgressBar();
                mainActivityViewInterface.hideMessageBanner();
                mainActivityViewInterface.displayError("Error fetching Data");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }
}
