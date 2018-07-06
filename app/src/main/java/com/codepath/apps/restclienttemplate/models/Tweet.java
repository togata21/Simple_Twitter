package com.codepath.apps.restclienttemplate.models;

/**
 * Created by togata on 7/2/18.
 */

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


@Parcel
public class Tweet {
    public String body;
    public long uid;
    public User user;
    public String createdAt;
    public boolean favorite;
    public String date;
    public boolean retweeted;

    public Tweet(){}

    //deserialize JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.favorite = jsonObject.getBoolean("favorited");
        tweet.date = jsonObject.getString("created_at");
        tweet.retweeted = jsonObject.getBoolean("retweeted");


        return tweet;
    }

    /// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");



}