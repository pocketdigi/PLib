package com.pocketdigi.plib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算MD5
 * 
 * @author fhp
 * 
 */
public class MD5Utils {
	/**
	 * 计算
	 * @param source
	 * @return
	 */
	private static StringBuffer calculate(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(source.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 返回32位
	 * @param source
	 * @return
	 */
	public static String getMD532(String source)
	{
		StringBuffer buf=calculate(source);
		return buf.toString();
	}
	/**
	 * 返回16位
	 * @param source
	 * @return
	 */
	public static String getMD516(String source)
	{
		StringBuffer buf=calculate(source);
		return buf.toString().substring(8,24);
	}
}
