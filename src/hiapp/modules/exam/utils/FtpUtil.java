package hiapp.modules.exam.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {
	private static final String basePath="/ftp/upload";
	private static final String host="192.168.0.251";
	private static final Integer port=21;
	private static final String userName="admin";
	private static final String passWord="1q2w3e4r";
	
	public static boolean uploadFile(String filePath,String fileName ,InputStream in){
		boolean result = uploadFile(host,port,userName,passWord,basePath,filePath,fileName,in);
		return result;
	}
	
	public static boolean downloadFromFTP(String path, String filename,HttpServletResponse response){
		boolean result = downloadFromFTP(host,port,userName,passWord,path,filename,response);
		return result;
	}
	
	/**  
     * Description: 向FTP服务器上传文件  
     * @param host FTP服务器hostname  
     * @param port FTP服务器端口  
     * @param username FTP登录账号  
     * @param password FTP登录密码  
     * @param basePath FTP服务器基础目录 
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath 
     * @param filename 上传到FTP服务器上的文件名  
     * @param input 输入流  
     * @return 成功返回true，否则返回false  
     */    
    public static boolean uploadFile(String host, int port, String username, String password, String basePath,  
            String filePath,String fileName ,InputStream in) {  
        boolean result = false;  
        FTPClient ftp = new FTPClient();  
        try {  
            int reply;  
            ftp.connect(host, port);// 连接FTP服务器  
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器  
            ftp.login(username, password);// 登录  
            reply = ftp.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftp.disconnect();  
                return result;  
            }  
            
            
            if(!ftp.changeWorkingDirectory(basePath)) {
                String[] arr=basePath.split("/");
                String dirs="";
                for (int i = 0; i < arr.length; i++) {
    				String dir="/"+arr[i];
    				if(arr[i]==null||"".equals(arr[i])) {
    					continue;
    				}
    				dirs+=dir;
    				if(!ftp.changeWorkingDirectory(dirs)) {
    					if(!ftp.makeDirectory(dirs)) {
    						 return result;  
    					}else {
    						ftp.changeWorkingDirectory(dirs); 
    					}
    				}
    			}
            }
      
            if(!ftp.changeWorkingDirectory(basePath+filePath)) {
            	  if (!ftp.makeDirectory(basePath+filePath)) {  
                      return result;  
                  } else {  
                      ftp.changeWorkingDirectory(basePath+filePath);  
                  } 
            }

            //设置上传文件的类型为二进制类型  
            ftp.setFileType(FTP.BINARY_FILE_TYPE);  
            //上传文件  
            ftp.enterLocalPassiveMode();  
            ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);  
    		ftp.storeFile(new String(fileName.getBytes("GBK"),"iso-8859-1"), in);
    		in.close();
            ftp.logout();  
            result = true;  
        } catch (IOException e) {  
            e.printStackTrace(); 
            result=false;
        } finally {  
            if (ftp.isConnected()) {  
                try {  
                    ftp.disconnect();  
                } catch (IOException ioe) {  
                }  
            }  
        }  
        return result;  
    } 
    
    /**
     * 下载文件
     * @param url
     * @param port
     * @param username
     * @param password
     * @param path
     * @param fileName
     * @param response
     * @return
     */
    public static boolean downloadFromFTP(String url,int port,String username,String password,String path,String fileName,HttpServletResponse response){
    	boolean result=false;
    	FTPClient ftp=new FTPClient();
    	InputStream is=null;
    	OutputStream out=null;
    	try {
    		int reply;  
			ftp.connect(url,port);
			ftp.login(username, password);// 登录  
			ftp.enterLocalActiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			reply=ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply))
			{
				ftp.disconnect();
				return result;
			} 
			ftp.changeWorkingDirectory(basePath+"/"+path);
			ftp.enterLocalPassiveMode();
			is=ftp.retrieveFileStream(fileName);
		    byte[] buffer = new byte[is.available()];  
		    is.read(buffer);
			out=response.getOutputStream();
			out.write(buffer);
			is.close();
			out.flush();  
			out.close();
			ftp.logout();
			result=true;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
    	return result;
    }
    
    
    /**
     * 获取ftp文件流
     * @param url
     * @param port
     * @param username
     * @param password
     * @param path
     * @param fileName
     * @param response
     * @return
     */
    public static InputStream getFtpInputStream(String fileAdress){
    	FTPClient ftp=new FTPClient();
    	InputStream is=null;
    	try {
    		int reply;  
			ftp.connect(host,port);
			ftp.login(userName, passWord);// 登录  
			ftp.enterLocalActiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			reply=ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply))
			{
				ftp.disconnect();
				return null;
			} 
		    String path=fileAdress.substring(0, fileAdress.lastIndexOf("/"));
		    String fileName=fileAdress.substring(fileAdress.lastIndexOf("/")+1);
			ftp.changeWorkingDirectory(basePath+path);
			ftp.enterLocalPassiveMode();
			is=ftp.retrieveFileStream(new String(fileName.getBytes("GBK"), "iso-8859-1"));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
    	return is;
    }
    
    /**
     * 获取ftp文件流
     * @param url
     * @param port
     * @param username
     * @param password
     * @param path
     * @param fileName
     * @param response
     * @return
     */
    public static String getExamFile(String fileAdress,String examPath,String questionId){
    	FTPClient ftp=new FTPClient();
    	InputStream is=null;
    	String newName=null;
    	try {
    		int reply;  
			ftp.connect(host,port);
			ftp.login(userName, passWord);// 登录  
			ftp.enterLocalActiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			reply=ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply))
			{
				ftp.disconnect();
				return null;
			} 
		    String path=fileAdress.substring(0, fileAdress.lastIndexOf("/"));
		    String fileName=fileAdress.substring(fileAdress.lastIndexOf("/")+1);
		    String suffix=fileAdress.substring(fileAdress.lastIndexOf("."));
			ftp.changeWorkingDirectory(basePath+path);
			ftp.enterLocalPassiveMode();
			is=ftp.retrieveFileStream(new String(fileName.getBytes("GBK"), "iso-8859-1"));
			newName=questionId+suffix;
			File file=new File(examPath);
			byte[] buff = new byte[8192];
			OutputStream out =new FileOutputStream(file);
			int count = 0;
		    while ( (count = is.read(buff)) != -1) {
		    	  out.write(buff, 0, count);
		    }
		    File file1=new File(examPath+File.separator+fileName);
		    file1.renameTo(new File(examPath+File.separator+newName));
		    out.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(ftp.isConnected()){
				try {
					ftp.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	return newName;
    }
    /**
     * 删除文件
     * @param fileAdress
     * @return
     */
    public static boolean deleteFileFromFTP(String fileAdress) {
    	  boolean result = false;  
          FTPClient ftp = new FTPClient(); 
          int reply;  
          try {
			ftp.connect(host, port);
			ftp.login(userName, passWord);// 登录  
		    reply = ftp.getReplyCode();  
            if (!FTPReply.isPositiveCompletion(reply)) {  
                ftp.disconnect();  
                return result;  
            }  
	      String path=fileAdress.substring(0, fileAdress.lastIndexOf("/"));
	      String fileName=fileAdress.substring(fileAdress.lastIndexOf("/")+1);
	      if(ftp.changeWorkingDirectory(path)) {
	    	  result = ftp.deleteFile(new String(fileName.getBytes("GBK"), "iso-8859-1")); 
	      } 
	     
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 连接FTP服务器  
          // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器  
          
          return result;
    }
    
    public static String setResponse(HttpServletResponse response,String fileName) throws UnsupportedEncodingException {
    	fileName=new String(fileName.getBytes("GBK"), "iso-8859-1");
        String headStr = "attachment; filename=\"" + fileName + "\"";
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", headStr);
        return fileName;
    }
}
