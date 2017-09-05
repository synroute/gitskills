package hiapp.modules.dmsetting.dbLayer;

public class DMModeSubMode {
	private int modeId;
	private int subModeId;
	private String modeIdNameChString;
	private String subModeIdNameChString;
	private String text;
	public int getModeId() {
		return modeId;
	}
	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
	public int getSubModeId() {
		return subModeId;
	}
	
	public void setSubModeId(int subModeId) {
		this.subModeId = subModeId;
	}
	public String getModeIdNameChString() {
		return modeIdNameChString;
	}
	public void setModeIdNameChString(String modeIdNameChString) {
		this.modeIdNameChString = modeIdNameChString;
	}
	public String getSubModeIdNameChString() {
		return subModeIdNameChString;
	}
	public void setSubModeIdNameChString(String subModeIdNameChString) {
		this.subModeIdNameChString = subModeIdNameChString;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
