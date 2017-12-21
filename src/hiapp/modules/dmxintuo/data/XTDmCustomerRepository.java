package hiapp.modules.dmxintuo.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import hiapp.utils.database.BaseRepository;
@Repository
public class XTDmCustomerRepository extends BaseRepository {
	

	public List<Map<String,String>> dmGetXTBizCustomer(int bizId,String type,String AppId)
	{
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		String sql="";
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Map<String,String> map=new HashMap<String, String>(); 
		if(type.equals("ACCT"))
		{
			sql="select AcNo,BankName from HAU_DM_B"+bizId+"C_ACCT where AppId='"+AppId+"'";
			try {
				dbConn =this.getDbConnection();
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while(rs.next()){
					map.put("AcNo", rs.getString(1));
					map.put("BankName", rs.getString(2));
					list.add(map);
				}
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			sql="select GageType,GageDesc from HAU_DM_B"+bizId+"C_GAGE where AppId='"+AppId+"'";
			try {
				dbConn =this.getDbConnection();
				stmt = dbConn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while(rs.next()){
					map.put("GageType", rs.getString(1));
					map.put("GageDesc", rs.getString(2));
					list.add(map);
				}
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
	}
}
