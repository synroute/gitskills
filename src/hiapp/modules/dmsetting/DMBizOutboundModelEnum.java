package hiapp.modules.dmsetting;
/** 
 * @author liuhao
 * @version 创建时间：2017年9月8日 下午3:39:09 
 * 类说明  外呼模式信息
 */
public enum DMBizOutboundModelEnum {
			MODE1(1,1,"自主型","手动分配"),
			MODE2(2,2,"自主型","hidialer自动外呼"),
			MODE3(3,3,"策略型","单号码重拨"),
			MODE4(4,4,"策略型","多号码重拨"),
			MODE5(5,5,"策略型","单号码预测外拨"),
			MODE6(6,6,"策略型","多号码预测外拨");
			
		private DMBizOutboundModelEnum(int id,int outboundID,String outboundType,String outboundMode) {
			this.id=id;
			this.outboundID=outboundID;
			this.outboundType=outboundType;
	    	this.outboundMode=outboundMode;
	    	
	    }
			//自增id
			private int	id;
			//外呼模式id
			private int	outboundID;
			//外呼模式类型
			private String	outboundType;
			//外呼模式
			private String	outboundMode;
			
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
			public int getOutboundID() {
				return outboundID;
			}
			public void setNameCh(int outboundID) {
				this.outboundID = outboundID;
			}
			public String getOutboundType() {
				return outboundType;
			}
			public void setOutboundType(String outboundType) {
				this.outboundType = outboundType;
			}
			public String getOutboundMode() {
				return outboundMode;
			}
			public void setOutboundMode(String outboundMode) {
				this.outboundMode = outboundMode;
			}
			
			
}
