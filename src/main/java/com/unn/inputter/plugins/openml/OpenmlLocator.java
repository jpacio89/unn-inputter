package com.unn.inputter.plugins.openml;

// import com.unn.common.dataset.DatasetLocator;

public class OpenmlLocator /*extends DatasetLocator*/ {
    int datasetId;

    public OpenmlLocator() {}

    public OpenmlLocator(int datasetId) {
        this.datasetId = datasetId;
    }

    public OpenmlLocator(String datasetId) {
        this.datasetId = Integer.parseInt(datasetId);
    }

    public int getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }
}
