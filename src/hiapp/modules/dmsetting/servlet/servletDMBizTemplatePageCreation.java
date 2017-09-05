package hiapp.modules.dmsetting.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.internal.compiler.util.Sorting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dm.setting.dbLayer.DMBizTemplatePageCreation;
import dm.setting.dbLayer.DMBizWorkSheets;
import dm.setting.dbLayer.FilterColumnSourceItem;
import dm.setting.dbLayer.FilterItem;
import dm.setting.dbLayer.GridColumnItem;
import dm.setting.dbLayer.GridColumnSourceItem;
import dm.setting.dbLayer.WorkSheetDetailItem;
import dm.setting.dbLayer.WorkSheetDm;
import hiapp.utils.ConstantResultType;
import hiapp.utils.UtilServlet;

/**
 * Servlet implementation class servletDMBizTemplatePageCreation
 */
//@WebServlet("/servletDMBizTemplatePageCreation")
public class servletDMBizTemplatePageCreation extends HttpServlet {
	public class ConstantPanelType {
	    public static final int PANELTYPE_PAGEINFOBASE			=1;  
	    public static final int PANELTYPE_FILTER				=2;  
	    public static final int PANELTYPE_GRID					=3;  
	    public static final int PANELTYPE_WORKSHEETDETAIL		=4;  
	    public static final int PANELTYPE_WORKSHEETDETAILLIST	=5;  
	}


	private static final long serialVersionUID = 1L;
	private static DMBizTemplatePageCreation dmBizTemplatePageCreation=null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletDMBizTemplatePageCreation() {
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
		
		String bizId=request.getParameter("bizId");
		String templateId=request.getParameter("templateId");

		if(utilServlet.Action.equals("getNaviTree")){
			try {
				getNaviTree(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("startEditXml")){
			try {
				startEditXml(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("saveEditXml")){
			try {
				String pathAbs=this.getServletContext().getRealPath("/");
				String fileNameRel="/app_pages/dial/dialPageTemplate.xml";
				String fileNameAbs=pathAbs+"app_pages\\dial\\dialPageTemplate.xml";
				saveEditXml(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),fileNameRel,fileNameAbs);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if(utilServlet.Action.equals("getPageBaseInfo")){
			try {
				String pageType=request.getParameter("pageType");
				getPageBaseInfo(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("updatePageBaseInfo")){
			try {
				String pageType=request.getParameter("pageType");
				String title=request.getParameter("title");
				updatePageBaseInfo(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType,title);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("getFilterColumnsSource")){
			try {
				String pageType=request.getParameter("pageType");
				getFilterColumnsSource(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("getGridColumnsSource")){
			try {
				String pageType=request.getParameter("pageType");
				getGridColumnsSource(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("getGridWorkSheetDetail")){
			try {
				String pageType=request.getParameter("pageType");
				String workSheetId=request.getParameter("workSheetId");
				getGridWorkSheetDetail(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType,Integer.parseInt(workSheetId));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("getFilterItems")){
			try {
				String pageType=request.getParameter("pageType");
				getFilterItems(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("getGridColumns")){
			try {
				String pageType=request.getParameter("pageType");
				getGridColumns(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("updateFilterItems")){
			try {
				String pageType=request.getParameter("pageType");
				String szFilterItems=request.getParameter("filterItems");
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(szFilterItems).getAsJsonArray();

				updateFilterItems(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType,jsonArray);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("updateGridColumns")){
			try {
				String pageType=request.getParameter("pageType");
				String szFilterItems=request.getParameter("filterItems");
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(szFilterItems).getAsJsonArray();

				updateGridColumns(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType,jsonArray);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(utilServlet.Action.equals("updateWorkSheetDetail")){
			try {
				String pageType=request.getParameter("pageType");
				String workSheetId=request.getParameter("workSheetId");
				String szworkSheetDetailItems=request.getParameter("workSheetDetailItems");
				JsonParser jsonParser=new JsonParser();
				JsonArray jsonArray=jsonParser.parse(szworkSheetDetailItems).getAsJsonArray();

				updateWorkSheetDetail(utilServlet,Integer.parseInt(bizId),Integer.parseInt(templateId),pageType,Integer.parseInt(workSheetId),jsonArray);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
				
		
		utilServlet.Response();
		utilServlet.Close();
	}
	public static boolean getNaviTree(UtilServlet utilServlet,int bizId,int templateId) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_TREE);
		if(!(bizId==dmBizTemplatePageCreation.getBizId() && templateId==dmBizTemplatePageCreation.getTemplateId())){
			utilServlet.setResultCode(1);
			utilServlet.setResultMessage("閿欒锛佸凡缁忔湁椤甸潰鍦ㄥ仛澶栨嫧椤甸潰鐢熸垚锛岃鍏堝畬鎴愬凡鏈夐〉闈㈢殑鎿嶄綔骞跺叧闂〉闈紒");
			
			return false;
		}

		JsonArray jaTree=utilServlet.getJaResultTree();
		int nNodeId=0;

		try {
//瀹㈡埛鍒楄〃椤甸潰///////////////////////////////////////////////
			{
				nNodeId=10000;
				JsonObject jsPage=new JsonObject();
				jsPage.addProperty("pageType", "CustListPage");
				jsPage.addProperty("type", ConstantPanelType.PANELTYPE_PAGEINFOBASE);
				jsPage.addProperty("id", nNodeId);
				jsPage.addProperty("text", "瀹㈡埛鍒楄〃椤甸潰");
				
				JsonArray jaInPage=new JsonArray();
				
				JsonObject jsFilter=new JsonObject();
				jsFilter.addProperty("pageType", "CustListPage");
				jsFilter.addProperty("type", ConstantPanelType.PANELTYPE_FILTER);
				jsFilter.addProperty("id", nNodeId+1);	
				jsFilter.addProperty("text", "绛涢�夎缃�");
				jaInPage.add(jsFilter);
				jsPage.add("children", jsFilter);
				
				JsonObject jsGridColumn=new JsonObject();
				jsGridColumn.addProperty("pageType", "CustListPage");
				jsGridColumn.addProperty("type", ConstantPanelType.PANELTYPE_GRID);
				jsGridColumn.addProperty("id", nNodeId+2);	
				jsGridColumn.addProperty("text", "瀹㈡埛鍒楄〃缃戞牸鍒楄缃�");
				jaInPage.add(jsGridColumn);
				jsPage.add("children", jaInPage);
				
				jaTree.add(jsPage);
			}		
//瀹㈡埛鍛煎彨椤甸潰///////////////////////////////////////////////
			{
				nNodeId=20000;
				JsonObject jsPage=new JsonObject();
				jsPage.addProperty("pageType", "CustDetailPage");
				jsPage.addProperty("type", ConstantPanelType.PANELTYPE_PAGEINFOBASE);
				jsPage.addProperty("id", nNodeId);
				jsPage.addProperty("text", "瀹㈡埛鍛煎彨椤甸潰");
				JsonArray jaInPage=new JsonArray();
				
				nNodeId=21000;
				JsonObject jsWorkSheetList=new JsonObject();
				jsWorkSheetList.addProperty("pageType", "CustDetailPage");
				jsWorkSheetList.addProperty("type", ConstantPanelType.PANELTYPE_WORKSHEETDETAILLIST);
				jsWorkSheetList.addProperty("id", nNodeId);	
				jsWorkSheetList.addProperty("text", "宸ヤ綔琛ㄨ缁�");
				jaInPage.add(jsWorkSheetList);
				List<WorkSheetDm> listWorkSheet=new ArrayList<WorkSheetDm>();
				DMBizWorkSheets.getWorkSheetAll(utilServlet, bizId, listWorkSheet);
				JsonArray jaWorkSheet=new JsonArray();

				for(int ii=0;ii<listWorkSheet.size();ii++){
					WorkSheetDm workSheet=listWorkSheet.get(ii);
					String type=workSheet.getType();
					if(type.equals("瀹㈡埛瀵煎叆宸ヤ綔琛�")) continue;	
					if(type.equals("棰勭害宸ヤ綔琛�")) continue;	
					if(type.equals("棰勭害鍘嗗彶宸ヤ綔琛�")) continue;	
					if(type.equals("瀹㈡埛鍒嗛厤宸ヤ綔琛�")) continue;	
					if(type.equals("瀹㈡埛鍒嗛厤鍘嗗彶宸ヤ綔琛�")) continue;	
					String label=String.format("%d:%s", workSheet.getId(),workSheet.getNameCh());
					JsonObject jsWorkSheet=new JsonObject();
					jsWorkSheet.addProperty("pageType", "CustDetailPage");
					jsWorkSheet.addProperty("type", ConstantPanelType.PANELTYPE_WORKSHEETDETAIL);
					jsWorkSheet.addProperty("id", nNodeId+ii+1);	
					jsWorkSheet.addProperty("text", label);
					jsWorkSheet.addProperty("workSheetId", workSheet.getId());
					jaWorkSheet.add(jsWorkSheet);
					
				}
				jsWorkSheetList.add("children", jaWorkSheet);
				jsPage.add("children", jaInPage);
				jaTree.add(jsPage);
			}	
//鎷ㄦ墦鍘嗗彶鍒楄〃椤甸潰///////////////////////////////////////////////
			{
				nNodeId=30000;
				JsonObject jsPage=new JsonObject();
				jsPage.addProperty("pageType", "DialListPage");
				jsPage.addProperty("type", ConstantPanelType.PANELTYPE_PAGEINFOBASE);
				jsPage.addProperty("id", nNodeId);
				jsPage.addProperty("text", "鎷ㄦ墦鍒楄〃椤甸潰");
				
				JsonArray jaInPage=new JsonArray();
				
				JsonObject jsFilter=new JsonObject();
				jsFilter.addProperty("pageType", "DialListPage");
				jsFilter.addProperty("type", ConstantPanelType.PANELTYPE_FILTER);
				jsFilter.addProperty("id", nNodeId+1);	
				jsFilter.addProperty("text", "绛涢�夎缃�");
				jaInPage.add(jsFilter);
				jsPage.add("children", jsFilter);

				JsonObject jsGridColumn=new JsonObject();
				jsGridColumn.addProperty("pageType", "DialListPage");
				jsGridColumn.addProperty("type", ConstantPanelType.PANELTYPE_GRID);
				jsGridColumn.addProperty("id", nNodeId+2);	
				jsGridColumn.addProperty("text", "鎷ㄦ墦鍒楄〃缃戞牸鍒楄缃�");
				jaInPage.add(jsGridColumn);
				jsPage.add("children", jaInPage);
				
				jaTree.add(jsPage);
			}		
//棰勭害鍒楄〃椤甸潰///////////////////////////////////////////////
			{
				nNodeId=40000;
				JsonObject jsFolderPresetTimePage=new JsonObject();
				jsFolderPresetTimePage.addProperty("id", nNodeId);
				jsFolderPresetTimePage.addProperty("text", "棰勭害鍒楄〃椤甸潰");
				JsonArray jaSystemSetting=new JsonArray();
				
				jaTree.add(jsFolderPresetTimePage);
			}		
			
		} catch (Exception e) {
			utilServlet.setResultCode(1);
			return false;
		} finally {
		}
		utilServlet.setResultCode(0);
		return true;
	}
	
	public static boolean saveEditXml(UtilServlet utilServlet,int bizId,int templateId,String fileNameRel,String fileNameAbs) throws Exception{
		dmBizTemplatePageCreation.saveXml(utilServlet,fileNameAbs);
		utilServlet.setResultValue("xmlFileName", fileNameRel);
		utilServlet.setResultCode(0);
		String message=String.format("鎴愬姛,缁濆璺緞鏂囦欢鍚�=[%s],鐩稿璺緞鏂囦欢鍚�=[%s],[%s,%s,%s]", 
										fileNameAbs,fileNameRel,
										String.format("/app_pages/dial/DmBiz%d_CustDetail", bizId),
										String.format("/app_pages/dial/DmBiz%d_CustList", bizId),
								        String.format("/app_pages/dial/DmBiz%d_DialList", bizId));
		utilServlet.setResultMessage(message);
		return true;
	}
	
	public static boolean startEditXml(UtilServlet utilServlet,int bizId,int templateId) throws Exception{
		dmBizTemplatePageCreation=new DMBizTemplatePageCreation();
		dmBizTemplatePageCreation.setBizId(bizId);
		dmBizTemplatePageCreation.setTemplateId(templateId);
		dmBizTemplatePageCreation.loadXml(utilServlet);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		return true;
	}
	public static boolean getPageBaseInfo(UtilServlet utilServlet,int bizId,int templateId,String pageType) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_QUERY);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		StringBuffer pageFile=new StringBuffer();
		StringBuffer title=new StringBuffer();
		dmBizTemplatePageCreation.getPageBaseInfo(utilServlet, pageType,pageFile,title);
		String value=pageFile.toString();
		utilServlet.setResultValue("pageFile", value);
		value=title.toString();
		utilServlet.setResultValue("title", value);
		
		return true;
	}
	public static boolean updatePageBaseInfo(UtilServlet utilServlet,int bizId,int templateId,String pageType,String title) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		dmBizTemplatePageCreation.updatePageBaseInfo(utilServlet, pageType,title);
		return true;
	}
	public static boolean getFilterColumnsSource(UtilServlet utilServlet,int bizId,int templateId,String pageType) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<FilterColumnSourceItem> listColumnsSource=new ArrayList<FilterColumnSourceItem>();
		if(!dmBizTemplatePageCreation.getFilterColumnsSource(utilServlet, bizId, templateId, pageType, listColumnsSource)){
			return false;
		}
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listColumnsSource.size();ii++){
        	FilterColumnSourceItem filterColumnSourceItem=listColumnsSource.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			jsColumn.addProperty("WorkSheetNameColumnNameCh",filterColumnSourceItem.getWorkSheetColumnNameCh());
			jsColumn.addProperty("WorkSheetNameCh",filterColumnSourceItem.getWorkSheetNameCh());
			jsColumn.addProperty("WsColumnNameCh",filterColumnSourceItem.getWsColumnNameCh());
			jsColumn.addProperty("ColumnName",filterColumnSourceItem.getColumnName());

			jsonArray.add(jsColumn);
        }
		jsonObject.addProperty("total", listColumnsSource.size());
		jsonObject.add("rows", jsonArray);
		
		
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		return true;
	}
	public static boolean getGridColumnsSource(UtilServlet utilServlet,int bizId,int templateId,String pageType) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<GridColumnSourceItem> listColumnsSource=new ArrayList<GridColumnSourceItem>();
		if(!dmBizTemplatePageCreation.getGridColumnsSource(utilServlet, bizId, templateId, pageType, listColumnsSource)){
			return false;
		}
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listColumnsSource.size();ii++){
        	GridColumnSourceItem gridColumnSourceItem=listColumnsSource.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			jsColumn.addProperty("workSheetNameColumnNameCh",gridColumnSourceItem.getWorkSheetColumnNameCh());
			jsColumn.addProperty("workSheetNameCh",gridColumnSourceItem.getWorkSheetNameCh());
			jsColumn.addProperty("wsColumnNameCh",gridColumnSourceItem.getWsColumnNameCh());
			jsColumn.addProperty("ColumnName",gridColumnSourceItem.getColumnName());
			
			
			jsonArray.add(jsColumn);
        }
		jsonObject.addProperty("total", listColumnsSource.size());
		jsonObject.add("rows", jsonArray);
		
		
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		return true;
	}
	public static boolean getGridColumns(UtilServlet utilServlet,int bizId,int templateId,String pageType) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<GridColumnItem> listGridColumnItems=new ArrayList<GridColumnItem>();
		if(!dmBizTemplatePageCreation.getGridColumns(utilServlet, bizId, templateId, pageType, listGridColumnItems)){
			return false;
		}
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listGridColumnItems.size();ii++){
        	GridColumnItem gridColumnItem=listGridColumnItems.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			jsColumn.addProperty("Title", gridColumnItem.getTitle());
			jsColumn.addProperty("Width", gridColumnItem.getWidth());
			jsColumn.addProperty("ColumnName", gridColumnItem.getColumnName());
			jsColumn.addProperty("WorkSheetColumnName", gridColumnItem.getWorkSheetColumnName());
			jsColumn.addProperty("operation", gridColumnItem.getOperation());
			jsonArray.add(jsColumn);
        }
        
        
		jsonObject.addProperty("total", listGridColumnItems.size());
		jsonObject.add("rows", jsonArray);
		
		
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		return true;
	}
	
	public static boolean getFilterItems(UtilServlet utilServlet,int bizId,int templateId,String pageType) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<FilterItem> listFilterItems=new ArrayList<FilterItem>();
		if(!dmBizTemplatePageCreation.getFilterItems(utilServlet, bizId, templateId, pageType, listFilterItems)){
			return false;
		}
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listFilterItems.size();ii++){
        	FilterItem filterItem=listFilterItems.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			
			jsColumn.addProperty("WorkSheetNameCh", filterItem.getWorkSheetNameCh());
			jsColumn.addProperty("WorkSheetColumnNameCh", filterItem.getWorkSheetColumnNameCh());
			jsColumn.addProperty("ColumnName", filterItem.getColumnName());
			jsColumn.addProperty("ControlType", filterItem.getControlType());
			jsColumn.addProperty("ComboboxOptions", filterItem.getComboboxOptions());
			jsColumn.addProperty("PrefixText", filterItem.getPrefixLabel());
			jsColumn.addProperty("PostfixText", filterItem.getPostfixLabel());
			jsColumn.addProperty("IsMustFill", filterItem.getIsMustFill());
			jsColumn.addProperty("ControlNameOptr", filterItem.getControlNameOptr());
			
			jsonArray.add(jsColumn);
        }
        
        
		jsonObject.addProperty("total", listFilterItems.size());
		jsonObject.add("rows", jsonArray);
		
		
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		return true;
	}
	public static boolean getGridWorkSheetDetail(UtilServlet utilServlet,int bizId,int templateId,String pageType,int workSheetId) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_GRID);
		List<WorkSheetDetailItem> listWorkSheetDetailItem=new ArrayList<WorkSheetDetailItem>();
		if(!dmBizTemplatePageCreation.getWorkSheetDetail(utilServlet, bizId, templateId, pageType, workSheetId,listWorkSheetDetailItem)){
			return false;
		}
		JsonObject jsonObject=utilServlet.getJoResult();
		JsonArray jsonArray=new JsonArray();
        for(int ii=0;ii<listWorkSheetDetailItem.size();ii++){
        	WorkSheetDetailItem workSheetDetailItem=listWorkSheetDetailItem.get(ii);
			JsonObject jsColumn=new JsonObject();
			jsColumn.addProperty("index", ii+1);
			jsColumn.addProperty("ColumnName", workSheetDetailItem.getColumnName());
			jsColumn.addProperty("WorkSheetColumnNameCh", workSheetDetailItem.getWorkSheetColumnNameCh());
			jsColumn.addProperty("ControlNameOptr", workSheetDetailItem.getControlNameOptr());
			jsColumn.addProperty("IsVisible", workSheetDetailItem.getIsVisible());
			jsColumn.addProperty("ControlType", workSheetDetailItem.getControlType());
			jsColumn.addProperty("ComboboxOptions", workSheetDetailItem.getComboboxOptions());
			jsColumn.addProperty("IsIncludeDialButton", workSheetDetailItem.getIsIncludeDialButton());
			jsColumn.addProperty("PrefixText", workSheetDetailItem.getPrefixText());
			jsColumn.addProperty("PostfixText", workSheetDetailItem.getPostfixText());
			jsColumn.addProperty("IsMustFill", workSheetDetailItem.getIsMustFill());
			jsColumn.addProperty("Length", workSheetDetailItem.getLength());
			jsColumn.addProperty("Mask", workSheetDetailItem.getMask());
			jsColumn.addProperty("IsReadOnly", workSheetDetailItem.getIsReadOnly());
			jsColumn.addProperty("IsDisabled", workSheetDetailItem.getIsDisabled());
			jsColumn.addProperty("IsFromFirstCol", workSheetDetailItem.getIsFromFirstCol());
			jsColumn.addProperty("OccupyColCount", workSheetDetailItem.getOccupyColCount());
			jsColumn.addProperty("OccupyRowCount", workSheetDetailItem.getOccupyRowCount());
			jsonArray.add(jsColumn);
        }
        
        
		jsonObject.addProperty("total", listWorkSheetDetailItem.size());
		jsonObject.add("rows", jsonArray);
		
		
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		return true;
	}

	public static boolean updateFilterItems(UtilServlet utilServlet,int bizId,int templateId,String pageType,JsonArray jsonArray) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		dmBizTemplatePageCreation.updateFilterItems(utilServlet, pageType,jsonArray);
		return true;
	}
	
	public static boolean updateGridColumns(UtilServlet utilServlet,int bizId,int templateId,String pageType,JsonArray jsonArray) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		dmBizTemplatePageCreation.updateGridColumns(utilServlet, pageType,jsonArray);
		return true;
	}
	public static boolean updateWorkSheetDetail(UtilServlet utilServlet,int bizId,int templateId,String pageType,int workSheetId,JsonArray jsonArray) throws Exception{
		utilServlet.setResultType(ConstantResultType.RESULTTYPE_SUBMIT);
		utilServlet.setResultCode(0);
		utilServlet.setResultMessage("鎴愬姛");
		dmBizTemplatePageCreation.updateWorkSheetDetail(utilServlet, pageType,workSheetId,jsonArray);
		return true;
	}
}
