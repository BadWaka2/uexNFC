package org.zywx.wbpalmstar.plugin.uexnfc;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.util.Log;

/**
 * 入口类
 * 
 * @author waka
 *
 */
public class EUExNFC extends EUExBase {

	private static final String TAG = "EUExNFC";

	// 回调
	private static final String CB_IS_NFC_SUPPORT = "uexNFC.cbIsNFCSupport";// 判断设备是否支持NFC回调
	private static final String CB_IS_NFC_OPEN = "uexNFC.cbIsNFCOpen";// 判断NFC是否开启回调
	private static final String CB_START_SCAN_NFC = "uexNFC.cbStartScanNFC";// 开始扫描NFC回调
	private static final String CB_GET_NFC_DATA = "uexNFC.cbGetNFCData";// 得到NFC数据回调

	/**
	 * 构造方法
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public EUExNFC(Context arg0, EBrowserView arg1) {
		super(arg0, arg1);

	}

	/**
	 * 判断设备是否支持NFC
	 * 
	 * @param param
	 */
	public void isNFCSupport(String[] param) {

		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
		if (nfcAdapter == null) {// 为空则不支持
			jsCallback(CB_IS_NFC_SUPPORT, 0, EUExCallback.F_C_TEXT, Constant.STATUS_FAIL);
			return;
		}
		jsCallback(CB_IS_NFC_SUPPORT, 0, EUExCallback.F_C_TEXT, Constant.STATUS_SUCCESS);
	}

	/**
	 * 判断NFC是否开启
	 * 
	 * @param param
	 */
	public void isNFCOpen(String[] param) {

		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
		if (nfcAdapter == null) {// 为空则不支持
			jsCallback(CB_IS_NFC_OPEN, 0, EUExCallback.F_C_TEXT, Constant.STATUS_FAIL);
			return;
		}
		if (!nfcAdapter.isEnabled()) {// isEnabled为false则未打开
			jsCallback(CB_IS_NFC_OPEN, 0, EUExCallback.F_C_TEXT, Constant.STATUS_FAIL);
			return;
		}
		jsCallback(CB_IS_NFC_OPEN, 0, EUExCallback.F_C_TEXT, Constant.STATUS_SUCCESS);
	}

	/**
	 * 开始扫描NFC
	 * 
	 * @param param
	 */
	public void startScanNFC(String[] param) {

		// 跳转到原生的透明的NFCActivity
		Intent intent = new Intent();
		intent.setClass(mContext, NFCActivity.class);
		startActivityForResult(intent, Constant.REQUEST_CODE_NFC_ACTIVITY);

		// 给前端回调
		jsCallback(CB_START_SCAN_NFC, 0, EUExCallback.F_C_TEXT, Constant.STATUS_SUCCESS);

	}

	@Override
	/**
	 * onActivityResult
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		// NFCActivity
		case Constant.REQUEST_CODE_NFC_ACTIVITY:

			// if OK
			if (resultCode == Activity.RESULT_OK) {

				// 获得NFC数据
				String nfcData = data.getStringExtra(Constant.GET_NFC_INFO_INTENT_EXTRA_NAME);

				// 回调给前端
				cbGetNFCData(nfcData);
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 返回NFC数据
	 * 
	 * @param nfcData
	 */
	public void cbGetNFCData(String nfcData) {

		jsCallback(CB_GET_NFC_DATA, 0, EUExCallback.F_C_TEXT, nfcData);
	}

	/**
	 * clean
	 */
	@Override
	protected boolean clean() {

		Log.i(TAG, "clean");

		return false;
	}

}
