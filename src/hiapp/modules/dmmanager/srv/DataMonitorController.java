package hiapp.modules.dmmanager.srv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hiapp.modules.dmmanager.data.DataMonitorJdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
public class DataMonitorController {
	@Autowired
	private DataMonitorJdbc dataMonitorJdbc;
	
	@RequestMapping(value="/srv/DataMonitorController/getMonitorData.srv")
	private void getMonitorData(HttpServletRequest request, HttpServletResponse response){
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String importId=request.getParameter("importId");
		Integer bizId=Integer.valueOf(request.getParameter("bizId"));
		Integer pageNum=Integer.valueOf(request.getParameter("page"));
		Integer pageSize=Integer.valueOf(request.getParameter("rows"));
		Map<String, Object> resultMap = dataMonitorJdbc.getMonitorData(bizId, startTime, endTime, importId, pageNum, pageSize);
		String jsonObject=new Gson().toJson(resultMap);
		try {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(jsonObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
