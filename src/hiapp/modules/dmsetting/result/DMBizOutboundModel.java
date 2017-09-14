package hiapp.modules.dmsetting.result;
/** 
 * @author yangwentian 
 * @version 创建时间：2017年9月11日 下午3:20:15 
 * 类说明 
 */
public class DMBizOutboundModel {
	private int modelId;
	private String modelType;
	private String modelName;
	private String description;
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
