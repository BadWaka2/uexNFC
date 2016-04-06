package org.zywx.wbpalmstar.plugin.uexnfc;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexnfc.card.CardManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * NFCActivity
 * 
 * @author waka
 *
 */
public class NFCActivity extends Activity implements OnClickListener {

	private static final String TAG = "NFCActivity";

	private TextView tvBaseInfo, tvContent;
	private Button btnClearBaseInfo, btnClearContent;

	// NFC相关
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mTechListsArray;

	// 本地广播接收器
	private NFCActivityLocalReceiver mLocalReceiver;
	private IntentFilter mIntentFilter;

	// 通用数据类型
	private byte[] mTagId;// 原始字符数组ID
	private String mTagIdHex;// 十六进制ID
	private String mTechnologies;// 所支持的协议类型

	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "【onCreate】");

		super.onCreate(savedInstanceState);
		setContentView(EUExUtil.getResLayoutID("plugin_uexnfc_activity_nfc"));

		// initView
		tvBaseInfo = (TextView) findViewById(EUExUtil.getResIdID("plugin_uexnfc_tv_base_info"));
		tvContent = (TextView) findViewById(EUExUtil.getResIdID("plugin_uexnfc_tv_content"));
		btnClearBaseInfo = (Button) findViewById(EUExUtil.getResIdID("plugin_uexnfc_btn_clear_base_info"));
		btnClearContent = (Button) findViewById(EUExUtil.getResIdID("plugin_uexnfc_btn_clear_content"));

		// initEvent
		btnClearBaseInfo.setOnClickListener(this);
		btnClearContent.setOnClickListener(this);

		// 初始化本地广播
		mLocalReceiver = new NFCActivityLocalReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Constant.LOCAL_BROADCAST_ACTION_GET_NFC_INFO_FAIL);
		// 注册本地广播接收器
		LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, mIntentFilter);

		// 初始化NFC相关变量
		initNFC();

		// 解析Intent
		resolveIntent(getIntent());

	}

	/**
	 * 初始化NFC相关变量
	 * 
	 * @formatter:off 格式化关闭
	 */
	private void initNFC() {

		// 初始化NfcAdapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// A.创建一个PendingIntent对象，以便Android系统能够在扫描到NFC标签时，用它来封装NFC标签的详细信息
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		/*
		 * B.声明你想要截获处理的Intent对象的Intent过滤器。
		 * 前台调度系统会在设备扫描到NFC标签时，用声明的Intent过滤器来检查接收到的Intent对象。
		 * 如果匹配就会让你的应用程序来处理这个Intent对象， 如果不匹配，前台调度系统会回退到Intent调度系统。
		 * 如果Intent过滤器和技术过滤器的数组指定了null，
		 * 那么就说明你要过滤所有的退回到TAG_DISCOVERED类型的Intent对象的标签。
		 * 以下代码会用于处理所有的NDEF_DISCOVERED的MIME类型。只有在需要的时候才做这种处理
		 */
		try {
			mIntentFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*"),
					new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, "*/*"), };
		} catch (IntentFilter.MalformedMimeTypeException e) {// 须捕捉MalformedMimeTypeException异常
			e.printStackTrace();
		}

		// C.建立一个应用程序希望处理的NFC标签技术的数组。调用Object.class.getName()方法来获取你想要支持的技术的类：
		mTechListsArray = new String[][] { { IsoDep.class.getName() }, { NfcV.class.getName() },
				{ NfcF.class.getName() }, { Ndef.class.getName() }, };

	}

	@Override
	/**
	 * onResume
	 * 
	 * @formatter:on 格式化开启
	 */
	protected void onResume() {

		Log.i(TAG, "【onResume】");

		super.onResume();

		if (mNfcAdapter != null && mPendingIntent != null && mIntentFilters != null && mTechListsArray != null) {

			// 启用前台调度
			// 须在主线程中被调用，并且只有在该Activity在前台时（要保证在onResume()方法中调用这个方法）
			// mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
			// mIntentFilters, mTechListsArray);
			// mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
			// null);// 后两个参数传null，表示不显示应用程序选择下拉菜单
		}
	}

	@Override
	/**
	 * onPause
	 */
	protected void onPause() {

		Log.i(TAG, "【onPause】");

		super.onPause();

		if (mNfcAdapter != null) {

			// 禁用前台调度
			// mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onDestroy() {

		Log.i(TAG, "【onDestroy】");

		super.onDestroy();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);

		if (mLocalReceiver != null) {
			mLocalReceiver = null;
		}
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

			// 清空TextView
			clearAllTextView();

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			getBaseInfo(tag);

			packageDataAndSendBroadcast();

			String data = CardManager.load(tag, getResources());
			if (data == null) {
				return;
			}
			Log.i(TAG, data);
			tvContent.setText(data);
		}

	}

	/**
	 * 得到基础信息
	 * 
	 * @param tag
	 * @return
	 */
	private void getBaseInfo(Tag tag) {

		// 原始字节数组id
		mTagId = tag.getId();

		// 十六进制id
		mTagIdHex = Util.byte2HexString(mTagId);
		Log.i(TAG, "【getBaseInfo】	mTagIdHex = " + mTagIdHex);

		// 支持协议类型
		StringBuffer sb = new StringBuffer();
		String prefix = "android.nfc.tech.";// 前缀
		for (String tech : tag.getTechList()) {
			sb.append(tech.substring(prefix.length()));// 去掉前缀
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length());// 删除多余的逗号
		mTechnologies = sb.toString();
		Log.i(TAG, "【getBaseInfo】	mTechnologies = " + mTechnologies);

		// MifareClassic特有
		for (String tech : tag.getTechList()) {
			if (tech.equals(MifareClassic.class.getName())) {
				sb.append('\n');
				MifareClassic mifareTag = MifareClassic.get(tag);
				String type = "Unknown";
				switch (mifareTag.getType()) {
				case MifareClassic.TYPE_CLASSIC:
					type = "Classic";
					break;
				case MifareClassic.TYPE_PLUS:
					type = "Plus";
					break;
				case MifareClassic.TYPE_PRO:
					type = "Pro";
					break;
				}
				sb.append("Mifare Classic type: ");
				sb.append(type);
				sb.append('\n');

				sb.append("Mifare size: ");
				sb.append(mifareTag.getSize() + " bytes");
				sb.append('\n');

				sb.append("Mifare sectors: ");
				sb.append(mifareTag.getSectorCount());
				sb.append('\n');

				sb.append("Mifare blocks: ");
				sb.append(mifareTag.getBlockCount());
			}

			if (tech.equals(MifareUltralight.class.getName())) {
				sb.append('\n');
				MifareUltralight mifareUlTag = MifareUltralight.get(tag);
				String type = "Unknown";
				switch (mifareUlTag.getType()) {
				case MifareUltralight.TYPE_ULTRALIGHT:
					type = "Ultralight";
					break;
				case MifareUltralight.TYPE_ULTRALIGHT_C:
					type = "Ultralight C";
					break;
				}
				sb.append("Mifare Ultralight type: ");
				sb.append(type);
			}
		}

	}

	/**
	 * 封装数据为一个JSON并发送本地广播
	 */
	private void packageDataAndSendBroadcast() {

		// 封装进一个JSON中
		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put(Constant.GET_NFC_INFO_UID, mTagIdHex);
			jsonObject.put(Constant.GET_NFC_INFO_TECHNOLOGIES, mTechnologies);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 发送本地广播
		Intent intent = new Intent(Constant.LOCAL_BROADCAST_ACTION_GET_NFC_INFO_SUCCESS);
		intent.putExtra(Constant.GET_NFC_INFO_INTENT_EXTRA_NAME, jsonObject.toString());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		finish();
	}

	@Override
	/**
	 * onClick
	 */
	public void onClick(View v) {

		// 清除基础信息 btnClearBaseInfo
		if (v.getId() == EUExUtil.getResIdID("plugin_uexnfc_btn_clear_base_info")) {
			tvBaseInfo.setText("");
		}

		// 清除内容 btnClearContent
		else if (v.getId() == EUExUtil.getResIdID("plugin_uexnfc_btn_clear_content")) {
			tvContent.setText("");
		}

	}

	/**
	 * 清空所有TextView中的内容
	 */
	private void clearAllTextView() {

		tvBaseInfo.setText("");
		tvContent.setText("");
	}

	/**
	 * 本地广播接收器
	 * 
	 * @author waka
	 *
	 */
	class NFCActivityLocalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.i(TAG, "【onReceive】		action = " + action);

			// 获取NFC信息失败广播
			if (action.equals(Constant.LOCAL_BROADCAST_ACTION_GET_NFC_INFO_FAIL)) {

				// 关闭NFCActivity
				finish();
			}
		}

	}
}
