package org.zywx.wbpalmstar.plugin.uexnfc;

public class Util {

	/**
	 * 将字节数组转换成一串十六进制值
	 * 
	 * Convert an array of bytes into a string of hex values.
	 * 
	 * @param bytes
	 *            Bytes to convert.
	 * @return The bytes in hex string format.
	 */
	public static String byte2HexString(byte[] bytes) {
		String ret = "";
		if (bytes != null) {
			for (Byte b : bytes) {
				ret += String.format("%02X", b.intValue() & 0xFF);
			}
		}
		return ret;
	}
}
