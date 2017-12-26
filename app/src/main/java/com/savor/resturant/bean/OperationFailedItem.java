package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 操作失败集合
 * Created by hezd on 2017/12/25.
 */

public class OperationFailedItem implements Serializable {
    /**
     * 当前操作类型，导入通讯录，新增客户，修改客户
     */
    public enum OpType implements Serializable{
        /**首次导入通讯录*/
        TYPE_IMPORT_FIRST,
        /**新增客户*/
        TYPE_ADD_CUSTOMER,
        /**修改客户*/
        TYPE_EDIT_CUSTOMER,
        /**新增导入通讯录*/
        TYPE_IMPORT_NEW,
    }
    /**
     * 当前操作类型，导入通讯录，新增客户，修改客户
     */
    private OpType type;
    private List<ContactFormat> contactFormat;

    @Override
    public String toString() {
        return "OperationFailedItem{" +
                "type=" + type +
                ", contactFormat=" + contactFormat +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationFailedItem item = (OperationFailedItem) o;

        if (type != item.type) return false;
        return contactFormat != null ? contactFormat.equals(item.contactFormat) : item.contactFormat == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (contactFormat != null ? contactFormat.hashCode() : 0);
        return result;
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

    public List<ContactFormat> getContactFormat() {
        return contactFormat;
    }

    public void setContactFormat(List<ContactFormat> contactFormat) {
        this.contactFormat = contactFormat;
    }
}
