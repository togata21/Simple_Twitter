package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    Context context;

    EditText post;
    TextView charCount;

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            int chars = 140-s.length();
            if (chars <=0){
                post.setInputType(0);
            }
            charCount.setText(String.valueOf(chars));
        }

        public void afterTextChanged(Editable s) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        post = (EditText) findViewById(R.id.etPost);
        charCount = (TextView) findViewById(R.id.tvCharacterCount);
        post.addTextChangedListener(mTextEditorWatcher);
        setTitle("");

    }


    public void launchComposeView(View v) {
        // first parameter is the context, second is the class of the activity to launch
        Intent i = new Intent(v.getContext(), TimelineActivity.class);
        startActivity(i); // brings up the second activity
    }

    public void tweet(View v){
        String message = post.getText().toString();
        context = v.getContext();

        if (!getIntent().getBooleanExtra("reply", false)){
            client.sendTweet(message, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweet = null;
                    try {
                        tweet = Tweet.fromJSON(response);
                        Intent i = new Intent();//v.getContext(), TimelineActivity.class);
                        i.putExtra("tweet", Parcels.wrap(tweet)); // pass arbitrary data to launched activity
                        setResult(RESULT_OK, i);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        else {

                client.sendTweet(message, getIntent().getLongExtra("uid", 0), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet tweet = null;
                        try {
                            tweet = Tweet.fromJSON(response);
                            Intent i = new Intent();//v.getContext(), TimelineActivity.class);
                            i.putExtra("tweet", Parcels.wrap(tweet)); // pass arbitrary data to launched activity
                            setResult(RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        }

    }

    /*public void back(View v){
        Intent intent = new Intent(v.getContext(), TimelineActivity.class);
        startActivity(intent);
    }*/


}
