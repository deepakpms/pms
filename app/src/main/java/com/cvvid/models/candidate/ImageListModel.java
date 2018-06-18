package com.cvvid.models.candidate;

import java.io.Serializable;

public class ImageListModel  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String url;

    public ImageListModel() {
    }

    public ImageListModel(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
