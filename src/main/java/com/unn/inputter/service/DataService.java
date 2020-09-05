package com.unn.inputter.service;

import com.unn.common.dataset.DatasetDescriptor;
import com.unn.common.dataset.Header;
import com.unn.common.globals.NetworkConfig;
import com.unn.common.server.services.DatacenterService;
import com.unn.common.utils.Utils;
import com.unn.inputter.Config;
import com.unn.inputter.plugins.openml.OpenmlDatasetProvider;
import com.unn.inputter.plugins.openml.OpenmlLocator;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
        this.service = Utils.getDatacenter(true);
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
