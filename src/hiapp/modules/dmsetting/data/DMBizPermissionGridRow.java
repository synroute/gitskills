package hiapp.modules.dmsetting.data;

import hiapp.modules.dmsetting.DMBizPermissionGridCell;

import java.util.ArrayList;
import java.util.List;

public class DMBizPermissionGridRow {
	private int permId;
	private List<DMBizPermissionGridCell> listBizPermissionGridCells;
	public void addCell(DMBizPermissionGridCell cell){
		if(listBizPermissionGridCells==null){
			listBizPermissionGridCells=new ArrayList<DMBizPermissionGridCell>();
		}
		listBizPermissionGridCells.add(cell);
	}
	public int getPermId() {
		return permId;
	}
	public void setPermId(int permId) {
		this.permId = permId;
	}
	public List<DMBizPermissionGridCell> getListBizPermissionGridCells() {
		return listBizPermissionGridCells;
	}
}
