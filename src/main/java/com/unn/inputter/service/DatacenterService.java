package com.unn.inputter.service;

import com.unn.inputter.models.DatasetDescriptor;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DatacenterService {
    @POST("/dataset/{namespace}/store/raw")
    Call<String> storeDataset(
        @Path("namespace") String namespace,
        @Body RequestBody body
    );

    @POST("/dataset/register")
    Call<String> registerAgent(
        @Body DatasetDescriptor body
    );


}
