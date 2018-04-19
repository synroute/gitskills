package hiapp.modules.exam.srv;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import hiapp.modules.exam.data.ExamDao;
import hiapp.modules.exam.utils.FtpUtil;
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
	
	
}
