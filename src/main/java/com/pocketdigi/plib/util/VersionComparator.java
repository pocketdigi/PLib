package com.pocketdigi.plib.util;

import java.util.Comparator;

/**
 * 版本号大小比较
 * Created by fhp on 15/8/17.
 */
public class VersionComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        if (s1 == null && s2 == null)
            return 0;
        else if (s1 == null)
            return -1;
        else if (s2 == null)
            return 1;

        String[]
                arr1 = s1.split("[^a-zA-Z0-9]+"),
                arr2 = s2.split("[^a-zA-Z0-9]+");
        int i1, i2, i3;

        for (int ii = 0, max = Math.min(arr1.length, arr2.length);
             ii <= max; ii++) {
            if (ii == arr1.length)
                return ii == arr2.length ? 0 : -1;
            else if (ii == arr2.length)
                return 1;

            try {
                i1 = Integer.parseInt(arr1[ii]);
            } catch (Exception x) {
                i1 = Integer.MAX_VALUE;
            }

            try {
                i2 = Integer.parseInt(arr2[ii]);
            } catch (Exception x) {
                i2 = Integer.MAX_VALUE;
            }

            if (i1 != i2) {
                return i1 - i2;
            }

            i3 = arr1[ii].compareTo(arr2[ii]);

            if (i3 != 0)
                return i3;
        }

        return 0;
    }

    /**
     * 判断版本1是否大于版本2
     * @param v1 版本1
     * @param v2 版本2
     * @return
     */
    public static boolean isV1GTV2(String v1, String v2){
        VersionComparator versionComparator=new VersionComparator();
        return versionComparator.compare(v1,v2)>0;
    }
}
