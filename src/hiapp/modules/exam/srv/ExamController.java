package hiapp.modules.exam.srv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import hiapp.modules.exam.data.ExamDao;
import hiapp.modules.exam.utils.FtpUtil;
import hiapp.modules.exam.utils.GsonUtil;
import hiapp.modules.exam.utils.OfficeToPdfUtil;
import hiapp.system.buinfo.User;

@Controller
public class ExamController {
	@Autowired
	public ExamDao examDao;
	/**
	 * 添加或修改试题
	 * @param requestes
	 * @param response
	 * @param file
	 */
	@RequestMapping(value="/srv/ExamController/insertOrUpdateQuestion.srv")
	public  void insertOrUpdateQuestion(HttpServletRequest request,HttpServletResponse response,MultipartFile file) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String questionId=request.getParameter("questionId");
		String questiondes=request.getParameter("questiondes");
		String questionClass=request.getParameter("questionClass");
		String questionsType=request.getParameter("questionsType");
		String questionType=request.getParameter("questionType");
		String questionLevel=request.getParameter("questionLevel");
		String score=request.getParameter("score");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String anwser=request.getParameter("anwser");
		
		InputStream in=null;
		Map<String,Object> resultMap=new HashMap<>();
		try {
			String ftpPath="";
			String fileName=file.getOriginalFilename();
			if(file!=null) {
				String filePath="/"+new SimpleDateFormat("yyyyMMdd").format(new Date());
				ftpPath=filePath+"/"+fileName;
				in = file.getInputStream();
				boolean flag = FtpUtil.uploadFile(filePath, fileName, in);
				if(flag) {
					if(questionId==null||"".equals(questionId)) {
						resultMap=examDao.insertQuestion(questiondes, questionClass, questionsType, questionType, questionLevel, score,  isUsed, ftpPath, anwser, userId);
					}else {
						resultMap=examDao.updateQuestion(questionId, questiondes, questionClass, questionsType, questionType, questionLevel, score, isUsed, ftpPath, anwser, userId);
					}
				}else {
					resultMap.put("dealSts","02");
					resultMap.put("dealDesc","上传失败");
				}
			}

			String jsonObject=new Gson().toJson(resultMap);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 删除试题
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/ExamController/deleteQuestion.srv",method= RequestMethod.POST)
	public void deleteQuestion(HttpServletRequest request,HttpServletResponse response) {
		String questionIds=request.getParameter("questionIds");
		Map<String, Object> resultMap = examDao.deleteQuestions(questionIds);
		try {
			String jsonObject=new Gson().toJson(resultMap);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询试题类别
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/ExamController/selectQuestionClass.srv")
	public void selectQuestionClass(HttpServletRequest request,HttpServletResponse response) {
		List<Map<String, Object>> resultList = examDao.selectQuestionClass();
		try {
			String jsonObject=new Gson().toJson(resultList);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * EXCEL导入试题
	 * @param request
	 * @param response
	 * @param file
	 * @return
	 */
	@RequestMapping(value="/srv/ExamController/excelImportQuestion.srv", method = RequestMethod.POST, produces = "application/json")
	public String excelImportQuestion(HttpServletRequest request,HttpServletResponse response,@RequestParam("file") MultipartFile file) {
		String fileName=file.getOriginalFilename();
		List<Map<Integer,Object>> list=new ArrayList<Map<Integer,Object>>();
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		Map<String,Object> resultMap=new HashMap<>();
		try {
			InputStream in = file.getInputStream();
			Workbook wookbook =null;
			String suffix=fileName.substring(fileName.indexOf("."));//获取后缀名
			if(".xls".equals(suffix)){ 
				wookbook = new HSSFWorkbook(in);
			}else if(".xlsx".equals(suffix)){
				wookbook = new XSSFWorkbook(in);
			}
			Sheet sheet = wookbook.getSheetAt(0);
			int totalRowNum = sheet.getLastRowNum()+1; //总行数
			int coloumNum=sheet.getRow(0).getPhysicalNumberOfCells();//总列数
			for (int i = 1; i < totalRowNum; i++) {
				 Row row = sheet.getRow(i);
				 Map<Integer,Object> map=new HashMap<>();
				 for (int j = 0; j <coloumNum+1; j++) {
					Cell cell=row.getCell(j);
					String cellValue="";
					if(cell!=null) {
						String value=GsonUtil.getStringcell(cell);
						if(value!=null){
							cellValue=value;
	            		}
					}
					map.put(j, cellValue);
				}
				 
				list.add(map);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("dealSts","02");
			resultMap.put("dealDesc","添加失败");
			return new Gson().toJson(resultMap);
		}
		
		resultMap=examDao.excelImportQuestion(list, userId);
		
		return new Gson().toJson(resultMap);
	}
	
	/**
	 * 查询试题
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/srv/ExamController/selectQuestion.srv", method = RequestMethod.POST, produces = "application/json")
	public String selectQuestion(HttpServletRequest request,HttpServletResponse response) {
		String questiongnType=request.getParameter("questiongnType");
		String questionLevel=request.getParameter("questionLevel");
		Integer minScore=Integer.valueOf(request.getParameter("minScore"));
		Integer maxScore=Integer.valueOf(request.getParameter("maxScore"));
		String questionType=request.getParameter("questionType");
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = examDao.selectQuestion(questiongnType, questionLevel, minScore, maxScore, questionType, num, pageSize);
		return new Gson().toJson(resultMap);
	}
	
	/**
	 * 添加或修改考试信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/srv/ExamController/insertOrUpdateExam.srv", method = RequestMethod.POST, produces = "application/json")
	public String insertExam(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String examId=request.getParameter("examId");
		String examName=request.getParameter("examName");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		Integer passLine=Integer.valueOf(request.getParameter("passLine"));
		Integer minLine=Integer.valueOf(request.getParameter("minLine"));
		Integer excellentLine=Integer.valueOf(request.getParameter("excellentLine"));
		Integer examType=Integer.valueOf(request.getParameter("examType"));
		Map<String, Object> resultMap=null;
		if(examId!=null&&!"".equals(examId)) {
			resultMap=examDao.updateExam(examId, examName, startTime, endTime, isUsed, passLine, minLine, excellentLine, examType, userId);
		}else {
			resultMap = examDao.insertExam(examName, startTime, endTime, isUsed, passLine, minLine, excellentLine, examType, userId);
		}
		return new Gson().toJson(resultMap);
	}
	/**	
	 * 给考试选择试题
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/srv/ExamController/insertQuestionForExam.srv", method = RequestMethod.POST, produces = "application/json")
	public String insertQuestionForExam(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String examId=request.getParameter("examId");
		String questions=request.getParameter("questions");
		Map<String, Object> resultMap = examDao.insertQuestionForExam(examId, questions, userId);
		return new Gson().toJson(resultMap);
	}
	/**
	 * 查询试卷信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/srv/ExamController/selectExamInfo.srv",method = RequestMethod.POST, produces = "application/json")
	public String selectExamInfo(HttpServletRequest request,HttpServletResponse response) {
		String examId=request.getParameter("examId");
		String examPath=request.getSession().getServletContext().getRealPath("/exam");
		File file=new File(examPath);
		if(file.exists()) {
			OfficeToPdfUtil.delAllFile(examPath);
		}else {
			file.mkdirs();
		}
		Map<String, Object> resultMap = examDao.selectExamInfo(examId, examPath);
		return new Gson().toJson(resultMap);
	}
	
	
	
}
