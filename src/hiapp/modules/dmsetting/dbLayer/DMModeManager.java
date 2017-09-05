package hiapp.modules.dmsetting.dbLayer;
import java.util.List;

public class DMModeManager {
	public static boolean getAllModeSubMode(List<DMModeSubMode> listModeSubModeText) {
		int countMode=DMModeEnum.getCount();
		for(int ii=0;ii<countMode;ii++){
			int modeId=DMModeEnum.getId(ii);
			String modeNameCh=DMModeEnum.getNameCh(ii);
			String modeIdNameChString=String.format("%d:%s", modeId,modeNameCh);
			
			int countSubMode=DMSubModeEnum.getCount();
			for(int jj=0;jj<countSubMode;jj++){
				int modeId1=DMSubModeEnum.getModeId(jj);
				if(modeId1==modeId){
					int subModeId=DMSubModeEnum.getId(jj);
					String subModeNameChString=DMSubModeEnum.getNameCh(jj);
					String subModeIdNameChString=String.format("&nbsp;&nbsp;&nbsp;&nbsp;%d:%s", subModeId,subModeNameChString);

					DMModeSubMode dmModeSubMode=new DMModeSubMode();
					dmModeSubMode.setModeId(modeId);
					dmModeSubMode.setSubModeId(subModeId);
					dmModeSubMode.setModeIdNameChString(modeIdNameChString);
					dmModeSubMode.setSubModeIdNameChString(subModeIdNameChString);
					dmModeSubMode.setText(subModeIdNameChString);
					listModeSubModeText.add(dmModeSubMode);
				}
				
			}
		}
		return true;
	}
	
}
