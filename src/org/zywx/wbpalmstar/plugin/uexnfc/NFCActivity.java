package org.zywx.wbpalmstar.plugin.uexnfc;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexnfc.card.CardManager;

import android.app.Activity;
import android.app.PendingIntent;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		// 初始化NFC相关变量
		initNFC();

		// 显式调用onNewIntent()方法
		onNewIntent(getIntent());

	}

	/**
	 * 初始化NFC
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
		super.onResume();

		if (mNfcAdapter != null && mIntentFilters != null && mTechListsArray != null) {

			// 启用前台调度
			// 须在主线程中被调用，并且只有在该Activity在前台时（要保证在onResume()方法中调用这个方法）
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mTechListsArray);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mNfcAdapter != null) {

			// 禁用前台调度
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	/**
	 * onNewIntent
	 * 
	 * 实现onNewIntent回调方法来处理扫描到的NFC标签的数据
	 */
	protected void onNewIntent(Intent intent) {
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
		if (action == null) {
			Toast.makeText(this, "action == null", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this, action, Toast.LENGTH_SHORT).show();

		// ACTION_NDEF_DISCOVERED
		if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {

		}

		// ACTION_TECH_DISCOVERED
		else if (action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {

			tvBaseInfo.setText("");
			tvContent.setText("");

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			tvBaseInfo.append(dumpTagData(tag));

			String data = CardManager.load(tag, getResources());
			if (data == null) {
				return;
			}
			Log.i(TAG, data);
			tvContent.setText(data);

		}
	}

	/**
	 * 解析基础信息
	 * 
	 * @param tag
	 * @return
	 */
	private String dumpTagData(Tag tag) {
		StringBuilder sb = new StringBuilder();
		byte[] tagId = tag.getId();
		sb.append("Tag ID (hex): ").append(Util.getHex(tagId)).append("\n");
		sb.append("Tag ID (dec): ").append(Util.getDec(tagId)).append("\n");
		sb.append("ID (reversed): ").append(Util.getReversed(tagId)).append("\n");

		String prefix = "android.nfc.tech.";
		sb.append("Technologies: ");
		for (String tech : tag.getTechList()) {
			sb.append(tech.substring(prefix.length()));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
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
		return sb.toString();
	}

	@Override
	public void onClick(View v) {

		// btnClearBaseInfo
		if (v.getId() == EUExUtil.getResIdID("plugin_uexnfc_btn_clear_support_formats")) {
			tvBaseInfo.setText("");
		}

		// btnClearContent
		else if (v.getId() == EUExUtil.getResIdID("plugin_uexnfc_btn_clear_content")) {
			tvContent.setText("");
		}

	}
}
