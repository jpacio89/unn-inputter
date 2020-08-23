package com.unn.inputter.service;

import com.unn.inputter.Config;
import com.unn.inputter.plugins.openml.OpenmlDatasetProvider;
import com.unn.inputter.plugins.openml.OpenmlLocator;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

import java.io.IOException;

public class DataService {
    Retrofit retrofit;
    DatacenterService service;

    public DataService() { }

    public void init() {
        this.retrofit = new Retrofit.Builder()
            .baseUrl(String.format("%s://%s:%s",
                Config.DATACENTER_PROTOCOL,
                Config.DATACENTER_HOST,
                Config.DATACENTER_PORT))
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        this.service = retrofit.create(DatacenterService.class);
    }

    public void loadOpenML(String datasetId) {
        try {
            OpenmlDatasetProvider provider = new OpenmlDatasetProvider();
            provider.init(new OpenmlLocator(datasetId));
            String csv = provider.load();
            String namespace = String.format("com.unn.openml.%s", datasetId);
            Call<String> call = this.service.storeDataset(namespace, csv);
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
