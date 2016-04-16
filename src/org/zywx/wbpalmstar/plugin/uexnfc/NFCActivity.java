package org.zywx.wbpalmstar.plugin.uexnfc;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexnfc.mifareclassic.MifareClassicBean;
import org.zywx.wbpalmstar.plugin.uexnfc.mifareclassic.MifareClassicHelper;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

/**
 * NFCActivity
 * 
 * @author waka
 *
 */
public class NFCActivity extends Activity {

	private static final String TAG = "NFCActivity";

	// NFC配置项
	private JSONObject mJsonNfcConfiguration;

	// 协议标识
	private static final int TECH_NFC_BASE = 0;// 基础类型
	private static final int TECH_ISO_DEP = 1;// IsoDep类型
	private static final int TECH_NFCA = 2;// NfcA类型
	private static final int TECH_NFCB = 3;// NfcB类型
	private static final int TECH_NFCF = 4;// NfcF类型
	private static final int TECH_NFCV = 5;// NfcV类型
	private static final int TECH_NDEF = 6;// Ndef类型
	private static final int TECH_NDEF_FORMATABLE = 7;// NdefFormatable类型
	private static final int TECH_MIFARE_CLASSIC = 8;// MifareClassic类型
	private static final int TECH_MIFARE_ULTRALIGHT = 9;// MifareUltralight类型

	// Handler
	private MyHandler mHandler = new MyHandler(this);

	/**
	 * 静态Handler内部类，避免内存泄漏
	 * 
	 * @author waka
	 *
	 */
	private static class MyHandler extends Handler {

		// 对Handler持有的对象使用弱引用
		private WeakReference<NFCActivity> wrNFCActivity;

		public MyHandler(NFCActivity nfcActivity) {
			wrNFCActivity = new WeakReference<NFCActivity>(nfcActivity);
		}

		public void handleMessage(Message msg) {

			// 获得数据
			JSONObject jsonObject = (JSONObject) msg.obj;

			// 发送广播并关闭当前 Activity
			wrNFCActivity.get().sendBroadcastAndFinish(jsonObject);
		}
	}

	@Override
	/**
	 * onCreate
	 */
	protected void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "【onCreate】");

		super.onCreate(savedInstanceState);
		setContentView(EUExUtil.getResLayoutID("plugin_uexnfc_activity_nfc"));

		// 解析Intent
		resolveIntent(getIntent());

	}

	@Override
	/**
	 * onDestroy
	 */
	protected void onDestroy() {

		Log.i(TAG, "【onDestroy】");

		super.onDestroy();

		// 移除消息队列中所有消息和所有的Runnable，避免内存泄漏
		mHandler.removeCallbacksAndMessages(null);

	}

	@Override
	/**
	 * onNewIntent
	 * 
	 * 实现onNewIntent回调方法来处理扫描到的NFC标签的数据
	 */
	protected void onNewIntent(Intent intent) {

		Log.i(TAG, "【onNewIntent】");

		super.onNewIntent(intent);

		// 解析Intent
		resolveIntent(intent);
	}

	/**
	 * 解析Intent
	 * 
	 * @param intent
	 */
	private void resolveIntent(Intent intent) {

		// 解析Intent的Action
		String action = intent.getAction();

		// 获得NFC配置项
		String jsonStrNfcConfiguration = intent.getStringExtra(Constant.KEY_NFC_CONFIGURATION);
		if (jsonStrNfcConfiguration != null && !jsonStrNfcConfiguration.isEmpty()) {
			try {

				mJsonNfcConfiguration = new JSONObject(jsonStrNfcConfiguration);
				Log.i(TAG, "【resolveIntent】	mJsonNfcConfiguration" + mJsonNfcConfiguration.toString());

			} catch (JSONException e) {

				e.printStackTrace();
				mJsonNfcConfiguration = null;
				Log.e(TAG, "【resolveIntent】	JSONException" + e.getMessage(), e);

			}
		}

		// 如果Action==null，直接return
		if (action == null) {

			Log.e(TAG, "【resolveIntent】	action == null");
			return;
		}

		// 如果是ACTION_TAG_DISCOVERED
		else if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {

			// 得到Tag
			final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			// 得到Tag信息，因为数据量较大，须在子线程中执行
			new Thread(new Runnable() {

				@Override
				public void run() {

					synchronized (this) {

						// 得到Tag信息
						JSONObject jsonObject = getTagInfo(tag);

						// 向Handler发消息，通知主线程发广播
						Message message = Message.obtain();
						message.obj = jsonObject;// 把得到的数据放在message里
						mHandler.sendMessage(message);
					}

				}
			}).start();

		}

	}

	/**
	 * 得到Tag信息
	 * 
	 * 内部会进行耗时操作，建议放在子线程中执行
	 * 
	 * @param tag
	 */
	private JSONObject getTagInfo(Tag tag) {

		// 得到基础信息
		NFCBaseBean baseBean = getBaseInfo(tag);
		JSONObject jsonBaseInfo = packageData(baseBean, TECH_NFC_BASE);

		int tech = TECH_NFC_BASE;
		try {
			tech = Integer.valueOf(mJsonNfcConfiguration.getString("tech"));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "【getTagInfo】	JSONException" + e.getMessage(), e);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Log.e(TAG, "【getTagInfo】	NumberFormatException" + e.getMessage(), e);
		} catch (NullPointerException e) {
			e.printStackTrace();
			Log.e(TAG, "【getTagInfo】	NullPointerException" + e.getMessage(), e);
		}

		// 1 IsoDep
		if (tech == TECH_ISO_DEP) {

			// 如果该标签支持IsoDep类型
			if (IsoDep.get(tag) != null) {

				IsoDep isoDep = IsoDep.get(tag);

				try {

					isoDep.connect();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		else if (tech == TECH_NFCA) {

			// 如果该标签支持NfcA类型
			if (NfcA.get(tag) != null) {

			}
		}

		else if (tech == TECH_MIFARE_CLASSIC) {

			// 如果该标签支持MifareClassic类型
			if (MifareClassic.get(tag) != null) {

				Log.i(TAG, "【getTagInfo】	类型 : MifareClassic");

				MifareClassicHelper mifareClassicHelper = new MifareClassicHelper(tag);

				// 读取数据
				MifareClassicBean mcBean = mifareClassicHelper.read();

				if (mcBean != null) {

					// 传入基本数据
					mcBean.setBaseBean(baseBean);

					// 封装数据进一个JSON中
					JSONObject jsonObject = packageData(mcBean, TECH_MIFARE_CLASSIC);
					return jsonObject;
				}
			}
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

			jsonObject.put(Constant.UID, baseBean.getTagIdHex());
			jsonObject.put(Constant.TECHNOLOGIES, baseBean.getTechnologies());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		switch (tech) {

		// MifareClassic
		case TECH_MIFARE_CLASSIC:

			MifareClassicBean mcBean = (MifareClassicBean) baseBean;

			try {

				jsonObject.put(Constant.CURRENT_TECH, MifareClassicBean.TECH_NAME);
				jsonObject.put(Constant.MIFARE_CLASSIC_TYPE, mcBean.getType());
				jsonObject.put(Constant.MIFARE_CLASSIC_SECTOR_COUNT, mcBean.getSectorCount());
				jsonObject.put(Constant.MIFARE_CLASSIC_BLOCK_COUNT, mcBean.getBlockCount());
				jsonObject.put(Constant.MIFARE_CLASSIC_SIZE, mcBean.getSize());

				JSONArray jsonArray = new JSONArray();
				SparseArray<String[]> detailData = mcBean.getDetailData();
				for (int i = 0; i < detailData.size(); i++) {

					int key = detailData.keyAt(i);
					String[] strings = detailData.get(key);

					JSONArray jsonArray2 = new JSONArray();
					for (String string : strings) {

						jsonArray2.put(string);
					}
					jsonArray.put(jsonArray2);
				}
				jsonObject.put(Constant.MIFARE_CLASSIC_DETAIL_DATA, jsonArray.toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		default:
			break;
		}

		return jsonObject;
	}

	/**
	 * 发送本地广播，然后finish
	 * 
	 * @param jsonObject
	 */
	private void sendBroadcastAndFinish(JSONObject jsonObject) {

		// 发送本地广播
		Intent intent = new Intent(Constant.LOCAL_BROADCAST_ACTION_GET_NFC_INFO_SUCCESS);
		intent.putExtra(Constant.GET_NFC_INFO_INTENT_EXTRA_NAME, jsonObject.toString());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		// 关闭当前Activity
		finish();
	}

}
