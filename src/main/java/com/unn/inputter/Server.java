package com.unn.inputter;

import com.google.gson.Gson;
import com.unn.common.server.StandardResponse;
import com.unn.common.server.StatusResponse;
import com.unn.inputter.Config;
import com.unn.inputter.service.DataService;

import static spark.Spark.*;

public class Server {
    static final String SUCCESS = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS));
    static DataService service;

    public Server() { }

    public static void serve() {
        service = new DataService();
        service.init();

        port(Config.INPUTTER_PORT);
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        // Loads a specific openml dataset
        post("/dataset/load/openml/:datasetId", (request, response) -> {
            String datasetId = request.params("datasetId");
            service.loadOpenML(datasetId);
            return SUCCESS;
        });

    }

}
