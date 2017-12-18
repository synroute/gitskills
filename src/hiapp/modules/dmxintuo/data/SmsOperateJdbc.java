package hiapp.modules.dmxintuo.data;

import hiapp.modules.dmxintuo.bean.SmsTemplate;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class SmsOperateJdbc extends BaseRepository{
	/**
	 * 查询出所有模板
	 * @return
	 */
	public List<SmsTemplate> getAllData(){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<SmsTemplate> list=new ArrayList<SmsTemplate>();
		try {
			conn=this.getDbConnection();
			String sql="select id,TEMPPLATENAME,TEMPLATETYPE,CONTENT from hasys_dm_smstemplate";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()){
				SmsTemplate sms=new SmsTemplate();
				sms.setId(rs.getInt(1));
				sms.setName(rs.getString(2));
				sms.setDataType(rs.getString(3));
				sms.setContent(rs.getString(4));
				list.add(sms);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
	/**
	 * 添加一个模板
	 * @param templateName
	 * @param templateType
	 * @param content
	 */
	public Map<String,Object> inserSmstTemplate(String  templateName,String  templateType,String content){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Map<String,Object> result=new HashMap<String,Object>();
		try {
			conn=this.getDbConnection();
			String selectSql="select id from hasys_dm_smstemplate where TEMPLATETYPE=?";
			pst=conn.prepareStatement(selectSql);
			pst.setString(1,templateType);
			rs=pst.executeQuery();
			Integer id=null;
			while(rs.next()){
				id=rs.getInt(1);
			}
			if(id!=null){
				result.put("result",false);
				result.put("message","该合作机构已存在,不能添加");
				return result;
			}
			DbUtil.DbCloseQuery(rs, pst);
			String sql="insert into hasys_dm_smstemplate(id,TEMPPLATENAME,TEMPLATETYPE,CONTENT) values(S_HASYS_DM_SIMTEMPLATE.nextval,?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1,templateName);
			pst.setString(2,templateType);
			pst.setString(3,content);
			pst.executeUpdate();
			result.put("result",true);
			result.put("message","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("result",false);
			result.put("message","添加失败");
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return result;
	}
	/**
	 * 获取导入表所有自定义字段
	 * @param bizId
	 */
	public List<Map<String,Object>>  getImportColumns(String workSheetId){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String sql="select '&lt'||t.columnname||'&gt' columnname,t.columnnamech from HASYS_WORKSHEETCOLUMN t where t.worksheetid=? and t.issyscolumn=0";
			pst=conn.prepareStatement(sql);
			pst.setString(1, workSheetId);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("columnname",rs.getString(1));
				map.put("columnnamech", rs.getString(2));
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
	/**
	 * 根据id获取模板信息
	 * @param id
	 */
	public SmsTemplate getSmsTemplateInfoById(Integer id){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		SmsTemplate sms=new SmsTemplate();
		try {
			conn=this.getDbConnection();
			String sql="select id,TEMPPLATENAME,TEMPLATETYPE,CONTENT from hasys_dm_smstemplate where id=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,id);
			rs=pst.executeQuery();
			while(rs.next()){
				sms.setId(rs.getInt(1));
				sms.setName(rs.getString(2));
				sms.setDataType(rs.getString(3));
				sms.setContent(rs.getString(4));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return sms;
	}
	
	/**
	 * 根据Id删除模板
	 * @param id
	 * @return
	 */
	public Map<String,Object> deleteTemplateById(Integer id){
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> result=new HashMap<String,Object>();
		try {
			conn=this.getDbConnection();
			String sql="delete from hasys_dm_smstemplate where id=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,id);
			pst.executeUpdate();
			result.put("result",true);
			result.put("message","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("result",false);
			result.put("message","删除失败");
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return result;
	}
	
	public List<Map<String,Object>> getSmsType(){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			String sql="select ITEMTEXT from HASYS_DIC_ITEM where DICID=519 and ITEMPARENT=1";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("name",rs.getString(1));
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return list;
	}
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public Map<String,Object> deteleBatchData(String ids){
		Connection conn=null;
		PreparedStatement pst=null;
		String[] arr=ids.split(",");
		Map<String,Object> result=new HashMap<String,Object>();
		try {
			conn=this.getDbConnection();
			String sql="delete from hasys_dm_smstemplate where id in(";
			for (int i = 0; i < arr.length; i++) {
				if(arr[i]==null||"".equals(arr[i])){
					continue;
				}
				sql+=arr[i]+",";
			}
			sql=sql.substring(0,sql.length()-1)+")";
			pst=conn.prepareStatement(sql);
			pst.executeUpdate();
			result.put("result",true);
			result.put("message","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("result",false);
			result.put("message","删除失败");
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return result;
	}
	
	/**
	 * 根据合作机构获取短信模板内容
	 * @param templateType
	 * @return
	 */
	public Map<String,Object> getContentByType(String templateType){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		String content=null;
		Map<String,Object> map=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String sql="select content from hasys_dm_smstemplate where TEMPLATETYPE=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,templateType);
			rs=pst.executeQuery();
			while(rs.next()){
				content=rs.getString(1);
			}
			map.put("result",true);
			map.put("content", content);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("result",false);
			map.put("content", "查询错误");
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return map;
	}
}
