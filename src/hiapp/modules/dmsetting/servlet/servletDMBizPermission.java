package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dm.setting.dbLayer.BizPermissionItem;
import dm.setting.dbLayer.BizPermissionValues;
import dm.setting.dbLayer.DMBizManager;
import dm.setting.dbLayer.DMBizPermission;
import dm.setting.dbLayer.DMEndCode;
import dm.setting.dbLayer.DMEndCodeManager;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDMBizPermission
 */
//@WebServlet("/servletDMBizPermission")
public class servletDMBizPermission extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizPermission() {
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
		UtilServlet utilServlet=new UtilServlet(request,response);
		if(utilServlet.Action.equals("loadDmBizPermissionColumns")){
			try {
				loadDmBizPermissionColumns(utilServlet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("loadDmBizPermissionData")){
			try {
				loadDmBizPermissionData(utilServlet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
		else if(utilServlet.Action.equals("submitPermission")){
			try {
				String szJaPermission=request.getParameter("JaPermissions");
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(szJaPermission).getAsJsonArray();
				submitPermission(utilServlet,jsonArray);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		utilServlet.Response();
		utilServlet.Close();
		
	}
	private static boolean loadDmBizPermissionColumns(UtilServlet utilServlet){
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		JsonArray jaColumns1=new JsonArray();
		JsonArray jaColumns2=new JsonArray();
        JsonObject jso=new JsonObject();
        jso.addProperty("field", "PermId");
        jso.addProperty("title", "bbb权限ID");
        jso.addProperty("width", 80);
        jaColumns1.add(jso);
        jso.addProperty("title", "cc权限ID");
        jaColumns2.add(jso);
		
        jso=new JsonObject();
        jso.addProperty("field", "PermName");
        jso.addProperty("title", "dd权限名称");
        jso.addProperty("width", 80);
        jaColumns1.add(jso);
        jso.addProperty("title", "ee权限名称");
        jaColumns2.add(jso);
		

        List<BizPermissionItem> listBizPermssion=new ArrayList<BizPermissionItem>();
        if(!DMBizPermission.getDmBizPermissionDefine(utilServlet, listBizPermssion)){
        	return false;
        }
        
        for(int ii=0;ii<listBizPermssion.size();ii++){
        	BizPermissionItem bizPermissionItem=listBizPermssion.get(ii);
        	jso=new JsonObject();
			String szLabel=String.format("%d:%s", bizPermissionItem.getBizId(),bizPermissionItem.getBizName());
			
			JsonObject jsBiz11=new JsonObject();
			jsBiz11.addProperty("field", "FSystemSetting");
			jsBiz11.addProperty("title", szLabel);
			jsBiz11.addProperty("width", 80);
			jaColumns1.add(jsBiz11);	
			
			JsonObject jsBiz12=new JsonObject();
			jsBiz11.addProperty("field", "FDataanager");
			jsBiz12.addProperty("title", "");
			jsBiz12.addProperty("width", 80);
			jaColumns1.add(jsBiz12);	
			
			
			
			JsonObject jsBiz21=new JsonObject();
			jsBiz21.addProperty("field", String.format("F%d_SystemSetting", bizPermissionItem.getBizId()));
			jsBiz21.addProperty("title", "cc系统配置");
			jsBiz21.addProperty("width", 80);
			jaColumns2.add(jsBiz21);	
			
			JsonObject jsBiz22=new JsonObject();
			jsBiz22.addProperty("field", String.format("F%d_DataManager", bizPermissionItem.getBizId()));
			jsBiz22.addProperty("title", "cc数据管理");
			jsBiz22.addProperty("width", 80);
			jaColumns2.add(jsBiz22);        
		}	
        
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jaaColumns=new JsonArray();
		jaaColumns.add(jaColumns1);
		jaaColumns.add(jaColumns2);
		jsonObject.add("Columns", jaaColumns);
		
		
		
		return true;
	}
	public static boolean loadDmBizPermissionData(UtilServlet utilServlet) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<BizPermissionValues> listPermissionValues=new ArrayList<BizPermissionValues>();
		if(!DMBizPermission.loadDmBizPermissionData(utilServlet, listPermissionValues)){
			return false;
		}	
		
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
		for(int ii=0;ii<listPermissionValues.size();ii++){
			BizPermissionValues bizPermissionValues=listPermissionValues.get(ii);
			JsonObject jsItem=new JsonObject();
			jsItem.addProperty("No", ii+1);
			jsItem.addProperty("PermId", bizPermissionValues.getPermId());
			jsItem.addProperty("PermName", bizPermissionValues.getPermName());
			List<BizPermissionItem> listBizPermissionItem=bizPermissionValues.getListPermissionItems();
			for(int jj=0;jj<listBizPermissionItem.size();jj++){
				BizPermissionItem bizPermissionItem=listBizPermissionItem.get(jj);
				if(bizPermissionItem.getPermissionName().equals("系统配置")){
					if(bizPermissionItem.getPermissionValue()){
						jsItem.addProperty(String.format("F%d_SystemSetting", bizPermissionItem.getBizId()), "Y");
					}
					else{
						jsItem.addProperty(String.format("F%d_SystemSetting", bizPermissionItem.getBizId()), "");
					}
				}
				else{
					if(bizPermissionItem.getPermissionValue()){
						jsItem.addProperty(String.format("F%d_DataManager", bizPermissionItem.getBizId()), "Y");
					}
					else{
						jsItem.addProperty(String.format("F%d_DataManager", bizPermissionItem.getBizId()), "");
					}
				}
			}
			jsonArray.add(jsItem);
		}
		jsonObject.addProperty("total", listPermissionValues.size());
		jsonObject.add("rows", jsonArray);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("成功");
		return true;
		
	}
	public static boolean submitPermission(UtilServlet utilServlet,JsonArray jaPermission) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		if(!DMBizPermission.submitPermission(utilServlet, jaPermission)){
			return false;
		}
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("成功");
		return true;

	}
}
