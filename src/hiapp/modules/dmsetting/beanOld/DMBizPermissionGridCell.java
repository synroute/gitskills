package hiapp.modules.dmsetting.beanOld;

public class DMBizPermissionGridCell {
	private String field;

	private String value;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(int value) {
		this.value = String.format("%d", value);
	}
}
