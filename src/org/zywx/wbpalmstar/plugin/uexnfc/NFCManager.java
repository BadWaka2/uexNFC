package org.zywx.wbpalmstar.plugin.uexnfc;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.plugin.uexnfc.bean.MifareClassicBean;
import org.zywx.wbpalmstar.plugin.uexnfc.bean.NFCBaseBean;
import org.zywx.wbpalmstar.plugin.uexnfc.mifareclassic.MifareClassicHelper;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.util.SparseArray;

/**
 * NFC管理器
 * 
 * 逻辑层
 * 
 * @author waka
 * @version createTime:2016年4月18日 下午1:40:00
 */
public class NFCManager {

	private static final String TAG = "NFCManager";

	/**
	 * 单例模式
	 */
	private static NFCManager instance = new NFCManager();

	/**
	 * NFC协议标识
	 */
	public static final int TECH_NFC_BASE = 0;// 基础类型
	public static final int TECH_ISO_DEP = 1;// IsoDep类型
	public static final int TECH_NFCA = 2;// NfcA类型
	public static final int TECH_NFCB = 3;// NfcB类型
	public static final int TECH_NFCF = 4;// NfcF类型
	public static final int TECH_NFCV = 5;// NfcV类型
	public static final int TECH_NDEF = 6;// Ndef类型
	public static final int TECH_NDEF_FORMATABLE = 7;// NdefFormatable类型
	public static final int TECH_MIFARE_CLASSIC = 8;// MifareClassic类型
	public static final int TECH_MIFARE_ULTRALIGHT = 9;// MifareUltralight类型

	private NFCManager() {

	}

	public static NFCManager getInstance() {
		return instance;
	}

	// TODO
	/**
	 * 得到Tag信息
	 * 
	 * 内部会进行耗时操作，建议放在子线程中执行
	 * 
	 * @param jsonNfcConfiguration
	 *            JSON格式的NFC配置项
	 * @param tag
	 *            NFC标签
	 * @return
	 */
	public JSONObject getTagInfo(JSONObject jsonNfcConfiguration, Tag tag) {

		/*
		 * 协议类型
		 */
		int tech = TECH_NFC_BASE;// 默认为基础类型

		/*
		 * 指令集
		 */
		JSONArray jsonArrayCmds = null;// 默认为null

		/** 解析NFC配置项 */
		if (jsonNfcConfiguration != null) {
			try {

				// 解析协议类型
				tech = Integer.valueOf(
						jsonNfcConfiguration.optString(Constant.JSON_FROM_FRONT_CONFIG_NFC_TECH, TECH_NFC_BASE + ""));
				// 解析指令集
				jsonArrayCmds = jsonNfcConfiguration.optJSONArray(Constant.JSON_FROM_FRONT_CONFIG_NFC_CMDS);

			} catch (NumberFormatException e) {
				e.printStackTrace();
				Log.e(TAG, "【getTagInfo】	NumberFormatException" + e.getMessage(), e);
			}
		}

		/** 根据tech进行不同处理 */

		// 得到基础信息
		NFCBaseBean baseBean = getBaseInfo(tag);
		JSONObject jsonBaseInfo = packageData(baseBean, TECH_NFC_BASE);

		/*
		 * 0.基础类型
		 */
		if (tech == TECH_NFC_BASE) {

			return jsonBaseInfo;
		}

		/*
		 * 1.IsoDep
		 */
		else if (tech == TECH_ISO_DEP) {

			// 如果该标签不支持IsoDep类型
			if (IsoDep.get(tag) == null) {

				Log.e(TAG, "【getTagInfo】	该标签不支持IsoDep类型");
				return jsonBaseInfo;
			}

			// 如果指令集为null
			if (jsonArrayCmds == null) {

				Log.e(TAG, "【getTagInfo】	指令集为null");
				return jsonBaseInfo;
			}

			IsoDep isoDep = IsoDep.get(tag);

			// 连接
			try {
				isoDep.connect();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "【getTagInfo】	isoDep.connect()" + e.getMessage(), e);
				return jsonBaseInfo;
			}

			String[] transceivedResponses = new String[jsonArrayCmds.length()];

			// 取出每一条指令，依次进行transceive操作
			for (int i = 0; i < jsonArrayCmds.length(); i++) {

				try {

					// 获得指令的十六进制字符串
					String cmdStr = jsonArrayCmds.getString(i);

					// 根据十六进制字符串转换得到byte数组
					byte[] cmd = Util.hexStringToByteArraySplitByComma(cmdStr);

					byte[] transceivedResponse = isoDep.transceive(cmd);

					String transceivedResponseStr = Util.byteArrayToHexStringSplitByComma(transceivedResponse);

					transceivedResponses[i] = transceivedResponseStr;

				} catch (JSONException e) {

					e.printStackTrace();
					transceivedResponses[i] = "";

				} catch (IOException e) {

					e.printStackTrace();
					transceivedResponses[i] = "";

				}
			}

			baseBean.setTransceivedResponses(transceivedResponses);

			return packageData(baseBean, TECH_ISO_DEP);
		}

		/*
		 * 2.NfcA
		 */
		else if (tech == TECH_NFCA) {

		}

		/*
		 * 3.NfcB
		 */
		else if (tech == TECH_NFCB) {

		}

		/*
		 * 4.NfcF
		 */
		else if (tech == TECH_NFCF) {

		}

		/*
		 * 5.NfcV
		 */
		else if (tech == TECH_NFCV) {

		}

		/*
		 * 6.Ndef
		 */
		else if (tech == TECH_NDEF) {

		}

		/*
		 * 7.NdefFormatable
		 */
		else if (tech == TECH_NDEF_FORMATABLE) {

		}

		/*
		 * 8.MifareClassic
		 */
		else if (tech == TECH_MIFARE_CLASSIC) {

			// 如果该标签不支持MifareClassic类型
			if (MifareClassic.get(tag) == null) {
				return jsonBaseInfo;
			}

			Log.i(TAG, "【getTagInfo】	类型 : MifareClassic");

			MifareClassicHelper mifareClassicHelper = new MifareClassicHelper(tag);

			// 读取数据
			MifareClassicBean mcBean = mifareClassicHelper.read();

			if (mcBean != null) {

				// 传入基本数据
				mcBean.setBaseBean(baseBean);

				// 封装数据进一个JSON中并返回
				return packageData(mcBean, TECH_MIFARE_CLASSIC);
			}

		}

		/*
		 * 9.MifareUltralight
		 */
		else if (tech == TECH_NFCA) {

		}

		return jsonBaseInfo;
	}

	/**
	 * 得到基础信息
	 * 
	 * @param tag
	 * @return
	 */
	private NFCBaseBean getBaseInfo(Tag tag) {

		NFCBaseBean baseBean = new NFCBaseBean();

		// 原始字节数组id
		byte[] tagId = tag.getId();

		// 十六进制id
		String tagIdHex = Util.byte2HexString(tagId);
		Log.i(TAG, "【getBaseInfo】	tagIdHex = " + tagIdHex);

		// 支持协议类型
		StringBuffer sb = new StringBuffer();
		String prefix = "android.nfc.tech.";// 前缀
		for (String tech : tag.getTechList()) {
			sb.append(tech.substring(prefix.length()));// 去掉前缀
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length());// 删除多余的逗号
		String technologies = sb.toString();
		Log.i(TAG, "【getBaseInfo】	technologies = " + technologies);

		baseBean.setTagId(tagId);
		baseBean.setTagIdHex(tagIdHex);
		baseBean.setTechnologies(technologies);

		return baseBean;
	}

	/**
	 * 根据协议封装数据成一个JSON
	 * 
	 * @param baseBean_基础Bean类
	 * @param tech_协议标识
	 * @return
	 */
	private JSONObject packageData(NFCBaseBean baseBean, int tech) {

		// 封装基础信息进一个JSON中
		JSONObject jsonObject = new JSONObject();
		try {

			// uid
			jsonObject.put(Constant.UID, baseBean.getTagIdHex());

			// 支持协议类型
			jsonObject.put(Constant.TECHNOLOGIES, baseBean.getTechnologies());

			// 指令集transceive后的返回值
			String[] transceivedResponses = baseBean.getTransceivedResponses();
			if (transceivedResponses != null) {
				JSONArray jsonArray1 = new JSONArray();
				for (int i = 0; i < transceivedResponses.length; i++) {
					jsonArray1.put(transceivedResponses[i]);
				}
				jsonObject.put(Constant.JSON_TO_FRONT_TRANSCEIVED_RESPONSES, jsonArray1);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "【packageData】	封装基础信息进一个JSON中" + e.getMessage(), e);
		}

		/*
		 * IsoDep
		 */
		if (tech == TECH_ISO_DEP) {

		}

		/*
		 * MifareClassic
		 */
		else if (tech == TECH_MIFARE_CLASSIC) {

			MifareClassicBean mcBean = (MifareClassicBean) baseBean;

			try {

				jsonObject.put(Constant.CURRENT_TECH, MifareClassicBean.TECH_NAME);
				jsonObject.put(Constant.MIFARE_CLASSIC_TYPE, mcBean.getType());
				jsonObject.put(Constant.MIFARE_CLASSIC_SECTOR_COUNT, mcBean.getSectorCount());
				jsonObject.put(Constant.MIFARE_CLASSIC_BLOCK_COUNT, mcBean.getBlockCount());
				jsonObject.put(Constant.MIFARE_CLASSIC_SIZE, mcBean.getSize());

				JSONArray jsonArray8 = new JSONArray();
				SparseArray<String[]> detailData = mcBean.getDetailData();
				for (int i = 0; i < detailData.size(); i++) {

					int key = detailData.keyAt(i);
					String[] strings = detailData.get(key);

					JSONArray jsonArray2 = new JSONArray();
					for (String string : strings) {

						jsonArray2.put(string);
					}
					jsonArray8.put(jsonArray2);
				}
				jsonObject.put(Constant.MIFARE_CLASSIC_DETAIL_DATA, jsonArray8.toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Log.i(TAG, "【packageData】	最终返回jsonObject = " + jsonObject.toString());
		return jsonObject;
	}
}
