package com.example.wikidroid.pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WikiPost extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private String thumbnailUrl;
    private String description;
    private String pageUrl;
    private boolean visited;

    public WikiPost() {
    }

    public WikiPost(int id, String title, String desc) {
        this.id = id;
        this.title = title;
        this.description = desc;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
