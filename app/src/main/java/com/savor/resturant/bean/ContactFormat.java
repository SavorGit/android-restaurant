
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

    private String name;

    private String key;
    private String mobile;
    private String mobile1;
    private int sex;
    private String birthday;
    private String birthplace;

    @Override
    public String toString() {
        return "ContactFormat{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mobile1='" + mobile1 + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", birthplace='" + birthplace + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactFormat contactFormat = (ContactFormat) o;

        if (name != null ? !name.equals(contactFormat.name) : contactFormat.name != null) return false;
        if (mobile != null ? !mobile.equals(contactFormat.mobile) : contactFormat.mobile != null) return false;
        return mobile1 != null ? mobile1.equals(contactFormat.mobile1) : contactFormat.mobile1 == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (mobile1 != null ? mobile1.hashCode() : 0);
        return result;
    }

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
