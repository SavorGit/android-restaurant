package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/12/26.
 */

public class Customer implements Serializable {
    private static final long serialVersionUID = -1;
    private String name;
    private String mobile;
    private String mobile1;
    private String customer_id;
    private String sex;
    private String birthday;
    private String birthplace;
    private String face_url;
    private String consume_ability;
    private String consume_ability_id;
    private String remark;
    private List<CustomerLabel> label = new ArrayList<CustomerLabel>();

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

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
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

    public String getConsume_ability_id() {
        return consume_ability_id;
    }

    public void setConsume_ability_id(String consume_ability_id) {
        this.consume_ability_id = consume_ability_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<CustomerLabel> getLabel() {
        return label;
    }

    public void setLabel(List<CustomerLabel> label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mobile1='" + mobile1 + '\'' +
                ", customer_id='" + customer_id + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", birthplace='" + birthplace + '\'' +
                ", face_url='" + face_url + '\'' +
                ", consume_ability='" + consume_ability + '\'' +
                ", consume_ability_id='" + consume_ability_id + '\'' +
                ", remark='" + remark + '\'' +
                ", label=" + label +
                '}';
    }
}
