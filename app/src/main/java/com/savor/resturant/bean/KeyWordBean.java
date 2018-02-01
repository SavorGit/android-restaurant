package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by admin on 2018/2/1.
 */

public class KeyWordBean implements Serializable {
    private static final long serialVersionUID = -1;
    /**欢迎词*/
    private String keyWord;
    /**北京id*/
    private String templateId;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "KeyWordBean{" +
                "keyWord='" + keyWord + '\'' +
                ", templateId='" + templateId + '\'' +
                '}';
    }
}
