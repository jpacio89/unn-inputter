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
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
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
        String[] vals = csv.split("\n");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), csv);
                try {
                    // service.registerAgent(new Thing()).execute();
                    DatasetDescriptor descriptor = new DatasetDescriptor()
                            .withNamespace(namespace)
                            .withHeader(new Header().withNames(vals[0].split(",")));
                    service.registerAgent(descriptor).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Call<String> call = service.storeDataset(namespace, body);
                    call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
