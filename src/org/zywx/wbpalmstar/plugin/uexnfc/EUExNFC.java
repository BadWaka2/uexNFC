package org.zywx.wbpalmstar.plugin.uexnfc;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;

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
	 * 打开NFCActivity
	 * 
	 * @param param
	 */
	public void startNFCActivity(String[] param) {
		Intent intent = new Intent();
		intent.setClass(mContext, NFCActivity.class);
		startActivityForResult(intent, Constant.REQUEST_CODE_NFC_ACTIVITY);
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

			}

			break;

		default:
			break;
		}
	}

	/**
	 * clean
	 */
	@Override
	protected boolean clean() {
		return false;
	}

}
