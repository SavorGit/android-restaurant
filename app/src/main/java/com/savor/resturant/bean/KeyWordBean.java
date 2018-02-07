package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * Created by admin on 2018/2/1.
 */

public class KeyWordBean implements Serializable {
    /**欢迎词*/
    private String keyWord;
    /**北京id*/
    private String templateId;
    private boolean isDefault = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyWordBean that = (KeyWordBean) o;

        if (isDefault != that.isDefault) return false;
        if (keyWord != null ? !keyWord.equals(that.keyWord) : that.keyWord != null) return false;
        return templateId != null ? templateId.equals(that.templateId) : that.templateId == null;
    }

    @Override
    public int hashCode() {
        int result = keyWord != null ? keyWord.hashCode() : 0;
        result = 31 * result + (templateId != null ? templateId.hashCode() : 0);
        result = 31 * result + (isDefault ? 1 : 0);
        return result;
    }

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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
