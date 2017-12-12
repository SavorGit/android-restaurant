package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 推荐菜历史缓存
 * Created by hezd on 2017/12/12.
 */

public class RecommendProHistory implements Serializable {
    private HotelBean hotelBean;
    private List<RecommendFoodAdvert> recmmendList;

    @Override
    public String toString() {
        return "RecommendProHistory{" +
                "hotelBean=" + hotelBean +
                ", recmmendList=" + recmmendList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecommendProHistory that = (RecommendProHistory) o;

        if (hotelBean != null ? !hotelBean.equals(that.hotelBean) : that.hotelBean != null)
            return false;
        return recmmendList != null ? recmmendList.equals(that.recmmendList) : that.recmmendList == null;
    }

    @Override
    public int hashCode() {
        int result = hotelBean != null ? hotelBean.hashCode() : 0;
        result = 31 * result + (recmmendList != null ? recmmendList.hashCode() : 0);
        return result;
    }

    public HotelBean getHotelBean() {
        return hotelBean;
    }

    public void setHotelBean(HotelBean hotelBean) {
        this.hotelBean = hotelBean;
    }

    public List<RecommendFoodAdvert> getRecmmendList() {
        return recmmendList;
    }

    public void setRecmmendList(List<RecommendFoodAdvert> recmmendList) {
        this.recmmendList = recmmendList;
    }
}
