package hiapp.modules.dmmanager.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.StyledEditorKit.BoldAction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dm.setting.dbLayer.BizItem;
import dm.setting.dbLayer.DMBizPermission;
import dm.setting.dbLayer.DMBizTemplateImport;
import dm.setting.dbLayer.DMBizTemplatesImport;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDataManagerBase
 */
@WebServlet("/servletDataManagerBase")
public class servletDataManagerBase extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDataManagerBase() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UtilServlet utilServlet=new UtilServlet(request, response);
		if(utilServlet.Action.equals("getPermittedBiz")){
			try {
				String userId=utilServlet.getUserId();
				getPermittedBiz(utilServlet,userId);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		utilServlet.Response();
		utilServlet.Close();
		
	}
	public static boolean getPermittedBiz(UtilServlet utilServlet,String userId) throws Exception{
		List<BizItem> listBizItem =new ArrayList<BizItem>();
		DMBizPermission.getPermittedBizList(utilServlet, userId, "数据管理", listBizItem);
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_COMBOBOXDICTIONAY);
		JsonArray jaDictionary=utilServlet.getJaResultComboboxDictionary();
		for(int ii=0;ii<listBizItem.size();ii++){
			BizItem bizItem=listBizItem.get(ii);
			String szText=String.format("%d:%s", bizItem.getBizId(),bizItem.getName());
			JsonObject jsonObject=new JsonObject();
			jsonObject.addProperty("id", bizItem.getBizId());
			jsonObject.addProperty("text", szText);
			jaDictionary.add(jsonObject);

		} 
		return true;
	}

}
