package hiapp.modules.exam.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import hiapp.modules.exam.bean.ExamStatus;
import hiapp.modules.exam.utils.FtpUtil;
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
	 * @param isUsed
	 * @param ftpPath
	 * @param anwser
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> insertQuestion(String questiondes,String questionClass,String questionsType,String questionType,String questionLevel,String score,Integer isUsed,String ftpPath,String anwser,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		String questionId=idFactory.newId("EX_QS");
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String insertQuestionSql="insert into EM_INF_QUESTIONBASE(QUESTIONID,QUESTIONDES,QUESTIONCLASS,QUESTIONSTYLE,QUESTIONTYPE,QUESTIONLEVE,DEFAULSCORE,INPUTTIME,INPUTER,ISUSED,ISUPDATE,FTPATH) values(?,?,?,?,?,?,?,sysdate,?,?,?,?)";
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
			List<Map<String,Object>> list=new Gson().fromJson(anwser, List.class);
			String insertAnwserSql="insert into EM_INF_ANSWER(ANSWERID,QUESTIONID,ANSWERSN,ANSWERBODY,ISRIGHT) values(S_EM_INF_ANSWER.NEXTVAL,?,?,?,?)";
			pst=conn.prepareStatement(insertAnwserSql);
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=list.get(i);
				String answerSn=String.valueOf(map.get("anwsersn"));
				String answerBody=String.valueOf(map.get("anwserBody"));
				Integer isRight=GsonUtil.getIntegerValue(String.valueOf(map.get("isRight")));
				pst.setString(1, questionId);
				pst.setString(2, answerSn);
				pst.setString(3, answerBody);
				pst.setInt(4, isRight);
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
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
	/**
	 * 修改试题
	 * @param questionId
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
	public Map<String,Object>  updateQuestion(String questionId,String questiondes,String questionClass,String questionsType,String questionType,String questionLevel,String score,Integer isUsed,String ftpPath,String anwser,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<>();
	
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String updateSql="update EM_INF_QUESTIONBASE set QUESTIONDES=?,QUESTIONCLAS=?,QUESTIONSTYLE=?,QUESTIONTYPE=?,QUESTIONLEVE=?,DEFAULSCORE=?,INPUTTIME=sysdate,INPUTER=?,ISUSED=?,ISUPDATE=ISUPDATE+1";
			if(!"".equals(ftpPath)) {
				updateSql+=",ftpath='"+ftpPath+"'";
			}
			updateSql+= " where QUESTIONID=?";
			pst=conn.prepareStatement(updateSql);
			pst.setString(1, questiondes);
			pst.setString(2, questionClass);
			pst.setString(3, questionsType);
			pst.setString(4, questionType);
			pst.setString(5, questionLevel);
			pst.setString(6, score);
			pst.setString(7, userId);
			pst.setInt(8, isUsed);
			pst.setInt(9, 1);
			pst.setString(10, questionId);
			pst.executeUpdate();
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			List<Map<String,Object>> list=GsonUtil.getGson().fromJson(anwser,  new TypeToken<List<Map<String,Object>>>(){}.getType());
			String deleteSql="delete from EM_INF_ANSWER where QUESTIONID=?";
			pst=conn.prepareStatement(deleteSql);
			pst.setString(1, questionId);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String insertAnwserSql="insert into EM_INF_ANSWER(ANSWERID,QUESTIONID,ANSWERSN,ANSWERBODY,ISRIGHT) values(S_EM_INF_ANSWER.NEXTVAL,?,?,?,?)";
			pst=conn.prepareStatement(insertAnwserSql);
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=list.get(i);
				String answerSn=String.valueOf(map.get("anwsersn"));
				String answerBody=String.valueOf(map.get("anwserBody"));
				Integer isRight=Integer.valueOf(String.valueOf(map.get("isRight")));
				pst.setString(1, questionId);
				pst.setString(2, answerSn);
				pst.setString(3, answerBody);
				pst.setInt(4, isRight);
				pst.addBatch();
			}
			pst.executeBatch();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","修改成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","修改失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return  resultMap;
	}
	
	/**
	 * 删除问题
	 * @param questionIds
	 * @return
	 */
	public  Map<String,Object>  deleteQuestions(String questionIds) {
		Connection conn=null;
		PreparedStatement pst=null;
		String[] arr=questionIds.split(",");
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String deleteQuestionSql="delete from EM_INF_EMQUESTIONBASE where QUESTIONID=in(";
			String deleteAnswerSql="delete from EM_INF_ANSWER where QUESTIONID in(";
			for (int i = 0; i < arr.length; i++) {
				String questionId=arr[i];
				if(questionId==null&&"".equals(questionId)) {
					continue;
				}
				deleteQuestionSql+="'"+questionId+"',";
				deleteAnswerSql+="'"+questionId+"',";
			}
			deleteQuestionSql=deleteQuestionSql.substring(0,deleteQuestionSql.length()-1)+")";
			deleteAnswerSql=deleteAnswerSql.substring(0,deleteAnswerSql.length()-1)+")";
			pst=conn.prepareStatement(deleteQuestionSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			pst=conn.prepareStatement(deleteAnswerSql);
			pst.executeUpdate();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","删除成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","删除成功");
		}
		
		return resultMap;
	}
	/**
	 * EXCEL导入题库
	 * @param list
	 * @param userId
	 * @return
	 */
	public Map<String,Object> excelImportQuestion(List<Map<Integer,Object>> list,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		PreparedStatement pst1=null;
		Map<String,Object> resultMap=new HashMap<>();
		List<String> questIdList=idFactory.newIds("EX_QS", list.size());
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String insertQuestionSql="insert into EM_INF_QUESTIONBASE(QUESTIONID,QUESTIONDES,QUESTIONCLASS,QUESTIONSTYLE,QUESTIONTYPE,QUESTIONLEVE,DEFAULSCORE,INPUTTIME,INPUTER,ISUSED,ISUPDATE,FTPATH) values(?,?,?,?,?,?,?,sysdate,?,?,?,?)";
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
				String anWsers=String.valueOf(map.get(7));
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
			String sql="select itemId,itemText from hasys_dic_item where dicId=125 and ITEMPARENT=-1";
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
			logger.info(e+"=========================");
		}finally {
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
	/**
	 * 查询题库
	 * @param questiongnType
	 * @param questionLevel
	 * @param minScore
	 * @param maxScore
	 * @param questionType
	 * @param num
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> selectQuestion(String questiongnType,String questionLevel,Integer minScore,Integer maxScore,String questionType,Integer num,Integer pageSize) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Integer startNum=(num-1)*pageSize+1;
		Integer endNum=num*pageSize+1;
		Map<String,Object> resultMap=new HashMap<>();
		List<Map<String,Object>> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select QUESTIONID,QUESTIONSTYLE,QUESTIONLEVE,DEFAULSCORE,QUESTIONCLASS,QUESTIONTYPE,QUESTIONDES,ISUSED,INPUTER,ftPath from (";
			String selectSql="select QUESTIONID,QUESTIONSTYLE,QUESTIONLEVE,DEFAULSCORE,QUESTIONCLASS,QUESTIONTYPE,QUESTIONDES,ISUSED,INPUTER,ftPath,rownum rn from EM_INF_QUESTIONBASE where 1=1";
			if(questiongnType!=null&&!"".equals(questiongnType)) {
				selectSql+=" and QUESTIONSTYLE='"+questiongnType+"'";
			}
			
			if(questionLevel!=null&&!"".equals(questionLevel)) {
				selectSql+=" and QUESTIONLEVE='"+questionLevel+"'";
			}
			if(minScore!=-1) {
				selectSql+=" and DEFAULSCORE >="+minScore;
			}
			if(maxScore!=-1) {
				selectSql+=" and DEFAULSCORE <"+maxScore;
			}
			if(questionType!=null&&!"".equals(questionType)) {
				selectSql+=" and QUESTIONCLASS='"+questionType+"'";
			}
			sql=sql+selectSql+" and rownum<"+endNum+") where rn>="+startNum;
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("questionId", rs.getObject(1));
				map.put("questingnType", rs.getObject(2));
				map.put("questionLevel", rs.getObject(3));
				map.put("score", rs.getObject(4));
				map.put("questionClass", rs.getObject(5));
				map.put("questionType", rs.getObject(6));
				map.put("questionDes",  rs.getObject(7));
				if(rs.getInt(8)==0) {
					map.put("isUsed","启用");
				}else {
					map.put("isUsed","停用");
				}
				map.put("userId",  rs.getObject(9));
				list.add(map);
			}
			DbUtil.DbCloseQuery(rs,pst);
			String getCountSql="select count(1) from ("+selectSql+")";
			pst=conn.prepareStatement(getCountSql);
			rs=pst.executeQuery();
			Integer total=0;
			while(rs.next()) {
				total=rs.getInt(1);
			}
			resultMap.put("rows", list);
			resultMap.put("total",total);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
		}finally {
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	public List<Map<String,Object>> getAnswerByQuestionId(String questionId) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<Map<String,Object>> list=new ArrayList<>();
		try {
			conn=this.getDbConnection();
			String sql="select ANSWERSN,ANSWERBODY,ISRIGHT from em_inf_answer where ANSWERID=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, questionId);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("anwserSn",rs.getObject(1));
				map.put("anwserBody", rs.getObject(2));
				if(rs.getInt(3)==1) {
					map.put("isRight", "是");
				}else {
					map.put("isRight", "否");
				}
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
		}finally {
			DbUtil.DbCloseQuery(rs,pst);
			DbUtil.DbCloseConnection(conn);
		}
		return list;
	}
	/**
	 * 添加考试信息
	 * @param examName
	 * @param startTime
	 * @param endTime
	 * @param isUsed
	 * @param passLine
	 * @param minLine
	 * @param excellentLine
	 * @param examType
	 * @param userId
	 * @return
	 */
	public Map<String,Object> insertExam(String examName,String startTime,String endTime,Integer isUsed,Integer passLine,Integer minLine,Integer excellentLine,Integer examType,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		String examId=idFactory.newId("EX_AM");
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			String sql="insert into EM_INF_EXAMINATION (EXAMINATIONID,EXAMINATIONNAME,STARTTIME,ENDTIME,ISUSED,CREATER,CREATTIME,PASSLINE,MEDIUMLINE,EXCELLENTLINE,EXAMTYPE) values(?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'),?,?,sysdate,?,?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, examId);
			pst.setString(2, examName);
			pst.setString(3, startTime);
			pst.setString(4, endTime);
			pst.setInt(5, isUsed);
			pst.setString(6, userId);
			pst.setInt(7, passLine);
			pst.setInt(8, minLine);
			pst.setInt(9, excellentLine);
			pst.setInt(10, examType);
			pst.executeUpdate();
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
	/**
	 * 修改考试
	 * @param examId
	 * @param examName
	 * @param startTime
	 * @param endTime
	 * @param isUsed
	 * @param passLine
	 * @param minLine
	 * @param excellentLine
	 * @param examType
	 * @param userId
	 * @return
	 */
	public Map<String,Object> updateExam(String examId,String examName,String startTime,String endTime,Integer isUsed,Integer passLine,Integer minLine,Integer excellentLine,Integer examType,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			String updateSql="update EM_INF_EXAMINATION set EXAMINATIONNAME=?,STARTTIME=to_date(?,'yyyy-mm-dd hh24:mi:ss'),endTime=to_date(?,'yyyy-mm-dd hh24:mi:ss'),ISUSED=?,CREATER=?,CREATTIME=sysdate,PASSLINE=?,"+
							 "MEDIUMLINE=?,EXCELLENTLINE=?,EXAMTYPE=? where EXAMINATIONID=?";
			pst=conn.prepareStatement(updateSql);
			pst.setString(1, examName);
			pst.setString(2, startTime);
			pst.setString(3, endTime);
			pst.setInt(4, isUsed);
			pst.setString(5, userId);
			pst.setInt(6, passLine);
			pst.setInt(7, minLine);
			pst.setInt(8, excellentLine);
			pst.setInt(9, examType);
			pst.setString(10, examId);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","修改成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","修改失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return resultMap;
	}

	
	public void selectExam(String examName,Integer isUsed,String createUser,String createTime,Integer examType) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			conn=this.getDbConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 给考试选择试题
	 * @param examId
	 * @param questions
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> insertQuestionForExam(String examId,String questions,String userId) {
		Connection conn=null;
		PreparedStatement pst=null;
		List<Map<String,Object>> list=GsonUtil.getGson().fromJson(questions,List.class);
		List<String> examationIdList=idFactory.newIds("EX_KS", list.size());
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String questionIds="";
			String useScores="";
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=list.get(i);
				String questionId=String.valueOf(map.get("questionId"));
				String useScore=String.valueOf(map.get("useScore"));
				questionIds+=questionId+",";
				useScores+=useScore+",";
			}
			questionIds=questionIds.substring(0,questionIds.length()-1).trim();
			useScores=useScores.substring(0,useScores.length()-1).trim();
			String examationIds=StringUtils.join(examationIdList.toArray(), ",");
			String deleteAnwserSql="delete from EM_INF_EMANSWER a where exists (select b.EMQUESTIONID from EM_INF_EMQUESTION b where a.QUESTIONID=b.EMQUESTIONID and b.EXAMINATIONID='"+examId+"')";
			pst=conn.prepareStatement(deleteAnwserSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String deleteEmSql="delete from EM_INF_EMQUESTIONBASE a where exists (select b.EMQUESTIONID from EM_INF_EMQUESTION b where a.QUESTIONID=b.EMQUESTIONID and b.EXAMINATIONID='"+examId+"')";
			pst=conn.prepareStatement(deleteEmSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String deleteEmQuestionSql="delete from EM_INF_EMQUESTION where EXAMINATIONID='"+examId+"'";
			pst=conn.prepareStatement(deleteEmQuestionSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String insertEmQuestionSql="insert into EM_INF_EMQUESTION(EMQUESTIONID,EXAMINATIONID,QUESTIONID,DEFAULSCORE,USESCORE) select b.examationId,'"+examId+"',b.questionId,a.DEFAULSCORE,b.useScore from EM_INF_QUESTIONBASE a right join (select "+
									   "REGEXP_SUBSTR ('"+examationIds+"', '[^,]+', 1,rownum) examationId,REGEXP_SUBSTR ('"+questionIds+"', '[^,]+', 1,rownum) questionId,REGEXP_SUBSTR ('"+useScores+"', '[^,]+', 1,rownum) useScore from dual "+
									   "CONNECT BY ROWNUM <=LENGTH ('"+examationIds+"') - LENGTH (REPLACE ('"+examationIds+"', ',', ''))+1) b on a.QUESTIONID=b.questionId";
			pst=conn.prepareStatement(insertEmQuestionSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String insertEmSql="insert into EM_INF_EMQUESTIONBASE(QUESTIONID,QUESTIONBODY,QUESTIONTYPENAME,QUESTIONBODYTYPENAME,QUESTIONLEVENAME,DEFAULSCORE,INPUTTIME,INPUTER,ISUSED,ISUPDATE,FTPATH) "+
							 "select a.examationId,b.QUESTIONDES,b.QUESTIONCLASS,b.QUESTIONTYPE,b.QUESTIONLEVE,a.DEFAULSCORE,sysdate,'"+userId+"',b.ISUSED,1,b.FTPATH from EM_INF_EMQUESTION a left join EM_INF_QUESTIONBASE b on a.QUESTIONID=b.QUESTIONID where a.EXAMINATIONID='"+examId+"'";
			
			pst=conn.prepareStatement(insertEmSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			String insertAnwserSql="insert into EM_INF_EMANSWER(ANSWERID,QUESTIONID,ANSWERSN,ANSWERBODY,ISRIGHT) select S_EM_INF_ANSWER.NEXTVAL,b.EMQUESTIONID,a.ANSWERSN,a.ANSWERBODY,a.ISRIGHT from EM_INF_ANSWER a right join EM_INF_EMQUESTION b on a.QUESTIONID=b.QUESTIONID where b.EXAMINATIONID='"+examId+"'";
			pst=conn.prepareStatement(insertAnwserSql);
			pst.executeUpdate();
			DbUtil.DbCloseExecute(pst);
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","保存成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(e+"=========================");
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","保存失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return resultMap;
	} 
	/**
	 * 查询试题
	 * @param examId
	 * @param examPath
	 * @return
	 */
	public Map<String,Object> selectExamInfo(String examId,String examPath) {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		List<Map<String,Object>> questionList=new ArrayList<>();
		List<Map<String,Object>> anwserList=new ArrayList<>();
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
		
			String getQuestionSql="select a.QUESTIONID,a.QUESTIONBODY,a.QUESTIONTYPENAME,a.QUESTIONBODYTYPENAME,a.QUESTIONSTYLENAME,a.QUESTIONLEVENAME,a.ftpPath,b.USESCORE from EM_INF_EMQUESTIONBASE a right join EM_INF_EMQUESTION b on a.QUESTIONID=b.EMQUESTIONID where b.EXAMINATIONID='"+examId+"'";
			pst=conn.prepareStatement(getQuestionSql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("questionId", rs.getObject(1));
				map.put("questionBody", rs.getObject(2));
				map.put("questionType", rs.getObject(3));
				map.put("questionBodyType", rs.getObject(4));
				map.put("quesType", rs.getObject(5));
				map.put("questionLevel", rs.getObject(6));
				map.put("ftpPath", rs.getObject(7));
				map.put("score", rs.getObject(8));
				questionList.add(map);
			}
			DbUtil.DbCloseQuery(rs, pst);
			for (int i = 0; i < questionList.size(); i++) {
				Map<String,Object> map=questionList.get(i);
				String questionId=String.valueOf(map.get("questionId"));
				String ftpPath=String.valueOf(map.get("ftpPath"));
				if(ftpPath!=null&&!"".equals(ftpPath)) {
					String newName = FtpUtil.getExamFile(ftpPath, examPath, questionId);
					map.put("ftpPath","/exam/"+newName);
				}
			}
			String getAnwserSql="select a.ANSWERID,a.QUESTIONID,a.ANSWERSN,a.ANSWERBODY,a.ISRIGHT from EM_INF_ANSWER a right join EM_INF_EMQUESTION b where a.QUESTIONID=b.EMQUESTIONID where EXAMINATIONID='"+examId+"' order by ANSWERSN asc";
			pst=conn.prepareStatement(getAnwserSql);
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String,Object> map=new HashMap<>();
				map.put("anwserId", rs.getObject(1));
				map.put("questionId", rs.getObject(2));
				map.put("anwsern", rs.getObject(3));
				map.put("anwserBody", rs.getObject(4));
				map.put("isRight", rs.getObject(5));
				anwserList.add(map);
			}
			resultMap.put("questionList", questionList);
			resultMap.put("anwserList", anwserList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			DbUtil.DbCloseQuery(rs, pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	/**
	 * 选择考生
	 * @param examId
	 * @param examUserIds
	 * @param invigilateUsers
	 */
	public Map<String,Object> selectExamUser(String examId,String examUserIds,String invigilateUsers) {
		Connection conn=null;
		PreparedStatement pst=null;
		PreparedStatement pst1=null;
		String[] userArr=examUserIds.split(",");
		String[] invigilateArr=invigilateUsers.split(",");
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String insertSql="insert into EM_INF_EMALLOT(EXAMINATIONID,EXAMINEETYPE,EXAMINEEID) values(?,?,?)";
			pst=conn.prepareStatement(insertSql);
			String insertExamUserSql="insert into EM_INF_EMPAPER(EXAMINATIONID,EXAMINEEID,EMSTATUS) values(?,?,?)";
			pst1=conn.prepareStatement(insertExamUserSql);
			for (int i = 0; i < userArr.length; i++) {
				String examUserId=userArr[i];
				if(examUserId==null||"".equals(examUserId)) {
					continue;
				}
				pst.setString(1, examId);
				pst.setInt(2, 0);
				pst.setString(3, examUserId);
				pst.addBatch();
				
				pst1.setString(1, examId);
				pst1.setString(2, examUserId);
				pst1.setString(3, ExamStatus.NOEXAM.getName());
				pst1.addBatch();
			}
			
			for (int i = 0; i < invigilateArr.length; i++) {
				String invigilateUser=invigilateArr[i];
				if(invigilateUser==null||"".equals(invigilateUser)) {
					continue;
				}
				pst.setString(1, examId);
				pst.setInt(2, 1);
				pst.setString(3, invigilateUser);
				pst.addBatch();
			}
			
			pst.executeBatch();
			pst1.executeBatch();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","保存成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("selectExamUser",e);
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","保存失败");
		}finally {
			DbUtil.DbCloseExecute(pst1);
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return resultMap;
	}
	public Map<String,Object> updateEaxmStausForExaming(String examUserId,String examId) {
		Connection conn=null;
		PreparedStatement pst=null;
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			String sql="update EM_INF_EMPAPER set EMSTATUS=?,LOGINTIME=sysdate where EXAMINATIONID=? and EXAMINEEID=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, ExamStatus.EXAMING.getName());
			pst.setString(2, examId);
			pst.setString(3, examUserId);
			pst.executeUpdate();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","更新成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("updateEaxmStausForExaming",e);
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","更新失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		return resultMap;
	}
	
	

	
	public Map<String,Object> saveEaxmScroe(String examInfo,Integer totalScore,String examUserId,String examId,String examStatus,String reason) {
		Connection conn=null;
		PreparedStatement pst=null;
		List<Map<String,Object>> list=GsonUtil.getGson().fromJson(examInfo,new TypeToken<List<Map<String,Object>>>(){}.getType());
		Map<String,Object> resultMap=new HashMap<>();
		try {
			conn=this.getDbConnection();
			conn.setAutoCommit(false);
			String insertSql="insert into EM_INF_EMSCORES(SCOREID,EMSN,EXAMINEEID,EXAMINATIONID,QUESTIONID,SCORE) values(S_EM_INF_EMSCORES.nextval,?,?,?,?,?)";
			pst=conn.prepareStatement(insertSql);
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=new HashMap<>();
				Integer emsn=Integer.valueOf(String.valueOf(map.get("emsn")));
				String questionId=String.valueOf(map.get("questionId"));
				Integer score=Integer.valueOf(String.valueOf(map.get("score")));
				
				pst.setInt(1, emsn);
				pst.setString(2, examUserId);
				pst.setString(3, examId);
				pst.setString(4, questionId);
				pst.setInt(5, score);
				pst.addBatch();
			}
			
			pst.executeBatch();
			DbUtil.DbCloseExecute(pst);
			String sql="update EM_INF_EMPAPER set EMSTATUS=?,SCORES=?,FORCEREASON=?,SUBMITTIME=sysdate where EXAMINATIONID=? and EXAMINEEID=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, examStatus);
			pst.setInt(2, totalScore);
			pst.setString(3, reason);
			pst.setString(4, examId);
			pst.setString(5, examUserId);
			pst.executeUpdate();
			conn.commit();
			resultMap.put("dealSts","01");
			resultMap.put("dealDesc","保存成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("saveEaxmScroe",e);
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","保存失败");
		}finally {
			DbUtil.DbCloseExecute(pst);
			DbUtil.DbCloseConnection(conn);
		}
		
		return  resultMap;
	}
}
