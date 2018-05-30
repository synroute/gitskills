package hiapp.modules.exam.bean;

public class Course {
	private String courseId;
	private String courseName;
	private String createTime;
	private String userId;
	private Integer isUsed;
	private Integer courseType;
	private String isUsedChina;
	private String courseTypeChina;
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
		if(this.isUsed==0) {
			this.isUsedChina="启用";
		}else {
			this.isUsedChina="停用";
		}	
	}
	public Integer getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
	}
	public Integer getCourseType() {
		return courseType;
	}
	public void setCourseType(Integer courseType) {
		this.courseType = courseType;
		if(this.courseType==0) {
			this.courseTypeChina="公开";
		}else {
			this.courseTypeChina="非公开";
		}	
	}
	public String getIsUsedChina() {
		return isUsedChina;
	}
	public void setIsUsedChina(String isUsedChina) {
		this.isUsedChina = isUsedChina;
	}
	public String getCourseTypeChina() {
		return courseTypeChina;
	}
	public void setCourseTypeChina(String courseTypeChina) {
		this.courseTypeChina = courseTypeChina;
	}
}
