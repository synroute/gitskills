package hiapp.modules.dmsetting.data;
public enum DMSubModeEnum {
	SUBMODE1(0,1,1,"base",							"基本","手动分配基本"),

	SUBMODE2(1,2,1,"base",							"基本","无重拨"),
	SUBMODE3(2,2,2,"hiDialerSingleNumber",			"hiDialer单号码自动外拨","hiDialer单号码自动外拨，接通后到坐席，即分配给该坐席"),
	SUBMODE4(3,2,3,"hiDialerMultiNumber",			"hiDialer多号码自动外拨","hiDialer多号码自动外拨，接通后到坐席，即分配给该坐席"),
	
	SUBMODE5(4,3,1,"base",							"基本","单号码，无重拨"),
	SUBMODE6(5,3,2,"SingleNumberRedial",			"单号码重拨","单号码重拨"),
	SUBMODE7(6,3,3,"MultiNumberRedial",				"多号码重拨","多号码重拨"),
	SUBMODE8(7,3,4,"SingleNumberRedialHiDialer",	"hiDialer单号码重拨","hiDialer单号码重拨"),
	SUBMODE9(8,3,5,"MultiNumberRedialHiDialer",		"hiDialer多号码重拨","hiDialer多号码重拨");
	
	private int index;
	private int modeId;
	private int id;
	private String name;
	private String nameCh;
	private String description;
	
	private DMSubModeEnum(int index,int modeId,int id,String name,String nameCh,String description) {
		this.index=index;
		this.modeId=modeId;
		this.id=id;
    	this.name=name;
    	this.nameCh=nameCh;
    	this.description=description;
    }
	public static DMSubModeEnum get(int id) {
        for (DMSubModeEnum t : DMSubModeEnum.values()) {  
            if (t.getId() == id) {  
                return t;  
            }  
        }
        return null;
	}


	public static int getId(int index) {
        for (DMSubModeEnum t : DMSubModeEnum.values()) {  
            if (t.getIndex() == index) {  
                return t.getId();  
            }  
        }
        return -1;
	}
	public static int getModeId(int index) {
        for (DMSubModeEnum t : DMSubModeEnum.values()) {  
            if (t.getIndex() == index) {  
                return t.getModeId();  
            }  
        }
        return -1;
	}
	public static String getNameCh(int index) {
        for (DMSubModeEnum t : DMSubModeEnum.values()) {  
            if (t.getIndex() == index) {  
                return t.getNameCh();  
            }  
        }
        return "";
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	public int getModeId() {
		return modeId;
	}

	public void setModeId(int modeId) {
		this.modeId = modeId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameCh() {
		return nameCh;
	}
	public void setNameCh(String nameCh) {
		this.nameCh = nameCh;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public static int getCount() {  
        return DMSubModeEnum.values().length;
    }

}
