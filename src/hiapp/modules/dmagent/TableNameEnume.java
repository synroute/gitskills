package hiapp.modules.dmagent;

public enum TableNameEnume {
	INPUTTABLENAME("HAU_DM_B","C_IMPORT"),PRESETTABLENAME("HASYS_DM_B","C_PRESETTIME"),JIEGUOTABLENAME("HAU_DM_B","C_JIEGUOBIAO");
	private String prefix;
	private String suffix;
	TableNameEnume(String prefix,String suffix){
		this.prefix = prefix;
		this.suffix = suffix;
	}
	public String getPrefix(){
		return this.prefix;
	}
	public String getSuffix(){
		return this.suffix;
	}
}
