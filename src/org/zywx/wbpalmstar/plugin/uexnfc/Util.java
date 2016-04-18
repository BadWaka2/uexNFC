package org.zywx.wbpalmstar.plugin.uexnfc;

import java.nio.ByteBuffer;

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

	/**
	 * byte数组转十六进制字符串,用逗号区分
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String byteArrayToHexStringSplitByComma(byte[] byteArray) {

		StringBuffer sb = new StringBuffer();

		for (Byte b : byteArray) {

			sb.append(String.format("%02X", b.intValue() & 0xFF));
			sb.append(",");// 用逗号区分
		}
		sb.delete(sb.length() - 1, sb.length());// 删除多余的逗号

		return sb.toString();
	}

	/**
	 * 十六进制字符串字符串转byte数组,用逗号区分
	 * 
	 * @param string
	 * @return
	 */
	public static byte[] hexStringToByteArraySplitByComma(String string) {

		String[] strings = string.split(",");// 用逗号区分

		ByteBuffer buff = ByteBuffer.allocate(strings.length);

		for (int i = 0; i < strings.length; i++) {

			int x = Integer.valueOf(strings[i], 16);
			buff.put((byte) x);
		}

		return buff.array();
	}
}
