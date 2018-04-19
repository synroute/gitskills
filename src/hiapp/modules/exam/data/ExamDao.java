package hiapp.modules.exam.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.apache.logging.log4j.Logger;
import hiapp.modules.exam.utils.GsonUtil;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import hiapp.utils.idfactory.IdFactory;
@Repository
public class ExamDao extends BaseRepository{
	@Autowired
	private IdFactory idFactory;
	@Autowired
	private Logger logger;
	
	/**
	 * 添加试题
	 * @param questiondes
	 * @param questionClass
	 * @param questionsType
	 * @param questionType
	 * @param questionLevel
	 * @param score
	 * @param importTime
	 * @param isUsed
	 * @param ftpPath
	 * @param anwser
	 * @param userId
	 * @return
	 */
	public Map<String,Object> insertQuestion(String questiondes,String questionClass,String questionsType,String questionType,String questionLevel,String score,String importTime,Integer isUsed,String ftpPath,String anwser,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		String questionId=idFactory.newId("EX_QS");
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String insertQuestionSql="insert into EM_INF_ANSWER(QUESTIONID,QUESTIONDES,QUESTIONCLASS,QUESTIONSTYLE,QUESTIONTYPE,QUESTIONLEVE,DEFAULSCORE,INPUTTIME,INPUTER,ISUSED,ISUPDATE,FTPATH) values(?,?,?,?,?,?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(insertQuestionSql);
			pst.setString(1, questionId);
			pst.setString(2, questiondes);
			pst.setString(3, questionClass);
			pst.setString(4, questionsType);
			pst.setString(5, questionType);
			pst.setString(6, questionLevel);
			pst.setString(7, score);
			pst.setString(8, userId);
			pst.setInt(9, isUsed);
			pst.setInt(10, 1);
			pst.setString(11, ftpPath);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			List<Map<String,Object>> list=GsonUtil.getGson().fromJson(anwser, List.class);
			String insertAnwserSql="insert into EM_INF_ANSWER(ANSWERID,QUESTIONID,ANSWERSN,ANSWERBODY,ISRIGHT) values(S_EM_INF_ANSWER.NEXTVAL,?,?,?,?)";
			pst=conn.prepareStatement(insertAnwserSql);
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=list.get(i);
				String answerSn=(String) map.get("anwsersn");
				String answerBody=(String) map.get("anwserBody");
				Integer isRight=Integer.valueOf(String.valueOf(map.get("isRight")));
				pst.setString(1, questionId);
				pst.setString(2, answerSn);
				pst.setString(3, answerBody);
				pst.setInt(4, isRight);
				pst.addBatch();
			}
			pst.executeBatch();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	public Map<String,Object> excelImportQuestion(List<Map<Integer,Object>> list,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		PreparedStatement pst1=null;
		Map<String,Object> resultMap=new HashMap<>();
		List<String> questIdList=idFactory.newIds("EX_QS", list.size());
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String insertQuestionSql="insert into EM_INF_ANSWER(QUESTIONID,QUESTIONDES,QUESTIONCLASS,QUESTIONSTYLE,QUESTIONTYPE,QUESTIONLEVE,DEFAULSCORE,INPUTTIME,INPUTER,ISUSED,ISUPDATE,FTPATH) values(?,?,?,?,?,?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(insertQuestionSql);
			String insertAnwserSql="insert into EM_INF_ANSWER(ANSWERID,QUESTIONID,ANSWERSN,ANSWERBODY,ISRIGHT) values(S_EM_INF_ANSWER.NEXTVAL,?,?,?,?)";
			pst1=conn.prepareStatement(insertAnwserSql);
			for (int i = 0; i < list.size(); i++) {
				Map<Integer,Object> map=list.get(i);
				String questionId=questIdList.get(i);
				pst.setObject(1,questionId);
				pst.setObject(2,map.get(0));
				pst.setObject(3,map.get(1));
				pst.setObject(4,map.get(2));
				pst.setObject(5,map.get(3));
				pst.setObject(6,map.get(4));
				pst.setObject(7,map.get(5));
				pst.setObject(8,userId);
				pst.setObject(9, map.get(6));
				pst.setInt(10,1);
				pst.setString(11, "");
				String anWsers=(String) map.get(7);
				String[] arr=anWsers.split(";".trim());
				for (int j = 0; j < arr.length; j++) {
					String anwser=arr[i];
					if(anwser==null||"".equals(anwser)) {
						continue;
					}
					String answerSn=anwser.substring(0, anwser.lastIndexOf("."));
					String answerBody=anwser.substring(anwser.lastIndexOf(".")+1,anwser.lastIndexOf(","));
					Integer isRight=Integer.valueOf(String.valueOf(anwser.substring(anwser.lastIndexOf(",")+1)));
					pst1.setString(1, questionId);
					pst1.setString(2, answerSn);
					pst1.setString(3, answerBody);
					pst1.setInt(4, isRight);
					pst1.addBatch();	
				}
					pst.addBatch();
			}
			
			pst1.executeBatch();
			pst.executeBatch();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","添加成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
		}finally {
			DbUtil.DbCloseExecute(pst1);
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return  resultMap;
	}
	
	/**
	 * 查询试题类别
	 * @return
	 */
	public  List<Map<String,Object>>  selectQuestionClass() {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<Map<String,Object>> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select itemId,itemTxt from hasys_dic_item where dicId=131 and ITEMPARENT=-1";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("typeId", rs.getObject(1));
				map.put("typeName", rs.getObject(2));
				list.add(map);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
	public void selectQuestion(String questiongnType,String questionLevel,Integer minScore,Integer maxScore,String questionType,Integer num,Integer pageSize) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		List<Map<String,Object>> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select QUESTIONID,QUESTIONSTYLE,QUESTIONTYPE,DEFAULSCORE from (";
			String selectSql="select QUESTIONID,QUESTIONSTYLE,QUESTIONTYPE,DEFAULSCORE,rownum rn from EM_INF_ANSWER where 1=1";
			if(questiongnType!=null&&!"".equals(questiongnType)) {
				selectSql+=" and QUESTIONTYPE='"+questiongnType+"'";
			}
			
			if(questionLevel!=null&&!"".equals(questionLevel)) {
				selectSql+=" and questionLevel='"+questionLevel+"'";
			}
			if(minScore!=null) {
				selectSql+=" and DEFAULSCORE >="+minScore;
			}
			if(maxScore!=null) {
				selectSql+=" and DEFAULSCORE <"+maxScore;
			}
			if(questionType!=null&&!"".equals(questionType)) {
				selectSql+=" and QUESTIONCLASS="+questionType;
			}
			sql=sql+selectSql+" and rownum<"+endNum+") where rn>="+startNum;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
