package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by togata on 7/2/18.
 */

@Parcel
public class User {

    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    //deserialize JSON
    public User(){}

    public static User fromJSON(JSONObject json) throws JSONException{

        User user = new User();

        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");

        return user;
    }
}