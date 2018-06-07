package hiapp.modules.exam.bean;

public class ExamInfo {
	private String examId;
	private String examName;
	private String startTime;
	private String endTime;
	private String isUsed;
	private String creatUserId;
	private String createTime;
	private Integer passLine;
	private Integer midLine;
	private Integer goodLine;
	private String examType;
	private Integer isUsedChina;
	private Integer examTypeNum;
	public Integer getIsUsedChina() {
		return isUsedChina;
	}
	public void setIsUsedChina(Integer isUsedChina) {
		this.isUsedChina = isUsedChina;
		if(this.isUsedChina==0) {
			this.isUsed="启用";
		}else {
			this.isUsed="停用";
		}
	}
	public Integer getExamTypeNum() {
		return examTypeNum;
	}
	public void setExamTypeNum(Integer examTypeNum) {
		this.examTypeNum = examTypeNum;
		if(this.examTypeNum==0) {
			this.examType="闭卷";
		}else {
			this.examType="开卷";
		}
	}

	public String getExamId() {
		return examId;
	}
	public void setExamId(String examId) {
		this.examId = examId;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}
	public String getCreatUserId() {
		return creatUserId;
	}
	public Integer getPassLine() {
		return passLine;
	}
	public void setPassLine(Integer passLine) {
		this.passLine = passLine;
	}
	public Integer getMidLine() {
		return midLine;
	}
	public void setMidLine(Integer midLine) {
		this.midLine = midLine;
	}
	public Integer getGoodLine() {
		return goodLine;
	}
	public void setGoodLine(Integer goodLine) {
		this.goodLine = goodLine;
	}
	public void setCreatUserId(String creatUserId) {
		this.creatUserId = creatUserId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getExamType() {
		return examType;
	}
	public void setExamType(String examType) {
		this.examType = examType;
	}
	
}
