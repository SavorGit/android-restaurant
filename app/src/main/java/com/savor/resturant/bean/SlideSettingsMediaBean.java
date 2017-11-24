package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 幻灯片配置返回的实体类
 * Created by Administrator on 2017/6/14.
 */

public class SlideSettingsMediaBean implements Serializable{

    private String name;
    private int exist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }
}
