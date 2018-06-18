package com.cvvid.models.employer;

import java.io.Serializable;

public class ViewLikesModel implements Serializable {

    private static final long serialVersionUID = 1L;

    String slug;
    String photoid;
    String photo;
    String id;

    public ViewLikesModel() {
    }

    public ViewLikesModel(String id, String slug, String photoid, String photo) {
        this.id = id;
        this.slug = slug;
        this.photoid = photoid;
        this.photo = photo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
