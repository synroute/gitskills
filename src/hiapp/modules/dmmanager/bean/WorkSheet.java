package hiapp.modules.dmmanager.bean;

public class WorkSheet {
	private Integer id;
	private String name;
	private String nameCh;
	private String description;
	private Integer isOwner;
	public Integer getIsOwner() {
		return isOwner;
	}
	public void setIsOwner(Integer isOwner) {
		this.isOwner = isOwner;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNameCh() {
		return nameCh;
	}
	public void setNameCh(String nameCh) {
		this.nameCh = nameCh;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
