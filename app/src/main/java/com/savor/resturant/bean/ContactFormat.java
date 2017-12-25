
package com.savor.resturant.bean;

import java.io.Serializable;

/**
 * 联系人
 */
public  class ContactFormat implements Serializable {
    /**
     * name : 小三
     * mobile : 130110858187
     * mobile1 : 13123456547
     * sex : 1
     * birthday : 2010-09-10
     * birthplace : 北京市丰台区
     */
    private String contactId;
    private String name = "";
    private String face_url = "";

    private String key;
    private String mobile = "";
    private String mobile1 = "";
    private int sex;
    private String birthday;
    private String birthplace;
    /**是否被选中*/
    private boolean isSelected;
    /**是否已添加*/
    private boolean isAdded;

    @Override
    public String toString() {
        return "ContactFormat{" +
                "contactId='" + contactId + '\'' +
                ", name='" + name + '\'' +
                ", face_url='" + face_url + '\'' +
                ", key='" + key + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mobile1='" + mobile1 + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", birthplace='" + birthplace + '\'' +
                ", isSelected=" + isSelected +
                ", isAdded=" + isAdded +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactFormat that = (ContactFormat) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (face_url != null ? !face_url.equals(that.face_url) : that.face_url != null)
            return false;
        return mobile != null ? mobile.equals(that.mobile) : that.mobile == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (face_url != null ? face_url.hashCode() : 0);
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        return result;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }
}
