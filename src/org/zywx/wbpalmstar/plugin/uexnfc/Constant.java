package org.zywx.wbpalmstar.plugin.uexnfc;

/**
 * 常量类
 * 
 * @author waka
 *
 */
public class Constant {

	// 给前端的标志：成功返回1，失败返回0
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAIL = 0;

	// requestCode
	public static final int REQUEST_CODE_NFC_ACTIVITY = 1;// NFCActivity

	// 得到NFC信息本地广播
	public static final String LOCAL_BROADCAST_GET_NFC_INFO_ACTION = "org.zywx.wbpalmstar.plugin.uexnfc.NFC_CALLBACK";// NFC数据回调本地广播Action
	public static final String GET_NFC_INFO_INTENT_EXTRA_NAME = "data";// intent附加信息字段名
	// 得到NFC信息JSON
	public static final String GET_NFC_INFO_UID = "uid";// uid，十六进制
	public static final String GET_NFC_INFO_TECHNOLOGIES = "technologies";// 支持协议类型数组

}
