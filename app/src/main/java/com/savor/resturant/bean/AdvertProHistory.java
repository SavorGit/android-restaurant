package com.savor.resturant.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hezd on 2017/12/12.
 */

public class AdvertProHistory implements Serializable {
    private HotelBean hotelBean;
    private List<RecommendFoodAdvert> advertList;

    @Override
    public String toString() {
        return "AdvertProHistory{" +
                "hotelBean=" + hotelBean +
                ", advertList=" + advertList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdvertProHistory that = (AdvertProHistory) o;

        if (hotelBean != null ? !hotelBean.equals(that.hotelBean) : that.hotelBean != null)
            return false;
        return advertList != null ? advertList.equals(that.advertList) : that.advertList == null;
    }

    @Override
    public int hashCode() {
        int result = hotelBean != null ? hotelBean.hashCode() : 0;
        result = 31 * result + (advertList != null ? advertList.hashCode() : 0);
        return result;
    }

    public HotelBean getHotelBean() {
        return hotelBean;
    }

    public void setHotelBean(HotelBean hotelBean) {
        this.hotelBean = hotelBean;
    }

    public List<RecommendFoodAdvert> getAdvertList() {
        return advertList;
    }

    public void setAdvertList(List<RecommendFoodAdvert> advertList) {
        this.advertList = advertList;
    }
}
