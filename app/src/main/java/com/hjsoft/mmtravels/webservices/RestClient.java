package com.hjsoft.mmtravels.webservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjsoft.mmtravels.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hjsoft on 27/2/17.
 */
public class RestClient {

    private static String BASE_URL= BuildConfig.BASE_URL;
    //private static String BASE_URL= "http://183.82.147.129:1353/api/";
    private static API REST_CLIENT;
    //http://192.168.1.6:1522/api/
    //http://183.82.147.129:1353/api/
    //http://192.168.1.8:92/api (working)


    static {
        setupRestClient();
    }

    public static API get() {
        return REST_CLIENT;
    }

    private static void setupRestClient(){

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        REST_CLIENT=retrofit.create(API.class);
    }
}


