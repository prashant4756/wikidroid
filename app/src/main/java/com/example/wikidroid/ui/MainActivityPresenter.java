package com.example.wikidroid.ui;

import android.support.v7.widget.SearchView;
import android.util.Log;

import com.example.wikidroid.API;
import com.example.wikidroid.dao.WikiPostDao;
import com.example.wikidroid.network.NetworkClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainActivityPresenter implements MainActivityPresenterInterface{

    private static final String TAG = "MainActivityPresenter";
    MainActivityViewInterface mainActivityViewInterface;
    private WikiPostDao wikiPostDao;

    public MainActivityPresenter(MainActivityViewInterface mainActivityViewInterface) {
        this.mainActivityViewInterface = mainActivityViewInterface;
        this.wikiPostDao = new WikiPostDao();
    }

    @Override
    public void getResultsBasedOnQuery(SearchView searchView) {
        getObservableQuery(searchView)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        if(s.equals("")){
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

    @Override
    public void launchWikiDetails(int postID) {
        mainActivityViewInterface.launchWikiDetails(postID);
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
                    wikiPostDao.updateLocalFromJson(new JSONObject(stringResponse));
                    mainActivityViewInterface.hideProgressBar();
                    mainActivityViewInterface.displayResult(wikiPostDao.getAllWikiPost());
                } catch (JSONException e) {
                    e.printStackTrace();
                    mainActivityViewInterface.hideProgressBar();
                    mainActivityViewInterface.displayError(e.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                mainActivityViewInterface.hideProgressBar();
                mainActivityViewInterface.displayError("Error fetching Movie Data");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
            }
        };
    }
}
