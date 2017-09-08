package hiapp.modules.dmsetting;
/** 
 * @author liuhao 
 * @version 创建时间：2017年9月8日 下午02:15:42 
 * 类说明  用于存储结束码信息
 */
public class DMBizOutboundModel {
			//自增id
			private int	id;
			//外呼模式id
			private int	outboundID;
			//外呼类型
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
