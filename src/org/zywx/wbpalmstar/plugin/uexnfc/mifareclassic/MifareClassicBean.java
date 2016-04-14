package org.zywx.wbpalmstar.plugin.uexnfc.mifareclassic;

import org.zywx.wbpalmstar.plugin.uexnfc.NFCBaseBean;

import android.util.SparseArray;

public class MifareClassicBean extends NFCBaseBean {

	/**
	 * 协议名
	 */
	public static final String TECH_NAME = "MifareClassic";

	/**
	 * MifareClassic标签的具体类型：TYPE_CLASSIC，TYPE_PLUA，TYPE_PRO，TYPE_UNKNOWN；
	 */
	private int type;

	/**
	 * 标签总共有的扇区数量
	 */
	private int sectorCount;

	/**
	 * 标签总共有的的块数量
	 */
	private int blockCount;

	/**
	 * 标签的容量：SIZE_1K,SIZE_2K,SIZE_4K,SIZE_MINI
	 */
	private int size;

	/**
	 * 详细数据，使用SparseArray存储
	 */
	private SparseArray<String[]> detailData;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSectorCount() {
		return sectorCount;
	}

	public void setSectorCount(int sectorCount) {
		this.sectorCount = sectorCount;
	}

	public int getBlockCount() {
		return blockCount;
	}

	public void setBlockCount(int blockCount) {
		this.blockCount = blockCount;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public SparseArray<String[]> getDetailData() {
		return detailData;
	}

	public void setDetailData(SparseArray<String[]> detailData) {
		this.detailData = detailData;
	}

	/**
	 * toString
	 * 
	 * @formatter:off
	 */
	public String toString() {

		String s = super.toString();
		s += TECH_NAME
		  + "type = " + type + "\n"
		  + "sectorCount = " + sectorCount + "\n"
		  + "blockCount = " + blockCount + "\n"
		  + "size = " + size + "\n"
		  ;
		
		// @formatter:on
		if (detailData != null) {

			for (int i = 0; i < detailData.size(); i++) {

				// 添加区号
				s += "sector" + i + ":\n";
				int key = detailData.keyAt(i);
				String[] strings = detailData.get(key);

				for (String string : strings) {

					// 添加每一块的数据
					s += string + "\n";
				}
			}
		}

		return s;
	}
}
