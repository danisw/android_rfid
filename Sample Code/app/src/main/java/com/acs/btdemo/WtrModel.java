package com.acs.btdemo;

import com.google.gson.annotations.SerializedName;

public class WtrModel {
    @SerializedName("no_WTR")
    private String title;
    @SerializedName("waktu")
    private String tanggal_wtr;

    public WtrModel(String title, String tanggal_wtr) {
        this.title = title;
        this.tanggal_wtr=tanggal_wtr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTanggal_wtr() {
        return tanggal_wtr;
    }

    public void setTanggal_wtr(String tanggal_wtr) {
        this.tanggal_wtr = tanggal_wtr;
    }
}
