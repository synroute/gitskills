package hiapp.modules.exam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;


public class OfficeToPdfUtil {
	 /**
	   * 转换文件成html
	   * 
	   * @param fromFileInputStream:
	   * @throws IOException 
	   */
	  public static void file2Html(InputStream fromFileInputStream, String toFilePath,String fileName,String type) throws IOException {
	    Date date = new Date();
	    delAllFile(toFilePath);//删除文件
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String ftpPathName="/"+sdf.format(date)+"pdf";
	    String docFileName = null;
	    String pdfName = null;
	    if("doc".equals(type)){
	      docFileName =  fileName + ".doc";
	      pdfName = fileName + ".html";
	    }else if("docx".equals(type)){
	      docFileName = fileName + ".docx";
	      pdfName = fileName + ".pdf";
	    }else if("xls".equals(type)){
	      docFileName =  fileName + ".xls";
	      pdfName =  fileName + ".pdf";
	    }else if("ppt".equals(type)){
	      docFileName =  fileName + ".ppt";
	      pdfName = fileName + ".pdf";
	    }else if("pptx".equals(type)){
	        docFileName = fileName+ ".pptx";
	        pdfName = fileName+ ".pdf";
	    }
	    String endPath=toFilePath +File.separatorChar + pdfName;
	    File htmlOutputFile = new File(endPath);
	    File docInputFile = new File(toFilePath + File.separatorChar + docFileName);
	    if (htmlOutputFile.exists())
	      htmlOutputFile.delete();
	    htmlOutputFile.createNewFile();
	    if (docInputFile.exists())
	      docInputFile.delete();
	    docInputFile.createNewFile();
	    /**
	     * 由fromFileInputStream构建输入文件
	     */
	    try {
	      OutputStream os = new FileOutputStream(docInputFile);
	      int bytesRead = 0;
	      byte[] buffer = new byte[1024 * 8];
	      while ((bytesRead = fromFileInputStream.read(buffer)) != -1) {
	        os.write(buffer, 0, bytesRead);
	      }
	 
	      os.close();
	      fromFileInputStream.close();
	    } catch (IOException e) {
	    }

	   OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
	    
	    try {
	     connection.connect();
	    } catch (ConnectException e) {
	      System.err.println("文件转换出错，请检查OpenOffice服务是否启动。");
	    }
	    // convert
	    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
	   converter.convert(docInputFile, htmlOutputFile);
	    // 转换完之后删除word文件
	    docInputFile.delete();
	    connection.disconnect();
	    InputStream  pdfIn=new FileInputStream(htmlOutputFile);
	    FtpUtil.uploadFile(ftpPathName, pdfName, pdfIn);

	  }
	  
	  public static boolean delAllFile(String path) {  
	         boolean flag = false;  
	         File file = new File(path);  
	         if (!file.exists()) {  
	           return flag;  
	         }  
	         if (!file.isDirectory()) {  
	           return flag;  
	         }  
	         String[] tempList = file.list();  
	         File temp = null;  
	         for (int i = 0; i < tempList.length; i++) {  
	            if (path.endsWith(File.separator)) {  
	               temp = new File(path + tempList[i]);  
	            } else {  
	                temp = new File(path + File.separator + tempList[i]);  
	            }  
	            if (temp.isFile()) {  
	               temp.delete();  
	            }  
	        /*    if (temp.isDirectory()) {  
	               delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件  
	               flag = true;  
	            } */ 
	         }  
	         return flag;  
	       }  
}
