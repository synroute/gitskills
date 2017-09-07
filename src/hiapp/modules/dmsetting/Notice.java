package hiapp.modules.dmsetting;
/**
 *类说明
 *@author Jiang Ning
 */
public class Notice {
	private Integer id;
	private Integer noticeId;//公告id
	private String noticeType;//公告类型
	private String noticeName;//公告标题
	private String notiCecontent;//公告内容
	private String grade;//重要级别 0：一般  1：重要  2：非常重要
	private String publishId;//发布人工号
	private String publishName;//发布人姓名
	private String publishTime;//发布时间
	private String receiveId;//接收人id
	
	public String getReceiveId() {
		return receiveId;
	}
	public void setReceiveId(String receiveId) {
		this.receiveId = receiveId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(Integer noticeId) {
		this.noticeId = noticeId;
	}
	public String getNoticeType() {
		return noticeType;
	}
	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}
	public String getNoticeName() {
		return noticeName;
	}
	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}
	public String getNotiCecontent() {
		return notiCecontent;
	}
	public void setNotiCecontent(String notiCecontent) {
		this.notiCecontent = notiCecontent;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String string) {
		this.grade = string;
	}
	public String getPublishId() {
		return publishId;
	}
	public void setPublishId(String publishId) {
		this.publishId = publishId;
	}
	public String getPublishName() {
		return publishName;
	}
	public void setPublishName(String publishName) {
		this.publishName = publishName;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
}
