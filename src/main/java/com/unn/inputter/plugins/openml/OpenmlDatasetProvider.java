package com.unn.inputter.plugins.openml;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.unn.inputter.plugins.openml.OpenmlLocator;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;

public class OpenmlDatasetProvider /* extends DatasetProvider */ {
    final String apiKey = "afd8250e50b774f1cd0b4a4534a1ae90";
    OpenmlConnector client;
    OpenmlLocator locator;

    public OpenmlDatasetProvider() {

    }

    public OpenmlDatasetProvider init(OpenmlLocator _locator) {
        this.client = new OpenmlConnector(apiKey);
        this.locator = _locator;
        return this;
    }

    private String escape(String csv) {
        return csv.replace(".", "_").replace("\"", "");
    }

    public String load() {
        try {
            DataSetDescription data = this.client.dataGet(locator.getDatasetId());
            File csvFile = this.client.datasetGetCsv(data);
            byte[] encoded = Files.readAllBytes(Paths.get(csvFile.getAbsolutePath()));
            String csv =  new String(encoded, StandardCharsets.UTF_8);
            return escape(csv);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
