package hiapp.modules.exam.bean;

public enum ExamStatus {
	NOEXAM("未考试"),EXAMING("正在考试"),EXAMOVER("完成考试"),BEFOREOVEREXAM("强制交卷");
	private ExamStatus(String name) {
		this.name = name;
	}
	private String name;
	public String getName() {
		return name;
	}


}
