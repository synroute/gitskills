package hiapp.modules.dmsetting;
/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午4:19:28 
 * 类说明  枚举信息
 */
public enum DMBizEnum {
		dialSubmit("拨打提交"),
		modifySubmit("修改提交");
			
		private DMBizEnum(String Type) {
			this.setType(Type);
	    	
	    }
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		//自增id
		private String	type;
		
}
