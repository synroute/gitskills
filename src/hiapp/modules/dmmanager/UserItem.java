package hiapp.modules.dmmanager;

import hiapp.utils.serviceresult.TreeBranch;

public class UserItem extends TreeBranch{
	private int dicId; 
	public int getDicId() {
		return dicId;
	}
	public void setDicId(int dicId) {
		this.dicId = dicId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public String getItemText() {
		return itemText;
	}
	public void setItemText(String itemText) {
		this.itemText = itemText;
	}
	private int itemId; 
	private String itemText; 
}
