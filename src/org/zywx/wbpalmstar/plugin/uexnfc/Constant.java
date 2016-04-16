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

	// 得到NFC信息本地广播
	public static final String LOCAL_BROADCAST_ACTION_GET_NFC_INFO_SUCCESS = "org.zywx.wbpalmstar.plugin.uexnfc.GET_NFC_INFO_SUCCESS";// 得到NFC信息成功广播
	public static final String LOCAL_BROADCAST_ACTION_GET_NFC_INFO_FAIL = "org.zywx.wbpalmstar.plugin.uexnfc.GET_NFC_INFO_FAIL";// 得到NFC信息失败广播
	public static final String GET_NFC_INFO_INTENT_EXTRA_NAME = "data";// intent附加信息字段名

	// EUExNFC发送PendingIntent时向NFCActivity发送的数据
	public static final String KEY_NFC_CONFIGURATION = "jsonNfcConfiguration";// NFC配置项

	// 得到NFC信息JSON
	public static final String UID = "uid";// uid，十六进制id
	public static final String TECHNOLOGIES = "technologies";// 支持协议类型数组
	public static final String CURRENT_TECH = "currentTech";// 当前协议类型

	// MifareClassic协议JSON
	public static final String MIFARE_CLASSIC_TYPE = "mifareClassicType";// MifareClassic类型
	public static final String MIFARE_CLASSIC_SECTOR_COUNT = "mifareClassicSectorCount";// MifareClassic标签总共有的扇区数量
	public static final String MIFARE_CLASSIC_BLOCK_COUNT = "mifareClassicBlockCount";// MifareClassic标签总共有的的块数量
	public static final String MIFARE_CLASSIC_SIZE = "mifareClassicSize";// MifareClassic标签的容量
	public static final String MIFARE_CLASSIC_DETAIL_DATA = "mifareClassicDetailData";// MifareClassic标签的详细数据
}
