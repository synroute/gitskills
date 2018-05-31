package hiapp.modules.exam.srv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import hiapp.modules.exam.data.TrainDao;
import hiapp.modules.exam.utils.ConverPPTFileToImageUtil;
import hiapp.modules.exam.utils.FtpUtil;
import hiapp.modules.exam.utils.GsonUtil;
import hiapp.modules.exam.utils.OfficeToPdfUtil;
import hiapp.modules.exam.utils.WordToHtml;
import hiapp.system.buinfo.User;

@RestController
public class TrainController {
	@Autowired
	private TrainDao trainDao;
	/**
	 * 查询课件类别
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/getCourseWareType.srv")
	public void getCourseWareType(HttpServletRequest request,HttpServletResponse response){
		String typeId=request.getParameter("typeId");
		List<Map<String, Object>> courseWareTypeList = trainDao.getCourseWareType(typeId);
		String jsonObject=new Gson().toJson(courseWareTypeList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 新增或修改课件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/insertOrUpdateCourseWare.srv")
	public void insertOrUpdateCourseWare(HttpServletRequest request,HttpServletResponse response,@RequestParam MultipartFile[] file){
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String courseWare=request.getParameter("courseWare");
		String courseWareSub=request.getParameter("courseWareSub");
		String subject=request.getParameter("subject");
		String content=request.getParameter("content");
		String courseWareId=request.getParameter("courseWareId");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String path="/"+new SimpleDateFormat("yyyyMMdd").format(new Date());
		String address="";
		InputStream in=null;
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String names="";
		try {
			boolean flag=true;
			if(file!=null&&file.length>0) {
				for (int i = 0; i < file.length; i++) {
					String fileName=file[i].getOriginalFilename();
					names+=fileName+",";
					in = file[i].getInputStream();
					address+=path+ "/"+fileName+",";
					boolean uploadResult = FtpUtil.uploadFile( path, fileName, in);
					if(!uploadResult) {
						flag=false;
						break;
					}
				}
				names=names.substring(0,names.length()-1);
			}
			
			if(flag){
				if(courseWareId!=null&&!"".equals(courseWareId)) {
					resultMap=trainDao.updateCourseWare(courseWareId, courseWare, courseWareSub, subject, content, isUsed, address, userId);
					resultMap.put("names", names);
				}else {
					resultMap=trainDao.insertCourseWare(courseWare, courseWareSub, subject, content, isUsed, address, userId);
					resultMap.put("names", names);
				}
			}else{
				resultMap.put("dealSts","02");
				resultMap.put("dealDesc","上传文件失败");
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
	 * office文件转pdf
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/officeToPdf.srv")
	public  void  officeToPdf(HttpServletRequest request,HttpServletResponse response) {
		String names=request.getParameter("names");
		String[] arr=names.split(",");
		String toFilePath=request.getSession().getServletContext().getRealPath("/office");
		InputStream in=null;
		for (int i = 0; i < arr.length; i++) {
			String path="/"+ new SimpleDateFormat("yyyyMMdd").format(new Date());
			String name=arr[i];
			String suffix=name.substring(name.lastIndexOf(".")+1);
			String fileName=name.substring(0, name.lastIndexOf(".")+1);
			String type=name.substring(name.lastIndexOf(".")+1);
			path=path+"/"+name;
			if(name==null||"".equals(name)||"pdf".equals(suffix.toLowerCase())) {
				continue;
			}
			in = FtpUtil.getFtpInputStream(path);
			try {
				OfficeToPdfUtil.file2Html(in, toFilePath, fileName, type);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 将ftp上文件放到uplaod下
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/getPdfFile.srv")
	public void getPdfFile(HttpServletRequest request,HttpServletResponse response) {
		String address=request.getParameter("address");
		String fileName=address.substring(address.lastIndexOf("/")+1);
		String suffix=fileName.substring(fileName.lastIndexOf(".")+1);
		String path="";
		if("pdf".equals(suffix.toLowerCase())) {
			path="/"+ new SimpleDateFormat("yyyyMMdd").format(new Date());
		}else {
			path="/"+ new SimpleDateFormat("yyyyMMdd").format(new Date())+"pdf";
		}
		String uploadPath=request.getSession().getServletContext().getRealPath("/upload");
		File uploadFile=new File(uploadPath);
		if(!uploadFile.exists()) {
			uploadFile.mkdirs();
		}
		String endPath=uploadPath+File.separator+fileName+".pdf";
		InputStream ftpInputStream = FtpUtil.getFtpInputStream(path+"/"+fileName);
		OfficeToPdfUtil.delAllFile(uploadPath);//删除文件
	    try {
	  		File file=new File(endPath);
			if(!file.exists()) {
				 file.createNewFile();
			}
			OutputStream out=new FileOutputStream(file);
		    int bytesRead = 0;
		    byte[] buffer = new byte[1024 * 8];
		    while ((bytesRead = ftpInputStream.read(buffer)) != -1) {
		    	out.write(buffer, 0, bytesRead);
		    }
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 下载课件文件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/downLoadCourseWare.srv")
	public void downLoadCourseWare(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String address=request.getParameter("address");
		String trainId=request.getParameter("trainId");
		boolean result = FtpUtil.downloadFromFTP(address, response);
		if(result) {
			trainDao.downLoadCourseWare(userId, trainId);
		}
	}
	
	
	/**
	 * 下载课件文件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/downLoadCourse.srv")
	public void downLoadCourse(HttpServletRequest request,HttpServletResponse response) {
		String address=request.getParameter("address");
		FtpUtil.downloadFromFTP(address, response);
	}
	/**
	 * 删除文件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="srv/TrainController/deleteCourseWareFile.srv")
	public void deleteCourseWareFile(HttpServletRequest request,HttpServletResponse response) {
		String courseWareId=request.getParameter("courseWareId");
		String address=request.getParameter("address");
		Map<String,Object>	resultMap=trainDao.updateCourseWareAddress(courseWareId, address);
		String jsonObject=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 课件查询
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/getCourses.srv")
	public void getCourses(HttpServletRequest request,HttpServletResponse response){
		String courseWare=request.getParameter("courseWare");
		String courseWareSub=request.getParameter("courseWareSub");
		String subject=request.getParameter("subject");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String createUser=request.getParameter("createUser");
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = trainDao.getCourses(courseWare, courseWareSub, subject, startTime, endTime, createUser, num, pageSize);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 删除课件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/deleteCourseWare.srv")
	public void deleteCourseWare(HttpServletRequest request,HttpServletResponse response) {
		String courseWareIds=request.getParameter("courseWareIds");
		Map<String, Object> resultMap = trainDao.deleteCourseWare(courseWareIds);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 新增或修改课程
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/insertOrUpdateCourse.srv")
	public void insertOrUpdateCourse(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String courseName=request.getParameter("courseName");
		String courseId=request.getParameter("courseId");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		Integer action=Integer.valueOf(request.getParameter("action"));
		Integer courseType=Integer.valueOf(request.getParameter("courseType"));
		Map<String, Object> resultMap=null;
		if(action==0) {
			resultMap=trainDao.insertCourse(userId, courseName, isUsed,courseType);
		}else {
			resultMap=trainDao.updateCourse(userId, courseName, isUsed, courseId,courseType);
		}
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 为课程选择课件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/insertCourseWareToCourse.srv")
	public void insertCourseWareToCourse(HttpServletRequest request,HttpServletResponse response) {
		String courseId=request.getParameter("courseId");
		String courseWareIds=request.getParameter("courseWareIds");
		Map<String, Object> resultMap = trainDao.insertCourseWareToCourse(courseId, courseWareIds);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 删除课程
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/deleteCourses.srv")
	public void deleteCourses(HttpServletRequest request,HttpServletResponse response) {
		String courseIds=request.getParameter("courseIds");
		Map<String, Object> resultMap = trainDao.deleteCourses(courseIds);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询课程
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/selectCourses.srv")
	public void selectCourses(HttpServletRequest request,HttpServletResponse response) {
		String courseName=request.getParameter("courseName");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer courseType=Integer.valueOf(request.getParameter("courseType"));
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultList = trainDao.selectCourses(courseName, isUsed, startTime, endTime, courseType,num,pageSize);
		String result=GsonUtil.getGson().toJson(resultList);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询课程下未拥有的课件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/selectCourseWareByCourseId.srv")
	public void selectCourseWareByCourseId(HttpServletRequest request,HttpServletResponse response) {
		String courseId=request.getParameter("courseId");
		String courseWare=request.getParameter("courseWare");
		String courseWareSub=request.getParameter("courseWareSub");
		String subject=request.getParameter("subject");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String createUser=request.getParameter("createUser");
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = trainDao.selectCourseWareByCourseId(courseId, courseWare, courseWareSub, subject, startTime, endTime, createUser, num, pageSize);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询课程下所有课件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/selectExitCourseWareByCourseId.srv")
	public void selectExitCourseWareByCourseId(HttpServletRequest request,HttpServletResponse response) {
		String courseId=request.getParameter("courseId");
		String subject=request.getParameter("subject");
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = trainDao.selectExistCourseWareByCourseId(courseId, subject, num, pageSize);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除课程下课件
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/deleteCourseWareFromCourse.srv",method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String deleteCourseWareFromCourse(HttpServletRequest request,HttpServletResponse response) {
		String courseId=request.getParameter("courseId");
		String courseWareIds=request.getParameter("courseWareIds");
		Map<String, Object> resultMap = trainDao.deleteCourseWareFromCourse(courseId, courseWareIds);
		return new Gson().toJson(resultMap);
	}
	/**
	 * 新增或修改培训
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/insertOrUpdateTrain.srv")
	public void  insertOrUpdateTrain(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String trainName=request.getParameter("trainName");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String trainId=request.getParameter("trainId");
		Map<String,Object> resultMap=null;
		if(trainId==null||"".equals(trainId)) {
			resultMap=trainDao.insertTrain(trainName, startTime, endTime, isUsed, userId);
		}else {
			resultMap=trainDao.updateTrain(trainName, startTime, endTime, isUsed, userId, trainId);
		}
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 给培训选择课程
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/selectCoursesToTrain.srv")
	public  void selectCoursesToTrain(HttpServletRequest request,HttpServletResponse response) {
		String trainId=request.getParameter("trainId");
		String courseIds=request.getParameter("courseIds");
		Map<String, Object> resultMap = trainDao.selectCoursesToTrain(trainId, courseIds);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除培训
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/deleteTrains.srv")
	public void deleteTrains(HttpServletRequest request,HttpServletResponse response) {
		String trainIds=request.getParameter("trainIds");
		Map<String, Object> resultMap = trainDao.deleteTrains(trainIds);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询培训
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/selectTrains.srv")
	public void selectTrains(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		String trainName=request.getParameter("trainName");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = trainDao.selectTrains(trainName, isUsed, startTime, endTime, userId, num, pageSize);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询培训下所有课程
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/selectCourseByTrainId.srv")
	public void selectCourseByTrainId(HttpServletRequest request,HttpServletResponse response) {
		String trainId=request.getParameter("trainId");
		String courseName=request.getParameter("courseName");
		Integer isUsed=Integer.valueOf(request.getParameter("isUsed"));
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		Integer courseType=Integer.valueOf(request.getParameter("courseType"));
		Integer num=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = trainDao.selectCourseByTrainId(trainId, courseName, isUsed, startTime, endTime, courseType, num, pageSize);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 选择培训人员
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/insertCsrByTrainId.srv")
	public void insertCsrByTrainId(HttpServletRequest request,HttpServletResponse response) {
		String  trainId=request.getParameter("trainId");
		String userIds=request.getParameter("userIds");
		Map<String, Object> resultMap = trainDao.insertCsrByTrainId(trainId, userIds);
		String result=GsonUtil.getGson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取当前用户角色
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/getRoles.srv")
	public void getRoles(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("user");
		String userId =String.valueOf(user.getId());
		List<String> roles = trainDao.getRoles(userId);
		String result=GsonUtil.getGson().toJson(roles);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/srv/TrainController/upload.srv")
	public void upload(Integer id,@RequestParam MultipartFile[] file,HttpServletRequest request,HttpServletResponse response) throws IOException {
		InputStream in=null;
		String path="/"+ new SimpleDateFormat("yyyyMMdd").format(new Date());
		String address="";
		String toFilePath=request.getSession().getServletContext().getRealPath(File.separator+"office");
		List<String> list=new ArrayList<String>();
		for (int i = 0; i < file.length; i++) {
			String fileName=file[i].getOriginalFilename();
			String fileName1=fileName.substring(0,fileName.lastIndexOf("."));
			String type =fileName.substring(fileName.lastIndexOf(".")+1);
			in = file[i].getInputStream();
			address=path+ "/"+fileName+",";
			list.add(address);
			FtpUtil.uploadFile(path, fileName,in);
			OfficeToPdfUtil.file2Html(in, toFilePath, fileName1, type);
		}
	
		String result=new Gson().toJson(list);
			try {
				PrintWriter printWriter = response.getWriter();
				printWriter.print(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/srv/TrainController/deleteFile.srv")
	public void deleteFile(HttpServletRequest request,HttpServletResponse response) {
		String adress="/ftp/upload/20180408/cep接口文档.txt";
		boolean re = FtpUtil.deleteFileFromFTP(adress);
		Map<String,Object> resultMap=new HashMap<>();
		resultMap.put("result", re);
		String result=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/srv/TrainController/getWord.srv")
	public void getWord(HttpServletRequest request,HttpServletResponse response) {
		String path="/"+ new SimpleDateFormat("yyyyMMdd").format(new Date());
		String fileName=path+"/"+"线索相关接口说明0402.docx";
		String htmlPath=request.getSession().getServletContext().getRealPath("/word/");
		Map<String,Object> map=new HashMap<>();
		String result=null;
		try {
			InputStream in = FtpUtil.getFtpInputStream(fileName);
			result = WordToHtml.Word2007ToHtml(in, htmlPath);
			map.put("result", result);
			String content=new Gson().toJson(map);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/srv/TrainController/getDoc.srv")
	public void getDoc(HttpServletRequest request,HttpServletResponse response) {
		String path="C:/Users/李远/Desktop/Hi-Agent6.0工作流使用说明（最新）.doc";
		String htmlPath=request.getSession().getServletContext().getRealPath("/doc/");
		Map<String,Object> map=new HashMap<>();
		String result=null;
		try {
			result=WordToHtml.dox(path, htmlPath);
			map.put("result", result);
			String content=new Gson().toJson(map);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/srv/TrainController/getPPtx.srv")
	public void getPPtx(HttpServletRequest request,HttpServletResponse response) {
		try {
			List<String> list = ConverPPTFileToImageUtil.toImage2007();
			String content=new Gson().toJson(list);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(content);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="/srv/TrainController/getPdf.srv")
	public void getPdf(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String uploadPath=request.getSession().getServletContext().getRealPath("/upload");
		String path="/"+ new SimpleDateFormat("yyyyMMdd").format(new Date())+"pdf";
		String fileName="线索相关接口说明0402.docx.pdf";
		InputStream ftpInputStream = FtpUtil.getFtpInputStream(path+"/"+fileName);
		File file=new File(uploadPath+File.separator+"线索相关接口说明0402.pdf");
		if(!file.exists()) {
			 file.createNewFile();;
		}
		OutputStream out=new FileOutputStream(file);
	     int bytesRead = 0;
	      byte[] buffer = new byte[1024 * 8];
	      while ((bytesRead = ftpInputStream.read(buffer)) != -1) {
	    	  out.write(buffer, 0, bytesRead);
	      }
	      out.close();
	}
	
}