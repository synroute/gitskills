package hiapp.modules.exam.bean;

public class CourseWare {
	private String courseWareId;
	private String courseWare;
	private String courseWareSub;
	private String subject;
	private String content;
	private Integer isUsed;
	private Integer useNumber;
	private String createTime;
	private String createUser;
	private String address;
	private String isUsedChina;
	public String getIsUsedChina() {

		return isUsedChina;
	}
	public void setIsUsedChina(String isUsedChina) {
		this.isUsedChina = isUsedChina;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCourseWare() {
		return courseWare;
	}
	public String getCourseWareId() {
		return courseWareId;
	}
	public void setCourseWareId(String courseWareId) {
		this.courseWareId = courseWareId;
	}
	public void setCourseWare(String courseWare) {
		this.courseWare = courseWare;
	}
	public String getCourseWareSub() {
		return courseWareSub;
	}
	public void setCourseWareSub(String courseWareSub) {
		this.courseWareSub = courseWareSub;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
		if(this.isUsed==0) {
			this.isUsedChina="启用";
		}else {
			this.isUsedChina="停用";
		}	
	}
	public Integer getUseNumber() {
		return useNumber;
	}
	public void setUseNumber(Integer useNumber) {
		this.useNumber = useNumber;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

}
