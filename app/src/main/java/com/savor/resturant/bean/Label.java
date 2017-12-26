package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by bushlee on 2017/12/26.
 */

public class Label implements Serializable {
    private static final long serialVersionUID = -1;
    private String label_id;
    private String label_name;

    public String getLabel_id() {
        return label_id;
    }

    public void setLabel_id(String label_id) {
        this.label_id = label_id;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    @Override
    public String toString() {
        return "Label{" +
                "label_id='" + label_id + '\'' +
                ", label_name='" + label_name + '\'' +
                '}';
    }
}
