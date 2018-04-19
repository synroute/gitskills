package hiapp.modules.exam.srv;

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
import hiapp.system.buinfo.User;

@Controller
public class ExamController {
	@Autowired
	public ExamDao examDao;
	/**
	 * 添加试题
	 * @param request
	 * @param response
	 * @param file
	 */
	@RequestMapping(value="srv/ExamController/insertQuestion.srv")
	public  void insertQuestion(HttpServletRequest request,HttpServletResponse response,MultipartFile file) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String questiondes=request.getParameter("questiondes");
		String questionClass=request.getParameter("questionClass");
		String questionsType=request.getParameter("questionsType");
		String questionType=request.getParameter("questionType");
		String questionLevel=request.getParameter("questionLevel");
		String score=request.getParameter("score");
		String importTime=request.getParameter("importTime");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String anwser=request.getParameter("anwser");
		String filePath="/"+new SimpleDateFormat("yyyyMMdd").format(new Date());
		String fileName=file.getOriginalFilename();
		String ftpPath=filePath+"/"+fileName;
		InputStream in=null;
		Map<String,Object> resultMap=new HashMap<>();
		try {
			in = file.getInputStream();
			boolean flag = FtpUtil.uploadFile(filePath, fileName, in);
			if(flag) {
				resultMap=examDao.insertQuestion(questiondes, questionClass, questionsType, questionType, questionLevel, score, importTime, isUsed, ftpPath, anwser, userId);
			}else {
				resultMap.put("dealSts","02");
				resultMap.put("dealDesc","添加失败");
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
	 * 查询试题类别
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/ExamController/selectQuestionClass.srv")
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
	@RequestMapping(value="srv/ExamController/excelImportQuestion.srv", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
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
			return GsonUtil.getGson().toJson(resultMap);
		}
		
		resultMap=examDao.excelImportQuestion(list, userId);
		
		return GsonUtil.getGson().toJson(resultMap);
	}
	
	@RequestMapping
	public void selectQuestion(HttpServletRequest request,HttpServletResponse response) {
		String questiongnType=request.getParameter("questiongnType");
		String questionLevel=request.getParameter("questionLevel");
		Integer minScore=Integer.valueOf(request.getParameter("minScore"));
		Integer maxScore=Integer.valueOf(request.getParameter("maxScore"));
		String questionType=request.getParameter("questionType");
	}
}
