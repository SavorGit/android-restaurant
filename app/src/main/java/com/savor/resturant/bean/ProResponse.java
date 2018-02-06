package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by hezd on 2018/2/5.
 */

public class ProResponse implements Serializable {
    private String founded_count;

    @Override
    public String toString() {
        return "ProResponse{" +
                "founded_count='" + founded_count + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProResponse that = (ProResponse) o;

        return founded_count != null ? founded_count.equals(that.founded_count) : that.founded_count == null;
    }

    @Override
    public int hashCode() {
        return founded_count != null ? founded_count.hashCode() : 0;
    }

    public String getFounded_count() {
        return founded_count;
    }

    public void setFounded_count(String founded_count) {
        this.founded_count = founded_count;
    }
}
