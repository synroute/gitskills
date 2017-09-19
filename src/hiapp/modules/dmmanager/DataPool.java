package hiapp.modules.dmmanager;

import java.util.List;

public class DataPool {
	private Integer id;
	private String text;
	private List<DataPool> children;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<DataPool> getChildren() {
		return children;
	}
	public void setChildren(List<DataPool> children) {
		this.children = children;
	}
}
