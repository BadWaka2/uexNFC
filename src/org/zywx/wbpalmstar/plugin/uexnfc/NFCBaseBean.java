package org.zywx.wbpalmstar.plugin.uexnfc;

public class NFCBaseBean {

	/**
	 * 原始字符数组ID
	 */
	private byte[] tagId;

	/**
	 * 十六进制ID
	 */
	private String tagIdHex;

	/**
	 * 所支持的协议类型
	 */
	private String technologies;

	public byte[] getTagId() {
		return tagId;
	}

	public void setTagId(byte[] tagId) {
		this.tagId = tagId;
	}

	public String getTagIdHex() {
		return tagIdHex;
	}

	public void setTagIdHex(String tagIdHex) {
		this.tagIdHex = tagIdHex;
	}

	public String getTechnologies() {
		return technologies;
	}

	public void setTechnologies(String technologies) {
		this.technologies = technologies;
	}

	/**
	 * toString
	 * 
	 */
	public String toString() {

		String s = null;

		if (tagId != null) {
			s += "tagId = " + tagId + "\n";
		}

		if (tagIdHex != null) {
			s += "tagIdHex = " + tagIdHex + "\n";
		}

		if (technologies != null) {
			s += "technologies = " + technologies + "\n";
		}

		return s;
	}

	/**
	 * 根据baseBean设置BaseBean的值
	 * 
	 * @param baseBean
	 */
	public void setBaseBean(NFCBaseBean baseBean) {

		tagId = baseBean.getTagId();
		tagIdHex = baseBean.getTagIdHex();
		technologies = baseBean.getTechnologies();
	}

}
