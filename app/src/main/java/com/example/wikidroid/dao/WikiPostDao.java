package com.example.wikidroid.dao;

import com.example.wikidroid.pojo.WikiPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class WikiPostDao {

    private Realm mRealm;

    public WikiPostDao() {
        mRealm = Realm.getDefaultInstance();
    }

    /*{
   "batchcomplete":true,
   "continue":{
      "gpsoffset":10,
      "continue":"gpsoffset||"
   },
   "query":{
      "redirects":[
         {
            "index":4,
            "from":"Sachin Tyagi",
            "to":"Solhah Singaarr"
         },
         {
            "index":3,
            "from":"Sachin The Film",
            "to":"Sachin: A Billion Dreams"
         }
      ],
      "pages":[
         {
            "pageid":57570,
            "ns":0,
            "title":"Sachin Tendulkar",
            "index":1,
            "thumbnail":{
               "source":"https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Sachin_Tendulkar_at_MRF_Promotion_Event.jpg/50px-Sachin_Tendulkar_at_MRF_Promotion_Event.jpg",
               "width":50,
               "height":45
            },
            "terms":{
               "description":[
                  "A former Indian cricketer from India and one of the greatest cricketers ever seen in the world"
               ]
            }
         }
      ]
   }
}*/
    public ArrayList<WikiPost> updateLocalFromJson(JSONObject data){
        ArrayList<WikiPost> wikiPosts = new ArrayList<>();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                JSONObject query = data.optJSONObject("query");
                if(query != null){
                    JSONArray pages = query.optJSONArray("pages");

//                    realm.where(WikiPost.class).equalTo("visited",false).findAll().deleteAllFromRealm();

                    if(pages != null){
                        for(int i = 0; i < pages.length(); i++){

                            JSONObject pageData = null, terms = null, thumbnailObject = null;
                            JSONArray descriptionArray = null;
                            String imageSource = "", description = "";

                            pageData = pages.optJSONObject(i);
                            if(pageData != null) {
                                int pageid = pageData.optInt("pageid",-1);
                                String title = pageData.optString("title", "N/A");

                                thumbnailObject = pageData.optJSONObject("thumbnail");
                                if(thumbnailObject != null && thumbnailObject.has("source"))
                                    imageSource = thumbnailObject.optString("source", "N/A");
                                terms = pageData.optJSONObject("terms");
                                if(terms != null && terms.has("description")) {
                                    descriptionArray = terms.optJSONArray("description");
                                    if(descriptionArray != null)
                                        description = descriptionArray.optString(0, "N/A");
                                }

                                WikiPost wikiPost = realm.where(WikiPost.class).equalTo("id", pageid).findFirst();
                                if(wikiPost == null)
                                    wikiPost = realm.createObject(WikiPost.class, pageid);

                                wikiPost.setTitle(title);
                                wikiPost.setDescription(description);
                                wikiPost.setThumbnailUrl(imageSource);
                                wikiPosts.add(wikiPost);
                            }
                        }
                    }
                }
            }
        });

        return wikiPosts;
    }

    public RealmResults<WikiPost> getAllWikiPost(){
        // need to be handled properly
        return mRealm.where(WikiPost.class).findAll();
    }

    public RealmResults<WikiPost> getVisitedWikiPost(){
        // need to be handled properly
        return mRealm.where(WikiPost.class).equalTo("visited",true).findAll();
    }

    public void setPostVisited(int id) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                WikiPost wikiPost = realm.where(WikiPost.class).equalTo("id", id).findFirst();
                if(wikiPost != null)
                    wikiPost.setVisited(true);
            }
        });
    }

    public String getPostUrl(int postId) {
        WikiPost wikiPost =  mRealm.where(WikiPost.class).equalTo("id", postId).findFirst();
        if(wikiPost != null )
            return wikiPost.getPageUrl();
        return null;
    }


    /*{
"batchcomplete": "",
    "query": {
        "pages": {
            "736": {
            "pageid": 736,
            "ns": 0,
            "title": "Albert Einstein",
            "contentmodel": "wikitext",
            "pagelanguage": "en",
            "pagelanguagehtmlcode": "en",
            "pagelanguagedir": "ltr",
            "touched": "2018-12-27T10:13:37Z",
            "lastrevid": 875113726,
            "length": 154715,
            "fullurl": "https://en.wikipedia.org/wiki/Albert_Einstein",
            "editurl": "https://en.wikipedia.org/w/index.php?title=Albert_Einstein&action=edit",
            "canonicalurl": "https://en.wikipedia.org/wiki/Albert_Einstein"
            }
    }
}
}*/
    public String updatePostUrl(int pageId, JSONObject response) {
        final String[] url = {null};
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try{
                    JSONObject query = response.getJSONObject("query");
                    JSONObject pages = query.getJSONObject("pages");
                    JSONObject pageDetails = pages.getJSONObject(String.valueOf(pageId));
                    String tempurl = pageDetails.optString("fullurl", null);
                    url[0] = tempurl;
                    WikiPost wikiPost = realm.where(WikiPost.class).equalTo("id", pageId).findFirst();
                    if(wikiPost != null)
                        wikiPost.setPageUrl(url[0]);
                }catch (Exception e){

                }
            }
        });
        return url[0];
    }
}
