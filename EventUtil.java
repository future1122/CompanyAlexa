package com.wistron.demo.tool.teddybear.avs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.willblaschko.android.alexa.connection.ClientUtil;
import com.willblaschko.android.alexa.data.Event;

import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by damon on 17-10-18.
 */

public class EventUtil {
    //OkHttpClient for transfer of data
    private Request.Builder mRequestBuilder = null;
    private MultipartBody.Builder mBodyBuilder;
    private Context mContext;

    private static EventUtil instance = null ;


    public static EventUtil getInstance(Context context){
        if(instance == null){
            instance = new EventUtil(context);
        }
        return instance;
    }
    private EventUtil(Context context){
        mContext = context;
    }

    public void sendEvent(String event){
        Log.i(Common.TAG,"Event : "+event);

        mRequestBuilder = new Request.Builder();
        mRequestBuilder.url(getEventsUrl());
        mRequestBuilder.addHeader("Authorization","Bearer "+ getAccessToken());

        mBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("metadata","metadata", RequestBody.create(MediaType.parse("application/json; charset=UTF-8")
                        ,event
                ));
        mRequestBuilder.post(mBodyBuilder.build());
        Request request = mRequestBuilder.build();
        OkHttpClient okHttpClient = ClientUtil.getTLS12OkHttpClient();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int code  = response.code();
        Log.d(Common.TAG,"response code : " + code);
    }
    private String getEventsUrl() {
        return new StringBuilder()
                .append(mContext.getString(com.willblaschko.android.alexa.R.string.alexa_api))
                .append("/")
                .append(mContext.getString(com.willblaschko.android.alexa.R.string.alexa_api_version))
                .append("/")
                .append("events")
                .toString();
    }

    private String getAccessToken() {
        String token = "";
        Log.i(Common.TAG, "Old access token: " + token);
        SharedPreferences preferences = mContext.getSharedPreferences(Common.TOKEN_PREFERENCE_KEY, Context
                .MODE_PRIVATE);
        token = preferences.getString(Common.PREF_ACCESS_TOKEN, token);

//        Log.i(Common.TAG,"Get access token from preference: " + token);
        return token;
    }

    private static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public static String getPlaybackStartedEvent(String token,long offsetInMilliseconds) {
        Event.Builder builder = new Event.Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackStarted")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token)
                .setPlayloadOffsetInMilliseconds(offsetInMilliseconds);
        return builder.toJson();
    }

    public static String getSpeechRecognizerEvent() {
        Event.Builder builder = new Event.Builder();
        builder.setHeaderNamespace("SpeechRecognizer")
                .setHeaderName("Recognize")
                .setHeaderMessageId(getUuid())
                .setHeaderDialogRequestId("dialogRequest-321")
                .setPayloadFormat("AUDIO_L16_RATE_16000_CHANNELS_1")
                .setPayloadProfile("CLOSE_TALK");
//                .setPayloadProfile("FAR_FIELD");
//                .setPayloadProfile("NEAR_FIELD");
        return builder.toJson();
    }


    public static String getExpectSpeechTimeOutEvent(){
        Event.Builder builder = new Event.Builder();
        builder.setHeaderNamespace("SpeechRecognizer")
                .setHeaderName("ExpectSpeechTimeOut")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }


    public static String getSetlertSucceededEvent(String token){
        Event.Builder builder = new Event.Builder();
        builder.setHeaderNamespace("Alerts")
                .setHeaderName("SetAlertSucceeded")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }
    public static String getSetAlertFailedEvent(String token){
        Event.Builder builder = new Event.Builder();
        builder.setHeaderNamespace("Alerts")
                .setHeaderName("SetAlertFailed")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getDeleteAlertSucceededEvent(String token){
        Event.Builder builder = new Event.Builder();
        builder.setHeaderNamespace("Alerts")
                .setHeaderName("DeleteAlertSucceeded")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    
}
