package com.savor.resturant.core;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/21.
 */

public class CustomerBean implements Serializable {
    private static final long serialVersionUID = -1;
    private String name;
    private String mobile;
    private String face_url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    @Override
    public String toString() {
        return "CustomerBean{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", face_url='" + face_url + '\'' +
                '}';
    }
}
