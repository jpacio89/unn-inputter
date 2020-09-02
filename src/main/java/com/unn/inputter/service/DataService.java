package com.unn.inputter.service;

import com.unn.inputter.Config;
import com.unn.inputter.models.DatasetDescriptor;
import com.unn.inputter.models.Header;
import com.unn.inputter.plugins.openml.OpenmlDatasetProvider;
import com.unn.inputter.plugins.openml.OpenmlLocator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class DataService {
    Retrofit retrofit;
    DatacenterService service;

    public DataService() { }

    public void init() {
        String url = String.format("%s://%s:%s",
            Config.DATACENTER_PROTOCOL,
            Config.DATACENTER_HOST,
            Config.DATACENTER_PORT);
        this.retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(JacksonConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        this.service = retrofit.create(DatacenterService.class);
    }

    public void loadOpenML(String datasetId) {
        OpenmlDatasetProvider provider = new OpenmlDatasetProvider();
        provider.init(new OpenmlLocator(datasetId));
        String csv = provider.load();
        String namespace = String.format("com.unn.openml.%s", datasetId);
        this.processDataset(namespace, csv);
    }

    void processDataset(String namespace, String csv) {
        new Thread(() -> {
            this.resetBrain();
            this.registerAgent(namespace, csv);
            this.uploadDataset(namespace, csv);
        }).start();
    }

    void resetBrain() {
        try {
            service.resetBrain().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registerAgent(String namespace, String csv) {
        try {
            String[] rows = csv.split("\n");
            Header header = new Header()
                .withNames(rows[0].split(","));
            DatasetDescriptor descriptor = new DatasetDescriptor()
                .withNamespace(namespace)
                .withHeader(header);
            service.registerAgent(descriptor).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void uploadDataset(String namespace, String csv) {
        try {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), csv);
            Call<String> call = service.storeDataset(namespace, body);
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
