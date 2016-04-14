package org.zywx.wbpalmstar.plugin.uexnfc;

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

	// 协议标识
	private static final int TECH_NFC_BASE = 0;// 基础类型
	private static final int TECH_MIFARE_CLASSIC = 1;// MifareClassic类型

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

		// 如果Action==null，直接return
		if (action == null) {

			Log.i(TAG, "action == null");
			return;
		}

		// 如果是ACTION_NDEF_DISCOVERED
		if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {

		}

		// 如果是ACTION_TECH_DISCOVERED
		else if (action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {

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

		/*
		 * 如果该标签支持MifareClassic类型
		 */
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

		/*
		 * 如果该标签支持IsoDep类型
		 */
		else if (IsoDep.get(tag) != null) {

		}

		else if (NfcA.get(tag) != null) {

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
		Log.i(TAG, "【得到Tag信息】	tagIdHex = " + tagIdHex);

		// 支持协议类型
		StringBuffer sb = new StringBuffer();
		String prefix = "android.nfc.tech.";// 前缀
		for (String tech : tag.getTechList()) {
			sb.append(tech.substring(prefix.length()));// 去掉前缀
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length());// 删除多余的逗号
		String technologies = sb.toString();
		Log.i(TAG, "【得到Tag信息】	technologies = " + technologies);

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
