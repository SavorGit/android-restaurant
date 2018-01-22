package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bushlee on 2017/12/26.
 */

public class    Customer implements Serializable {
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
    private String bill_info;
    private List<CustomerLabel> label = new ArrayList<CustomerLabel>();

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
                ", bill_info='" + bill_info + '\'' +
                ", label=" + label +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (name != null ? !name.equals(customer.name) : customer.name != null) return false;
        if (mobile != null ? !mobile.equals(customer.mobile) : customer.mobile != null)
            return false;
        if (mobile1 != null ? !mobile1.equals(customer.mobile1) : customer.mobile1 != null)
            return false;
        if (customer_id != null ? !customer_id.equals(customer.customer_id) : customer.customer_id != null)
            return false;
        if (sex != null ? !sex.equals(customer.sex) : customer.sex != null) return false;
        if (birthday != null ? !birthday.equals(customer.birthday) : customer.birthday != null)
            return false;
        if (birthplace != null ? !birthplace.equals(customer.birthplace) : customer.birthplace != null)
            return false;
        if (face_url != null ? !face_url.equals(customer.face_url) : customer.face_url != null)
            return false;
        if (consume_ability != null ? !consume_ability.equals(customer.consume_ability) : customer.consume_ability != null)
            return false;
        if (consume_ability_id != null ? !consume_ability_id.equals(customer.consume_ability_id) : customer.consume_ability_id != null)
            return false;
        if (remark != null ? !remark.equals(customer.remark) : customer.remark != null)
            return false;
        if (bill_info != null ? !bill_info.equals(customer.bill_info) : customer.bill_info != null)
            return false;
        return label != null ? label.equals(customer.label) : customer.label == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (mobile != null ? mobile.hashCode() : 0);
        result = 31 * result + (mobile1 != null ? mobile1.hashCode() : 0);
        result = 31 * result + (customer_id != null ? customer_id.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (birthplace != null ? birthplace.hashCode() : 0);
        result = 31 * result + (face_url != null ? face_url.hashCode() : 0);
        result = 31 * result + (consume_ability != null ? consume_ability.hashCode() : 0);
        result = 31 * result + (consume_ability_id != null ? consume_ability_id.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (bill_info != null ? bill_info.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
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

    public String getBill_info() {
        return bill_info;
    }

    public void setBill_info(String bill_info) {
        this.bill_info = bill_info;
    }

    public List<CustomerLabel> getLabel() {
        return label;
    }

    public void setLabel(List<CustomerLabel> label) {
        this.label = label;
    }
}
