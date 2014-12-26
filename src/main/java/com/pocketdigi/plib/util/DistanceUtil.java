package com.pocketdigi.plib.util;

/**
 * 计算任意两点间距离(经纬度)
 * Created by fhp on 14/12/25.
 */
public class DistanceUtil {

    private final static double EARTH_RADIUS = 6378.137;//地球半径
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    /**
     * 两点间距离
     * @param lat1 A点的纬度
     * @param lng1 A点的经度
     * @param lat2 B点的纬度
     * @param lng2 B点的经度
     * @return 距离,米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS*1000;
        return s;
    }
}
