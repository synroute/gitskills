package hiapp.modules.dmsetting.beanOld;


public enum DMModeEnum {
	MODE1(0,1,"Manual",			"手动分配","手动分配,坐席决定拨打的客户和时机"),
	MODE2(1,2,"SystemDistrube",	"自动抽取数据","系统决定抽取的数据，坐席决定重拨的客户和时机"),
	MODE3(2,3,"SystemAll",		"服务器定义","系统决定拨打或重拨的坐席、客户和时机");
	private int index;
	private int id;
	private String name;
	private String nameCh;
	private String description;
	
	private DMModeEnum(int index,int id,String name,String nameCh,String description) {
		this.index=index;
		this.id=id;
    	this.name=name;
    	this.nameCh=nameCh;
    	this.description=description;
    }  
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public static int getId(int index) {
        for (DMModeEnum t : DMModeEnum.values()) {  
            if (t.getIndex() == index) {  
                return t.getId();  
            }  
        }
        return -1;
	}
	public static DMModeEnum get(int id) {
        for (DMModeEnum t : DMModeEnum.values()) {  
            if (t.getId() == id) {  
                return t;  
            }  
        }
        return null;
	}

	public static String getNameCh(int index) {
        for (DMModeEnum t : DMModeEnum.values()) {  
            if (t.getIndex() == index) {  
                return t.getNameCh();  
            }  
        }
        return "";
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
        return DMModeEnum.values().length;
    }

}
