package org.zywx.wbpalmstar.plugin.uexnfc.mifareclassic;

import java.io.IOException;

import org.zywx.wbpalmstar.plugin.uexnfc.Util;
import org.zywx.wbpalmstar.plugin.uexnfc.bean.MifareClassicBean;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;
import android.util.SparseArray;

/**
 * MifareClassic类型操作辅助类
 * 
 * @author waka
 * 
 * @参考文章 http://drops.wooyun.org/tips/2065
 */
public class MifareClassicHelper {

	private static final String TAG = "MifareClassicHelper";

	/**
	 * MifareClassic对象的实例
	 */
	private MifareClassic mMifareClassic;

	/**
	 * 判断Tag是否支持MifareClassic协议的标识量
	 */
	private boolean isMifareClassic;

	/**
	 * 构造方法
	 * 
	 * @param tag
	 *            须传入Tag
	 */
	public MifareClassicHelper(Tag tag) {

		mMifareClassic = MifareClassic.get(tag);

		// 如果==null
		if (mMifareClassic == null) {

			// 不支持
			isMifareClassic = false;
		} else {

			// 支持
			isMifareClassic = true;
		}
	}

	/**
	 * 返回标识量
	 * 
	 * @return
	 */
	public boolean isMifareClassic() {
		return isMifareClassic;
	}

	/**
	 * 读取数据(通过默认出厂密码)
	 * 
	 * 耗时操作，须在子线程中执行
	 * 
	 * @return
	 */
	public synchronized MifareClassicBean read() {

		if (!isMifareClassic) {

			Log.i(TAG, "isMifareClassic == false");
			return null;
		}

		// 新建Bean类存数据
		MifareClassicBean mcBean = new MifareClassicBean();

		// 基础信息
		mcBean.setType(mMifareClassic.getType());
		mcBean.setSectorCount(mMifareClassic.getSectorCount());
		mcBean.setBlockCount(mMifareClassic.getBlockCount());
		mcBean.setSize(mMifareClassic.getSize());

		// 连接
		try {
			mMifareClassic.connect();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "【read】	mMifareClassic.connect() IOException", e);
		}

		// 新建SparseArray
		SparseArray<String[]> sparseArray = new SparseArray<String[]>();

		// 循环遍历每个扇区
		for (int i = 0; i < mcBean.getSectorCount(); i++) {

			try {

				// 验证当前扇区的KeyA密码
				boolean authA = mMifareClassic.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT);// 默认出厂密码：KEY_DEFAULT
				Log.d(TAG, "【read】	keyA验证-默认出厂密码-扇区号 = " + i + " 验证结果 = " + authA);

				// 验证当前扇区的KeyB密码
				boolean authB = mMifareClassic.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT);// 默认出厂密码：KEY_DEFAULT
				Log.d(TAG, "【read】	keyB验证-默认出厂密码-扇区号 = " + i + " 验证结果 = " + authB);

				// 如果keyA或keyB验证成功
				if (authA || authB) {

					// 获得当前扇区的块(block)数量
					int blockCount = mMifareClassic.getBlockCountInSector(i);

					// 新建String数组，用来存放每一扇区的数据
					String[] sectorData = new String[blockCount];

					// 获得当前扇区的第一个块的索引，扇区是从0开始的，但是块不是，所以要获得第一个的索引，然后在基础上++
					int blockIndex = mMifareClassic.sectorToBlock(i);

					// 遍历每个块
					for (int j = 0; j < blockCount; j++) {

						// 获得块数据
						byte[] data = mMifareClassic.readBlock(blockIndex);
						String dataString = Util.byte2HexString(data);
						Log.d(TAG, "【read】	第" + i + "个区-第" + j + "个块的索引为 " + blockIndex + " 数据为 " + dataString);

						// 存放数据到数组中
						sectorData[j] = dataString;

						// 在之前索引的基础上++
						blockIndex++;
					}

					// 将扇区数据数组放入SparseArray中
					sparseArray.put(i, sectorData);
				}

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "【read】	循环遍历每个扇区 	IOException", e);
			}
		}

		// 最后记得关闭
		if (mMifareClassic != null) {
			try {
				mMifareClassic.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "【read】	mMifareClassic.close() IOException", e);
			}
		}

		// 把SparseArray放入Bean中
		mcBean.setDetailData(sparseArray);

		return mcBean;
	}

}
