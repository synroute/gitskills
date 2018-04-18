package hiapp.modules.exam.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	
	public  void  selectQuestionClass() {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			conn=this.getDbConnection();
			String sql="";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
