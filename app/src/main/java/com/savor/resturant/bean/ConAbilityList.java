package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 *
 *消费能力列表
 * Created by hezd on 2017/12/26.
 */

public class ConAbilityList implements Serializable {

    private List<ConAbility> list;

    @Override
    public String toString() {
        return "ConAbilityList{" +
                "list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConAbilityList that = (ConAbilityList) o;

        return list != null ? list.equals(that.list) : that.list == null;
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }

    public List<ConAbility> getList() {
        return list;
    }

    public void setList(List<ConAbility> list) {
        this.list = list;
    }

    public class ConAbility implements Serializable{

        /**
         * id : 1
         * name : 100及以下
         */

        private int id;
        private String name;

        @Override
        public String toString() {
            return "ConAbility{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConAbility that = (ConAbility) o;

            if (id != that.id) return false;
            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
