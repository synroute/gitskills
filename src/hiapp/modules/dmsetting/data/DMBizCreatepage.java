package hiapp.modules.dmsetting.data;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dm.setting.dbLayer.DMBizWorkSheets;
import dm.setting.dbLayer.WorkSheetTypeDm;
import hiapp.utils.base.DatabaseType;
import hiapp.modules.dmsetting.ImportMapColumn;
import hiapp.system.worksheet.dblayer.WorkSheet;
import hiapp.system.worksheet.dblayer.WorkSheetColumn;
import hiapp.system.worksheet.dblayer.WorkSheetManager;

public class DMBizCreatepage {

	public static boolean getMapColumns(Connection dbConn,DatabaseType dataBaseType,int bizId,List<ImportMapColumn> listMapColumn,String worksheet){
		int workSheetId=0;
		if (worksheet.equals("结果表")) {
			workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn,bizId, WorkSheetTypeDm.WSTDM_USERDEFINE.getType());
		}else{
			workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn,bizId, WorkSheetTypeDm.WSTDM_CUSTOMERIMPORT.getType());
		}
		
		WorkSheet workSheet=null;
		WorkSheetManager.getWorkSheet(dbConn, dataBaseType, workSheetId, workSheet);
		List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
		WorkSheet.getColumns(dbConn, workSheetId,listColumns);
		for(int ii=0;ii<listColumns.size();ii++){
			WorkSheetColumn workSheetColumn=listColumns.get(ii);
			ImportMapColumn importMapColumn=new ImportMapColumn();
			importMapColumn.setName(workSheetColumn.getName());
			importMapColumn.setNameCh(workSheetColumn.getNameCh());
			importMapColumn.setDescription(workSheetColumn.getDescription());
			listMapColumn.add(importMapColumn);
		}
		
        return true;
	}
	
	public static boolean getWorkMapColumns(Connection dbConn,DatabaseType dataBaseType,int bizId ,List<ImportMapColumn> listMapColumn) throws SQLException{
		//int workSheetId=DMBizWorkSheets.getWorkSheetId(dbConn,bizId, WorkSheetTypeDm.WSTDM_CUSTOMERIMPORT.getType());
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String worksql="select ID,NAME,NAMECH from HASYS_WORKSHEET where ID in (select WORKSHEETID from HASYS_DM_BIZWORKSHEET where BIZID="+bizId+")";
		stmt = dbConn.prepareStatement(worksql);
		rs = stmt.executeQuery();
		while(rs.next())
		{
			WorkSheet workSheet=null;
			WorkSheetManager.getWorkSheet(dbConn, dataBaseType, rs.getInt("ID"), workSheet);
			List<WorkSheetColumn> listColumns=new ArrayList<WorkSheetColumn>();
			WorkSheet.getColumns(dbConn, rs.getInt("ID"),listColumns);
			for(int ii=0;ii<listColumns.size();ii++){
				WorkSheetColumn workSheetColumn=listColumns.get(ii);	
				ImportMapColumn importMapColumn=new ImportMapColumn();
				importMapColumn.setName(workSheetColumn.getName());
				importMapColumn.setNameCh(workSheetColumn.getNameCh());
				importMapColumn.setDescription(workSheetColumn.getDescription());
				importMapColumn.setWorksheetid(rs.getString("ID"));
				importMapColumn.setWorksheetName(rs.getString("NAME"));
				importMapColumn.setWorksheetNameCh(rs.getString("NAMECH"));
				listMapColumn.add(importMapColumn);
			}
		}
		
        return true;
	}
	
	
	
}
