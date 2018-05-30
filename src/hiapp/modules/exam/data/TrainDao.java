package hiapp.modules.exam.data;

import hiapp.modules.exam.bean.CourseWare;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.idfactory.IdFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TrainDao extends BaseRepository{
	@Autowired
	private Logger logger;
	@Autowired
	private IdFactory idFactory;
	public List<Map<String,Object>> getCourseWareType(String itemId){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		String sql=null;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		try {
			conn=this.getDbConnection();
			if(itemId!=null&&!"".equals(itemId)){
				sql="select itemId,itemText from hasys_dic_item where ITEMPARENT="+itemId;
			}else{
				sql="select itemId,itemText from hasys_dic_item where dicId=131 and ITEMPARENT=-1 ";
			}
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("typeId", rs.getObject(1));
				map.put("typeName", rs.getObject(2));
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally{
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
	/**
	 * 新增课件
	 * @param courseWare
	 * @param courseWareSub
	 * @param subject
	 * @param content
	 * @param isUsed
	 * @param address
	 * @param userId
	 * @return
	 */
	public Map<String,Object> insertCourseWare(String courseWare,String courseWareSub,String subject,String content,Integer isUsed,String address,String userId){
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String courseWareId=idFactory.newId("EX_CW");
		try {
			conn=this.getDbConnection();
			String sql="insert into EM_INF_COURSEWARE(COURSEWARETYPE,COURSEWARESUB,SUBJECT,CONTENT,ADDRESS,USENUMBER,CREATETIME,USERID,ISUSED,ISUPDATE,COURSEWAREID)"+
					   " values(?,?,?,?,?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1,courseWare);
			pst.setString(2,courseWareSub);
			pst.setString(3,subject);
			pst.setString(4,content);
			pst.setString(5,address);
			pst.setInt(6,0);
			pst.setString(7,userId);
			pst.setInt(8,isUsed);
			pst.setInt(9,1);
			pst.setString(10, courseWareId);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally{
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 课件查询
	 * @param courseWare
	 * @param courseWareSub
	 * @param subject
	 * @param startTime
	 * @param endTime
	 * @param createUser
	 * @param num
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getCourses(String courseWare,String courseWareSub,String subject,String startTime,String endTime,String createUser,Integer num,Integer pageSize){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		Map<String,Object> resultMap=new HashMap<String,Object>();
		List<CourseWare> list=new ArrayList<CourseWare>();
		try {
			conn=this.getDbConnection();
			String selectSql="select COURSEWAREID,COURSEWARETYPE,COURSEWARESUB,SUBJECT,CONTENT,USENUMBER,CREATETIME,USERID,isUsed,ADDRESS from (";
			String sql="select COURSEWAREID,COURSEWARETYPE,COURSEWARESUB,SUBJECT,CONTENT,USENUMBER,to_char(CREATETIME,'yyyy-mm-dd hh24:mi:ss') CREATETIME,USERID,isUsed,ADDRESS,rownum rn from EM_INF_COURSEWARE a where 1=1 ";
			if(courseWare!=null&&!"".equals(courseWare)){
				sql+="and COURSEWARETYPE='"+courseWare+"' ";
			}
			if(courseWareSub!=null&&!"".equals(courseWareSub)){
				sql+="and COURSEWARESUB='"+courseWareSub+"' ";
			}
			if(subject!=null&&!"".equals(subject)){
				sql+="and SUBJECT like '%"+courseWareSub+"%' ";
			}
			if(startTime!=null&&!"".equals(startTime)){
				sql+="and CREATETIME >to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss') ";
			}
			if(endTime!=null&&!"".equals(endTime)){
				sql+="and CREATETIME <to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss') ";
			}
			if(createUser!=null&&!"".equals(createUser)){
				sql+="and USERID='"+createUser+"' ";
			}
			selectSql=selectSql+sql+" and rownum<?) where rn>=?";
			pst=conn.prepareStatement(selectSql);
			pst.setInt(1, endNum);
			pst.setInt(2, startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				CourseWare course=new CourseWare();
				course.setCourseWareId(rs.getString(1));
				course.setCourseWare(rs.getString(2));
				course.setCourseWareSub(rs.getString(3));
				course.setSubject(rs.getString(4));
				course.setContent(rs.getString(5));
				course.setUseNumber(rs.getInt(6));
				course.setCreateTime(rs.getString(7));
				course.setCreateUser(rs.getString(8));
				course.setIsUsed(rs.getInt(9));
				course.setAddress(rs.getString(10));
				list.add(course);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String getCountSql="select count(*) from ("+sql+")";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()) {
				total=rs.getInt(1);
			}
			resultMap.put("rows", list);
			resultMap.put("total", total);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 下载课件
	 * @param userId
	 * @param trainId
	 */
	public void downLoadCourseWare(String userId,String trainId) {
		Connection conn=null;
		PreparedStatement pst=null;
		try {
			conn=this.getDbConnection();
			String updateTrainUserSql="update EM_INF_TRAINUSER set DOWNLOADNUM=DOWNLOADNUM+1 where userId=? and trainId=?";
			pst=conn.prepareStatement(updateTrainUserSql);
			pst.setString(1, userId);
			pst.setString(2, trainId);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
	}
	/**
	 * 浏览课件
	 * @param userId
	 * @param trainId
	 */
	public void checkCourseWare(String userId,String trainId,String courseWareId) {
		Connection conn=null;
		PreparedStatement pst=null;
		try {
			conn=this.getDbConnection();
			String updateCourseWareSql="update EM_INF_COURSEWARE set USENUMBER=USENUMBER+1 where COURSEWAREID=?";
			pst=conn.prepareStatement(updateCourseWareSql);
			pst.setString(1, courseWareId);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String updateTrainUserSql="update EM_INF_TRAINUSER set DOWNLOADNUM=BROWSENUM+1 where userId=? and trainId=?";
			pst=conn.prepareStatement(updateTrainUserSql);
			pst.setString(1, userId);
			pst.setString(2, trainId);
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
	}
	/**
	 * 修改课件
	 * @param courseWareId
	 * @param courseWare
	 * @param courseWareSub
	 * @param subject
	 * @param content
	 * @param isUsed
	 * @param address
	 * @param userId
	 * @return
	 */
	
	public Map<String,Object>  updateCourseWare(String courseWareId,String courseWare,String courseWareSub,String subject,String content,Integer isUsed,String address,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String updateSql="update EM_INF_COURSEWARE set COURSEWARETYPE=?,courseWareSub=?,subject=?,content=?,USERID=?,ISUSED=?,ISUPDATE=ISUPDATE+1,ADDRESS=ADDRESS||','||? where COURSEWAREID=?";
			pst=conn.prepareStatement(updateSql);
			pst.setString(1,courseWare);
			pst.setString(2,courseWareSub);
			pst.setString(3,subject);
			pst.setString(4,content);
			pst.setString(5, userId);
			pst.setInt(6, isUsed);
			pst.setString(7, address);
			pst.setString(8, courseWareId);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","修改成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","修改失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 修改地址
	 * @param courseWareId
	 * @param address
	 * @return
	 */
	public Map<String,Object> updateCourseWareAddress(String courseWareId,String address) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String updateSql="update EM_INF_COURSEWARE set address=replace(address,'"+address+",','') where COURSEWAREID='"+courseWareId+"'";
			pst=conn.prepareStatement(updateSql);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","删除失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 删除课件
	 * @param courseWareIds
	 */
	public Map<String,Object> deleteCourseWare(String courseWareIds) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String[] courseWareArr=courseWareIds.split(",");
		try {
			conn=this.getDbConnection();
			String deleteSql="delete from EM_INF_COURSEWARE where COURSEWAREID in(";
			for (int i = 0; i < courseWareArr.length; i++) {
				String courseWare=courseWareArr[i];
				if(courseWare==null||"".equals(courseWare)) {
					continue;
				}
				deleteSql+="'"+courseWare+"',";
			}
			deleteSql=deleteSql.substring(0,deleteSql.length()-1)+")";
			pst=conn.prepareStatement(deleteSql);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","删除失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 新增课程
	 * @param userId
	 * @param courseName
	 * @param isUsed
	 * @return
	 */
	public Map<String,Object> insertCourse(String userId,String courseName,Integer isUsed) {
		Connection conn=null;
		PreparedStatement pst=null;
		String courseId=idFactory.newId("EX_CS");
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String insertSql="insert into EM_INF_COURSE(COUSERID,COURSENAME,CREATETIME,USERID,ISUSED,ISUPDATE) values(?,?,sysdate,?,?)";
			pst=conn.prepareStatement(insertSql);
			pst.setString(1,courseId);
			pst.setString(2,courseName);
			pst.setInt(3, isUsed);
			pst.setInt(4, 0);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 修改课程
	 * @param userId
	 * @param courseName
	 * @param isUsed
	 * @param courseId
	 * @return
	 */
	public  Map<String,Object>  updateCourse(String userId,String courseName,Integer isUsed,String courseId) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String updateSql="update EM_INF_COURSE set COURSENAME=?,CREATETIME=sysdate,USERID=?,ISUSED=?,ISUPDATE=ISUPDATE+1 where COUSERID=?";
			pst=conn.prepareStatement(updateSql);
			pst.setString(1, courseName);
			pst.setString(2, userId);
			pst.setInt(3, isUsed);
			pst.setString(4, courseId);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 给课程选择课件
	 * @param courseId
	 * @param courseWareIds
	 */
	public Map<String,Object> insertCourseWareToCourse(String courseId,String courseWareIds){
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String[] arr=courseWareIds.split(",");
		try {
			conn=this.getDbConnection();
			String deleteSql="delete from EM_MAP_COURSE where COURSEID=?";
			pst=conn.prepareStatement(deleteSql);
			pst.setString(1, courseId);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			conn.setAutoCommit(false);
			String insertSql="insert into EM_MAP_COURSE(COURSEID,COURSEWAREID,SHOWORDER) values(?,?,?)";
			pst=conn.prepareStatement(insertSql);
			int m=1;
			for (int i = 0; i < arr.length; i++) {
				String courseWareId=arr[i];
				if(courseWareId==null||"".equals(courseWareId)) {
					continue;
				}
				pst.setString(1, courseId);
				pst.setString(2, courseWareId);
				pst.setInt(3, m);
				pst.addBatch();
				m++;
			}
			pst.executeBatch();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","保存成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","保存失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}

	/**
	 * 删除课程
	 * @param courseIds
	 * @return
	 */
	public Map<String,Object> deleteCourses(String courseIds) {
		Connection conn=null;
		PreparedStatement pst=null;
		String[]  arr=courseIds.split(",");
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(true);
			String deletSql="delete from EM_INF_COURSE where COURSEID in(";
			String deleteCourseWareSql="delete from EM_MAP_COURSE where COURSEID in(";
			for (int i = 0; i < arr.length; i++) {
				String courseId=arr[i];
				if(courseId==null||"".equals(courseId)) {
					continue;
				}
				deletSql+="'"+courseId+"',";
				deleteCourseWareSql+="'"+courseId+"',";
			}
			deletSql=deletSql.substring(0,deletSql.length()-1)+")";
			pst=conn.prepareStatement(deletSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			deleteCourseWareSql=deleteCourseWareSql.substring(0,deleteCourseWareSql.length()-1)+")";
			pst=conn.prepareStatement(deleteCourseWareSql);
			pst.executeUpdate();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","删除失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 查询课程
	 * @param courseName
	 * @param isUsed
	 * @param startTime
	 * @param endTime
	 * @param courseType
	 * @return
	 */
	public Map<String,Object> selectCourses(String courseName,Integer isUsed,String startTime,String endTime,Integer courseType,Integer num,Integer pageSize) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		Map<String,Object> resultMap=new HashMap<>();
		List<Map<String,Object>> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select COUSERID,COURSENAME,CREATETIME,userId,ISUSED,courseType from(";
			String selectSql="select COUSERID,COURSENAME,to_char(CREATETIME,'yyyy-mm-dd hh24:mi:ss') CREATETIME,userId,ISUSED,courseType,rownum rn from EM_INF_COURSE where 1=1 ";
			if(courseName!=null&&!"".equals(courseName)) {
				selectSql+=" and COURSENAME='"+courseName+"'";
			}
			if(isUsed!=2) {
				selectSql+=" and ISUSED="+isUsed;
			}
			
			if(startTime!=null&&!"".equals(startTime)) {
				selectSql+=" and CREATETIME>=to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			
			if(endTime!=null&&!"".equals(endTime)) {
				selectSql+=" and CREATETIME<to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			
			if(courseType!=2) {
				selectSql+=" and courseType="+courseType;
			}
			sql=sql+selectSql+" and rownum<"+endNum+") where rn>="+startNum;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("courseId", rs.getObject(1));
				map.put("courseName", rs.getObject(2));
				map.put("createTime", rs.getObject(3));
				map.put("userId", rs.getObject(4));
				map.put("isUsed", rs.getObject(5));
				map.put("courseType", rs.getObject(6));
				list.add(map);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String getCountSql="select count(*) from ("+selectSql+")";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()) {
				total=rs.getInt(1);
			}
			resultMap.put("rows", list);
			resultMap.put("total", total);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
		
	}
	/**
	 * 查询课程下所有课件
	 * @param courseId
	 * @param courseWare
	 * @param courseWareSub
	 * @param subject
	 * @param startTime
	 * @param endTime
	 * @param createUser
	 * @param num
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> selectCourseWareByCourseId(String courseId,String courseWare,String courseWareSub,String subject,String startTime,String endTime,String createUser,Integer num,Integer pageSize){
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		Map<String,Object> resultMap=new HashMap<String,Object>();
		List<CourseWare> list=new ArrayList<CourseWare>();
		List<String> courseWareIdList=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String selectSql="select COURSEWAREID,COURSEWARETYPE,COURSEWARESUB,SUBJECT,CONTENT,USENUMBER,CREATETIME,USERID,isUsed from (";
			String sql="select COURSEWAREID,COURSEWARETYPE,COURSEWARESUB,SUBJECT,CONTENT,USENUMBER,to_char(CREATETIME,'yyyy-mm-dd hh24:mi:ss') CREATETIME,USERID,isUsed,rownum rn from EM_INF_COURSEWARE a where 1=1 ";
			if(courseWare!=null&&!"".equals(courseWare)){
				sql+="and COURSEWARETYPE='"+courseWare+"' ";
			}
			if(courseWareSub!=null&&!"".equals(courseWareSub)){
				sql+="and COURSEWARESUB='"+courseWareSub+"' ";
			}
			if(subject!=null&&!"".equals(subject)){
				sql+="and SUBJECT like '%"+courseWareSub+"%' ";
			}
			if(startTime!=null&&!"".equals(startTime)){
				sql+="and CREATETIME >to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss') ";
			}
			if(endTime!=null&&!"".equals(endTime)){
				sql+="and CREATETIME <to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss') ";
			}
			if(createUser!=null&&!"".equals(createUser)){
				sql+="and USERID='"+createUser+"' ";
			}
			if(courseId!=null&&!"".equals(courseId)) {
				sql+="and exists(select COURSEWAREID from EM_MAP_COURSE b where b.COURSEID='"+courseId+"' and a.COURSEWAREID=b.COURSEWAREID)";
			}
			selectSql=selectSql+sql+" and rownum<?) where rn>=?";
			pst=conn.prepareStatement(selectSql);
			pst.setInt(1, endNum);
			pst.setInt(2, startNum);
			rs=pst.executeQuery();
			while(rs.next()){
				CourseWare course=new CourseWare();
				course.setCourseWareId(rs.getString(1));
				course.setCourseWare(rs.getString(2));
				course.setCourseWareSub(rs.getString(3));
				course.setSubject(rs.getString(4));
				course.setContent(rs.getString(5));
				course.setUseNumber(rs.getInt(6));
				course.setCreateTime(rs.getString(7));
				course.setCreateUser(rs.getString(8));
				course.setIsUsed(rs.getInt(9));
				list.add(course);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String getCountSql="select count(*) from ("+sql+")";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()) {
				total=rs.getInt(1);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String selectCourseWareSql="select COURSEWAREID from EM_INF_COURSEWARE a where "+
					 "exsits(select COURSEWAREID from EM_MAP_COURSE b where b.COURSEID=? and a.COURSEWAREID=b.COURSEWAREID)";
			pst=conn.prepareStatement(selectCourseWareSql);
			rs=pst.executeQuery();
			while(rs.next()) {
				courseWareIdList.add(rs.getString(1));
			}
			resultMap.put("rows", list);
			resultMap.put("total", total);
			resultMap.put("courseWareIds", courseWareIdList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 新增培训
	 * @param trainName
	 * @param startTime
	 * @param endTime
	 * @param IsUsed
	 * @param userId
	 * @return
	 */
	public Map<String,Object>  insertTrain(String trainName,String startTime,String endTime,Integer isUsed,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		String trainId=idFactory.newId("EX_TR");
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String insertSql="insert into EM_INF_TRAIN(TRAINID,TRAINNAME,CREATETIME,USERID,TRAINSTARTTIME,TRAINENDTIME,ISUSED,ISUPDATE) "
					+ "values (?,?,sysdate,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?)";
			pst=conn.prepareStatement(insertSql);
			pst.setString(1, trainId);
			pst.setString(2, trainName);
			pst.setString(3, userId);
			pst.setString(4, startTime);
			pst.setString(5, endTime);
			pst.setInt(6, isUsed);
			pst.setInt(7, 1);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return resultMap;
	}
	/**
	 * 修改培训
	 * @param trainName
	 * @param startTime
	 * @param endTime
	 * @param IsUsed
	 * @param userId
	 * @param trainId
	 * @return
	 */
	public Map<String,Object>  updateTrain(String trainName,String startTime,String endTime,Integer isUsed,String userId,String trainId){
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String updateSql="update EM_INF_TRAIN set trainName=?,CREATETIME=sysdate,USERID=?,TRAINSTARTTIME=to_date(?,'yyyy-mm-dd hh24:mi:ss'),TRAINENDTIME=to_date(?,'yyyy-mm-dd hh24:mi:ss'),ISUSED=?,ISUPDATE=ISUPDATE+1 where TRAINID=?";
			pst=conn.prepareStatement(updateSql);
			pst.setString(1, trainName);
			pst.setString(2, userId);
			pst.setString(3, startTime);
			pst.setString(4, endTime);
			pst.setInt(5, isUsed);
			pst.setString(6, trainId);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","修改成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","修改失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 给培训选择课程
	 * @param trainId
	 * @param courseIds
	 * @return
	 */
	public Map<String,Object> selectCoursesToTrain(String trainId,String courseIds) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String[] arr=courseIds.split(",");
		try {
			conn=this.getDbConnection();
			String deleteSql="delete from EM_MAP_TRAIN where TRAINID=?";
			pst=conn.prepareStatement(deleteSql);
			pst.setString(1, trainId);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String insertSql="insert into EM_MAP_TRAIN(trainId,COURSEID,SHOWORDER) values(?,?,?)";
			pst=conn.prepareStatement(insertSql);
			int m=1;
			for (int i = 0; i < arr.length; i++) {
				String courseId=arr[i];
				if(courseId==null||"".equals(courseId)) {
					continue;
				}
				pst.setString(1, trainId);
				pst.setString(2, courseId);
				pst.setInt(3, m);
				pst.addBatch();
				m++;
				
			}
			pst.executeBatch();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","保存成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","保存失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return resultMap;
	}
	/**
	 * 删除培训
	 * @param trainIds
	 * @return
	 */
	public Map<String,Object> deleteTrains(String trainIds) {
		Connection conn=null;
		PreparedStatement pst=null;
		String[] arr=trainIds.split(",");
		Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			conn=this.getDbConnection();
			String deleteSql="delete from EM_INF_TRAIN where TRAINID in(";
			for (int i = 0; i < arr.length; i++) {
				String trainId=arr[i];
				if(trainId==null||"".equals(trainId)) {
					continue;
				}
				deleteSql+="'"+trainId+"',";
			}
			deleteSql=deleteSql.substring(0,deleteSql.length()-1)+")";
			pst=conn.prepareStatement(deleteSql);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","删除失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 查询培训
	 * @param trainName
	 * @param isUsed
	 * @param startTime
	 * @param endTime
	 * @param userId
	 * @return
	 */
	public Map<String,Object>  selectTrains(String trainName,Integer isUsed,String startTime,String endTime,String userId,Integer num,Integer pageSize) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		Map<String,Object> resultMap=new HashMap<>();
		List<Map<String,Object>> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String selectSql="select TRAINID,TRAINNAME,CREATETIME,USERID,TRAINSTARTTIME,TRAINENDTIME,ISUSED from (";
			String sql="select TRAINID,TRAINNAME,to_char(CREATETIME,'yyyy-mm-dd hh24:mi:ss') CREATETIME,USERID,to_char(TRAINSTARTTIME,'yyyy-mm-dd hh24:mi:ss') TRAINSTARTTIME,to_char(TRAINENDTIME,'yyyy-mm-dd hh24:mi:ss') TRAINENDTIME,ISUSED,rownum rn from EM_INF_TRAIN where 1=1" ;
			if(trainName!=null&&!"".equals(trainName)) {
				sql+=" and TRAINNAME='"+trainName+"'";
			}
			if(userId!=null&&!"".equals(userId)) {
				sql+=" and userId='"+userId+"'";
			}
			if(isUsed!=2) {
				sql+=" and ISUSED="+isUsed;
			}
			if(startTime!=null&&!"".equals(startTime)) {
				sql+=" and CREATETIME>=to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			if(endTime!=null&&!"".equals(endTime)) {
				sql+=" and CREATETIME<to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			selectSql=selectSql+sql+" and rownum<"+endNum+") where rn>="+startNum;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("trainId", rs.getObject(1));
				map.put("trainName", rs.getObject(2));
				map.put("createTime", rs.getObject(3));
				map.put("userId", rs.getObject(4));
				map.put("startTime", rs.getObject(5));
				map.put("endTime", rs.getObject(6));
				map.put("isUsed", rs.getObject(7));
				list.add(map);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String getCountSql="select count(*) from ("+sql+")";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()) {
				total=rs.getInt(1);
			}
			resultMap.put("rows", list);
			resultMap.put("total", total);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	/**
	 * 查询培训下所有课程
	 * @param trainId
	 * @param courseName
	 * @param isUsed
	 * @param startTime
	 * @param endTime
	 * @param courseType
	 * @param num
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> selectCourseByTrainId(String trainId,String courseName,Integer isUsed,String startTime,String endTime,Integer courseType,Integer num,Integer pageSize) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		Map<String,Object> resultMap=new HashMap<>();
		List<Map<String,Object>> list=new ArrayList<>();
		List<String> courseIdList=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select COUSERID,COURSENAME,CREATETIME,userId,ISUSED,courseType from(";
			String selectSql="select COUSERID,COURSENAME,to_char(CREATETIME,'yyyy-mm-dd hh24:mi:ss') CREATETIME,userId,ISUSED,courseType,rownum rn from EM_INF_COURSE a where 1=1 ";
			if(courseName!=null&&!"".equals(courseName)) {
				selectSql+=" and COURSENAME='"+courseName+"'";
			}
			if(isUsed!=2) {
				selectSql+=" and ISUSED="+isUsed;
			}
			
			if(startTime!=null&&!"".equals(startTime)) {
				selectSql+=" and CREATETIME>=to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			
			if(endTime!=null&&!"".equals(endTime)) {
				selectSql+=" and CREATETIME<to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss')";
			}
			
			if(courseType!=2) {
				selectSql+=" and courseType="+courseType;
			}
			
			if(trainId!=null&&!"".equals(trainId)) {
				selectSql+=" and exists(select COURSEID from EM_MAP_TRAIN b where b.TRAINID='"+trainId+"' and a.COUSERID=b.COURSEID)";
			}
			sql=sql+selectSql+" and rownum<"+endNum+") where rn>="+startNum;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("courseId", rs.getObject(1));
				map.put("courseName", rs.getObject(2));
				map.put("createTime", rs.getObject(3));
				map.put("userId", rs.getObject(4));
				map.put("isUsed", rs.getObject(5));
				map.put("courseType", rs.getObject(6));
				list.add(map);
			}
			DbUtil.DbCloseQuery(rs, pst);
			String getCountSql="select count(*) from ("+selectSql+")";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()) {
				total=rs.getInt(1);
			}
			DbUtil.DbCloseQuery(rs, pst);
			
			String selectCourseSql="select COUSERID from EM_INF_COURSE a where "+
					 "exists(select COURSEID from EM_MAP_TRAIN b where b.TRAINID='"+trainId+"' and a.COUSERID=b.COURSEID)";
			pst=conn.prepareStatement(selectCourseSql);
			rs=pst.executeQuery();
			while(rs.next()) {
				courseIdList.add(rs.getString(1));
			}
			resultMap.put("rows", list);
			resultMap.put("total", total);
			resultMap.put("courseIds", courseIdList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
		
	}
	/**
	 * 选择培训人员
	 * @param trainId
	 * @param userIds
	 */
	public Map<String,Object>  insertCsrByTrainId(String trainId,String userIds) {
		Connection conn=null;
		PreparedStatement pst=null;
		String arr[]=userIds.split(",");
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String sql="insert into EM_INF_TRAINUSER(TRAINID,USERID,DOWNLOADNUM,BROWSENUM) values(?,?,?,?,?)";
			pst=conn.prepareStatement(sql);
			for (int i = 0; i < arr.length; i++) {
				String userId=arr[i];
				if(userId==null||"".equals(userId)) {
					continue;
				}
				pst.setString(1, trainId);
				pst.setString(2, userId);
				pst.setInt(3, 0);
				pst.setInt(4, 0);
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 获取当前用户角色
	 * @param userId
	 * @return
	 */
	public List<String> getRoles(String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<String> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select roleType from BU_INF_ROLE a  where exists (select roleid from Bu_Map_Userorgrole b where userId=? and a.roleid=b.roleid)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, userId);
			rs=pst.executeQuery();
			while(rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=======");
		}finally {
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
}
