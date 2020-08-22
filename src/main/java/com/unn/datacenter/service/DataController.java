package com.unn.datacenter.service;

import com.google.gson.Gson;
import com.unn.datacenter.models.*;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class DataController {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static DataService service;

    public DataController() { }


    public static void serve() {
        service = new DataService();
        service.init();

        // Installs/resets a brain
        post("/brain/reset", (request, response) -> {
            return SUCCESS;
        });

    }

}
