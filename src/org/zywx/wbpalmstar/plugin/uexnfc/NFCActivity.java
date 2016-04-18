package org.zywx.wbpalmstar.plugin.uexnfc;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
						JSONObject jsonObject = NFCManager.getInstance().getTagInfo(mJsonNfcConfiguration, tag);

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
