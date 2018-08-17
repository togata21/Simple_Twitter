package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.ParseException;


/**
 * Created by togata on 7/3/18.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private List<Tweet> mTweets;
    Context context;

    public TweetAdapter(List<Tweet> tweets){
        mTweets = tweets;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Tweet tweet = mTweets.get(position);
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvHandle.setText("@"+tweet.user.screenName + "  ·  "+getRelativeTimeAgo(tweet.createdAt));
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivProfileImage);

        if (tweet.favorite==true)
            holder.ibFavorite.setImageResource(R.drawable.ic_vector_heart);
        if (tweet.retweeted==true)
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvDate;
        public TextView tvHandle;
        public ImageButton ibPost;
        public ImageButton ibRetweet;
        public ImageButton ibFavorite;
        public ViewHolder(View view){
            super(view);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            ibPost = (ImageButton) itemView.findViewById(R.id.ibReply);
            ibPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Tweet reply_tweet = mTweets.get(position);
                    Intent intent = new Intent(v.getContext(), ComposeActivity.class);
                    intent.putExtra("reply", true);
                    intent.putExtra("uid", reply_tweet.uid);
                    Activity temp = (Activity) v.getContext();
                    temp.startActivityForResult(intent, 2);
                }
            });

            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Tweet retweet = mTweets.get(position);
                    if (retweet.retweeted == false) {
                        retweet.retweeted = true;
                        TwitterClient client = TwitterApp.getRestClient(v.getContext());
                        client.retweet(retweet.uid, v, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(context, "retweet", Toast.LENGTH_SHORT).show();
                                ibRetweet.setImageResource(R.drawable.ic_vector_retweet);

                                Intent intent = new Intent(context, TimelineActivity.class);
                                Activity temp = (Activity) context;
                                temp.startActivityForResult(intent, 2);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                                Log.d("adapter", errorResponse.toString());
                                throwable.printStackTrace();
                            }
                        });
                    }
                    else{
                        retweet.retweeted = false;
                        TwitterClient client = TwitterApp.getRestClient(v.getContext());
                        client.unretweet(retweet.uid, v, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(context, "unretweet", Toast.LENGTH_SHORT).show();
                                ibRetweet.setImageResource(R.drawable.ic_vector_retweet);

                                Intent intent = new Intent(context, TimelineActivity.class);
                                Activity temp = (Activity) context;
                                temp.startActivityForResult(intent, 2);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                                Log.d("adapter", errorResponse.toString());
                                throwable.printStackTrace();
                            }
                        });
                    }
                }
            });

            ibFavorite = (ImageButton) itemView.findViewById(R.id.ibFavorite);
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Tweet retweet = mTweets.get(position);
                    TwitterClient client = TwitterApp.getRestClient(v.getContext());
                    if (!retweet.favorite) {
                        ibFavorite.setImageResource(R.drawable.ic_vector_heart);
                        retweet.favorite = true;
                        //Toast.makeText(v.getContext(), position+" favorite", Toast.LENGTH_SHORT).show();
                        client.favorite(retweet.uid, v, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(context, "favorite", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, TimelineActivity.class);
                                Activity temp = (Activity) context;
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                                Log.d("adapter", errorResponse.toString());
                                throwable.printStackTrace();
                            }
                        });
                    }
                    else{
                        ibFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
                        retweet.favorite = false;
                        client.unFavorite(retweet.uid, v, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(context, "unfavorite", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, TimelineActivity.class);
                                Activity temp = (Activity) context;
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                                Log.d("adapter", errorResponse.toString());
                                throwable.printStackTrace();
                            }
                        });
                    }
                }
            });
        }
    }


    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        String newDate = "";
        try {
            long dateMillis;
            dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            int index = relativeDate.indexOf(' ');
            newDate = relativeDate.substring(0,index)+relativeDate.substring(index+1, index+2);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
