package com.example.wikidroid.pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WikiPost extends RealmObject {

    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
