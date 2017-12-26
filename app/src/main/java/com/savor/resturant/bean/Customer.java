package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/12/26.
 */

public class Customer implements Serializable {
    private static final long serialVersionUID = -1;
    private String username;
    private String usermobile;
    private String usermobile1;
    private String sex;
    private String birthday;
    private String birthplace;
    private String face_url;
    private String consume_ability;
    private String remark;
    private List<Label> label = new ArrayList<Label>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    public String getUsermobile1() {
        return usermobile1;
    }

    public void setUsermobile1(String usermobile1) {
        this.usermobile1 = usermobile1;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
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

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    public String getConsume_ability() {
        return consume_ability;
    }

    public void setConsume_ability(String consume_ability) {
        this.consume_ability = consume_ability;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Label> getLabel() {
        return label;
    }

    public void setLabel(List<Label> label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username='" + username + '\'' +
                ", usermobile='" + usermobile + '\'' +
                ", usermobile1='" + usermobile1 + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", birthplace='" + birthplace + '\'' +
                ", face_url='" + face_url + '\'' +
                ", consume_ability='" + consume_ability + '\'' +
                ", remark='" + remark + '\'' +
                ", label=" + label +
                '}';
    }
}
