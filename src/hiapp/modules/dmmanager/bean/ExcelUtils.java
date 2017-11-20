package hiapp.modules.dmmanager.bean;

import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


public class ExcelUtils extends BaseRepository{
	@SuppressWarnings("deprecation")
	public  void exportExcel(List<String> excelHeader,List<Map<String,Object>> dataList,List<String> sheetCulomn,HttpServletRequest request,HttpServletResponse response){
		 HSSFWorkbook workbook = new HSSFWorkbook();                        // 创建工作簿对象
         HSSFSheet sheet = workbook.createSheet();                     // 创建工作表
         
        // 产生表格标题行
         HSSFRow rowm = sheet.createRow(0);
         HSSFCell cellTiltle = rowm.createCell(0);
         
         //sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展】
        // HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
         HSSFCellStyle style = this.getStyle(workbook);                    //单元格样式对象
         
        /* sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, (excelHeader.size()-1)));  
         cellTiltle.setCellStyle(columnTopStyle);
         cellTiltle.setCellValue("导出数据表");*/
         
         // 定义所需列数
         int columnNum = excelHeader.size();
         HSSFRow rowRowName = sheet.createRow(0);                // 在索引2的位置创建行(最顶端的行开始的第二行)
          
         // 将列头设置到sheet的单元格中
         for(int n=0;n<columnNum;n++){
             HSSFCell  cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
             cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
             HSSFRichTextString text = new HSSFRichTextString(excelHeader.get(n));
             cellRowName.setCellValue(text);    
             cellRowName.setCellStyle(style);   //设置列头单元格的值
         }
         //将查询出的数据设置到sheet对应的单元格中
         for (int i = 0; i < dataList.size(); i++) {
        	 HSSFRow row = sheet.createRow(i+1);//创建所需的行数
        	 for (int j = 0; j < sheetCulomn.size(); j++) {
        		 HSSFCell  cell = null;   //设置单元格的数据类型
        		 cell=row.createCell(j);
        		 if(dataList.get(i).keySet().contains(sheetCulomn.get(j))){
        			 String value=dataList.get(i).get(sheetCulomn.get(j)).toString();
        			 cell.setCellValue(dataList.get(i).get(sheetCulomn.get(j)).toString());
            		 
        		 }
        		 cell.setCellStyle(style);   
			}
        	 
		}
         
       //让列宽随着导出的列长自动适应
         for (int colNum = 0; colNum < columnNum; colNum++) {
             int columnWidth = sheet.getColumnWidth(colNum) / 256;
             for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                 HSSFRow currentRow;
                 //当前行未被使用过
                 if (sheet.getRow(rowNum) == null) {
                     currentRow = sheet.createRow(rowNum);
                 } else {
                     currentRow = sheet.getRow(rowNum);
                 }
                 if (currentRow.getCell(colNum) != null) {
                     HSSFCell currentCell = currentRow.getCell(colNum);
                     if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                         int length = currentCell.getStringCellValue().getBytes().length;
                         if (columnWidth < length) {
                             columnWidth = length;
                         }
                     }
                 }
             }
             if(colNum == 0){
                 sheet.setColumnWidth(colNum, (columnWidth-2) * 256);
             }else{
                 sheet.setColumnWidth(colNum, (columnWidth+4) * 256);
             }
         }
         
       
          
		try {
			 String fileName = "Excel-" + String.valueOf(System.currentTimeMillis()).substring(4, 13) + ".xls";
	         String headStr = "attachment; filename=\"" + fileName + "\"";
	         OutputStream out = response.getOutputStream();
	         response.setContentType("application/force-download");// 设置强制下载不打开
	         response.setContentType("APPLICATION/OCTET-STREAM");
	         response.setHeader("Content-Disposition", headStr);
			 workbook.write(out);
			 out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         
	}
	
	@SuppressWarnings({"deprecation" })
	public void excelImport(HttpServletRequest request,MultipartFile file,String tableName){
		 List<String> excelHeaderList=new ArrayList<String>();//表头集合
		 List<List<String>> excelDataList=new ArrayList<List<String>>();
		 String fileName=file.getOriginalFilename();
		try {
			InputStream in = file.getInputStream();
			Workbook workbook =null;
			String suffix=fileName.substring(fileName.indexOf("."));//获取后缀名
			if(".xls".equals(suffix)){ 
				workbook = new HSSFWorkbook(in);
			}else if(".xlsx".equals(suffix)){
				workbook = new XSSFWorkbook(in);
			}
			Sheet sheet = workbook.getSheetAt(0);
			Row firstRow=sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum()+1; //总行数
			int coloumNum=firstRow.getPhysicalNumberOfCells();//总列数
			for (int i = 0; i < coloumNum; i++) {
				String cellVlue=null;
            	if(firstRow.getCell(i)!=null){
            		String value=getStringcell(firstRow.getCell(i));
            		if(value!=null){
            			cellVlue=value;
            		}else{
            			firstRow.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
		            	cellVlue=firstRow.getCell(i).getRichStringCellValue().toString();
            		}
            			excelHeaderList.add(cellVlue);
               }else{
            	   excelHeaderList.add("");
               }
			}
			
			for(int i = 1; i < totalRowNum; i++){
				 //获取当前行的数据
	            Row row = sheet.getRow(i);
	            List<String>  list=new ArrayList<String>();
	            for (int j = 0; j < coloumNum; j++) {
	            	String cellVlue=null;
	            	if(row.getCell(j)!=null){
	            		String value=getStringcell(row.getCell(j));
	            		if(value!=null){
	            			cellVlue=value;
	            		}else{
	            			row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
			            	cellVlue=row.getCell(j).getRichStringCellValue().toString();
	            		}
	         	           list.add(cellVlue);
	            	
	               }else{
	            	   list.add("");
	               }
				}
	           excelDataList.add(list);
			}
			insertDataToDb(excelDataList,tableName,coloumNum);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
	
	
	public  void  insertDataToDb(List<List<String>> excelDataList,String tableName,Integer coloumNum){
		Connection conn=null;
		PreparedStatement pst = null;
		Statement statement=null;
		ResultSet rs=null;
		String tempTableName=tableName+"_TEMP";
		
		try {
			conn=this.getDbConnection();
			String sql="select * from "+tableName+" where rownum=1";
			pst=conn.prepareStatement(sql);
			rs=pst.executeQuery();
			ResultSetMetaData data=rs.getMetaData();
			int columnCount=data.getColumnCount();
			String creatTableSql="create table "+tempTableName+"(";
			while(rs.next()){
				for (int i = 1; i <= columnCount; i++) {
					String columnName=data.getColumnName(i);
					int columnType = data.getColumnType(i);
					int columnSize=data.getColumnDisplaySize(i);
					creatTableSql+=columnName;
					if(Types.VARCHAR==columnType){
						creatTableSql+="  varchar2("+columnSize+"),";
					}else if(Types.INTEGER==columnType){
						creatTableSql+=" number,";
					}else if(Types.DATE==columnType){
						creatTableSql+=" date,";
					}
					
				}
				creatTableSql=creatTableSql.substring(0,creatTableSql.length()-1)+")";
				pst=conn.prepareStatement(creatTableSql);
				pst.executeUpdate();
				statement=conn.createStatement();
				for (int i = 0; i<excelDataList.size(); i++) {
					List<String> columnDataList=excelDataList.get(i);
					String insertDataSql=" insert into "+tempTableName+"(";
					String valueSql=" values(";
					for (int j = 1; j  <= columnCount; j++) {
						String columnName=data.getColumnName(j);
						int columnType = data.getColumnType(j);
						if(columnCount<=coloumNum){
							insertDataSql+=columnName+",";
							if(Types.VARCHAR==columnType){
								valueSql+="'"+columnDataList.get(j-1)+"',";
							}else if(Types.INTEGER==columnType){
								valueSql+=columnDataList.get(j-1)+",";
							}else if(Types.DATE==columnType){
								valueSql+="to_date('"+columnDataList.get(j-1)+"','yyyy-mm-dd')"+",";
							}
						}else if(columnCount>coloumNum){
							if(j<coloumNum){
								insertDataSql+=columnName+",";
								if(Types.VARCHAR==columnType){
									valueSql+="'"+columnDataList.get(j-1)+"',";
								}else if(Types.INTEGER==columnType){
									valueSql+=columnDataList.get(j-1)+",";
								}else if(Types.DATE==columnType){
									valueSql+="to_date('"+columnDataList.get(j-1)+"','yyyy-mm-dd')"+",";
								}
							}
						}
					}
					
					insertDataSql=insertDataSql.substring(0,insertDataSql.length()-1)+")"+valueSql.substring(0,valueSql.length()-1)+")";
					statement.addBatch(insertDataSql);
				}
				
				statement.executeBatch();
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.DbCloseQuery(rs,pst);
			try {
				if(statement!=null){
					statement.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DbUtil.DbCloseConnection(conn);
		}
	}
	
	public String getStringcell(Cell cell){
		String value=null;
		if("yyyy/mm/dd".equals(cell.getCellStyle().getDataFormatString()) || "m/d/yy".equals(cell.getCellStyle().getDataFormatString())
		        || "yy/m/d".equals(cell.getCellStyle().getDataFormatString()) || "mm/dd/yy".equals(cell.getCellStyle().getDataFormatString())
		        || "dd-mmm-yy".equals(cell.getCellStyle().getDataFormatString())|| "yyyy/m/d".equals(cell.getCellStyle().getDataFormatString())){
			value= new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
		}
		return value;
	}
	
	
	
	
	
	
	 /*  
     * 列数据信息单元格样式
     */  
      @SuppressWarnings("deprecation")
	public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
            // 设置字体
            HSSFFont font = workbook.createFont();
            //设置字体大小
            //font.setFontHeightInPoints((short)10);
            //字体加粗
            //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            //设置字体名字 
            font.setFontName("Courier New");
            //设置样式; 
            HSSFCellStyle style = workbook.createCellStyle();
            //设置底边框; 
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            //设置底边框颜色;  
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            //设置左边框;   
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            //设置左边框颜色; 
            style.setLeftBorderColor(HSSFColor.BLACK.index);
            //设置右边框; 
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            //设置右边框颜色; 
            style.setRightBorderColor(HSSFColor.BLACK.index);
            //设置顶边框; 
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //设置顶边框颜色;  
            style.setTopBorderColor(HSSFColor.BLACK.index);
            //在样式用应用设置的字体;  
            style.setFont(font);
            //设置自动换行; 
            style.setWrapText(false);
            //设置水平对齐的样式为居中对齐;  
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //设置垂直对齐的样式为居中对齐; 
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
           
            return style;
      
      }
      
      /* 
       * 列头单元格样式
       */    
        @SuppressWarnings("deprecation")
		public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {
            
              // 设置字体
            HSSFFont font = workbook.createFont();
            //设置字体大小
            font.setFontHeightInPoints((short)11);
            //字体加粗
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            //设置字体名字 
            font.setFontName("Courier New");
            //设置样式; 
            HSSFCellStyle style = workbook.createCellStyle();
            //设置底边框; 
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            //设置底边框颜色;  
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            //设置左边框;   
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            //设置左边框颜色; 
            style.setLeftBorderColor(HSSFColor.BLACK.index);
            //设置右边框; 
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            //设置右边框颜色; 
            style.setRightBorderColor(HSSFColor.BLACK.index);
            //设置顶边框; 
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //设置顶边框颜色;  
            style.setTopBorderColor(HSSFColor.BLACK.index);
            //在样式用应用设置的字体;  
            style.setFont(font);
            //设置自动换行; 
            style.setWrapText(false);
            //设置水平对齐的样式为居中对齐;  
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //设置垂直对齐的样式为居中对齐; 
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            
            return style;
            
        }
}
