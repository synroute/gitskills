package hiapp.modules.exam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class WordToHtml {
	     
	     /**
	      * 2007版本word转换成html 2017-2-27
	      * @param wordPath word文件路径
	      * @param wordName word文件名称无后缀
	      * @param suffix  word文件后缀
	      * @return
	      * @throws IOException
	      */
	     public static String Word2007ToHtml(InputStream in,String htmlPath) throws IOException {
	       //String htmlPath = wordPath + File.separator + wordName + "_show" + File.separator;
	       String htmlName =  UUID.randomUUID().toString()+".html";
	       String imagePath = htmlPath + "image" +"/";
	        
	       //判断html文件是否存在
	       File htmlFile = new File(htmlPath + htmlName);
	       if(htmlFile.exists()){ 
	    	   htmlFile.delete();
	       }
	            
/*	       //word文件
	       File wordFile = new File(wordPath); 
	        
	       // 1) 加载word文档生成 XWPFDocument对象 
	       InputStream in = new FileInputStream(wordFile); */
	       XWPFDocument document = new XWPFDocument(in); 
	    
	       // 2) 解析 XHTML配置 (这里设置IURIResolver来设置图片存放的目录) 
	       File imgFolder = new File(imagePath);
	       XHTMLOptions options = XHTMLOptions.create();
	       options.setExtractor(new FileImageExtractor(imgFolder));
	       //html中图片的路径 相对路径 
	       options.URIResolver(new BasicURIResolver("image"));
	       options.setIgnoreStylesIfUnused(false); 
	       options.setFragment(true); 
	        
	       // 3) 将 XWPFDocument转换成XHTML
	       //生成html文件上级文件夹
	       File folder = new File(htmlPath);
	       if(!folder.exists()){ 
	         folder.mkdirs(); 
	       }
	       OutputStream out = new FileOutputStream(htmlFile); 
	       XHTMLConverter.getInstance().convert(document, out, options);
	        
	       return "/word/"+htmlName;
	     } 
	     
	     /**  
	      * 转换doc  
	      * @param filePath  
	      * @param fileName  
	      * @param htmlName  
	      * @throws Exception  
	      */  
	     public static String dox(String filePath ,String htmlPath) throws Exception{  
	    	 String htmlName="doc.html";
	    	 String imagePath = htmlPath + "image" +"/";
	    	 
		        File folder = new File(htmlPath.substring(0, htmlPath.length()-1));
			       if(folder.exists()){ 
			    	   delFolder(htmlPath.substring(0, htmlPath.length()-1));
			         folder.mkdirs();
			     }else {
			    	 folder.mkdirs();
			     }
			   File htmlFile = new File(htmlPath + htmlName);
		       File imageFolder = new File(imagePath);
		       if(!imageFolder.exists()){ 
		    	   imageFolder.mkdirs(); 
		     } 
	         InputStream input = new FileInputStream(new File(filePath));  
	         HWPFDocument wordDocument = new HWPFDocument(input);  
	         WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());  
	    
	         wordToHtmlConverter.setPicturesManager( new PicturesManager()  
	         {  
	             public String savePicture( byte[] content,  
	                     PictureType pictureType, String suggestedName,  
	                     float widthInches, float heightInches )  
	             {  
	                 return "image/"+suggestedName;  
	             }  
	         } );  
	         //解析word文档  
	        wordToHtmlConverter.processDocument(wordDocument);  
	        //save pictures  
	        List pics=wordDocument.getPicturesTable().getAllPictures();  
	        if(pics!=null){  
	            for(int i=0;i<pics.size();i++){  
	                Picture pic = (Picture)pics.get(i);  
	                System.out.println();  
	                try {  
	                    pic.writeImageContent(new FileOutputStream(imagePath  
	                            + pic.suggestFullFileName()));  
	                } catch (FileNotFoundException e) {  
	                    e.printStackTrace();  
	                }    
	            }  
	        }  
	        Document htmlDocument = wordToHtmlConverter.getDocument();  

	        OutputStream outStream = new FileOutputStream(htmlFile);  
	        DOMSource domSource = new DOMSource(htmlDocument);  
	        StreamResult streamResult = new StreamResult(outStream);  
	   
	        TransformerFactory factory = TransformerFactory.newInstance();  
	        Transformer serializer = factory.newTransformer();  
	        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");  
	        serializer.setOutputProperty(OutputKeys.INDENT, "yes");  
	        serializer.setOutputProperty(OutputKeys.METHOD, "html");  
	          
	        serializer.transform(domSource, streamResult);  
	        outStream.close();  
	        return "/doc/"+htmlName;
	    }   
	     
	     
	   //删除文件夹  
	     public static void delFolder(String folderPath) {  
	          try {  
	             delAllFile(folderPath); //删除完里面所有内容  
	             String filePath = folderPath;  
	             filePath = filePath.toString();  
	             java.io.File myFilePath = new java.io.File(filePath);  
	             myFilePath.delete(); //删除空文件夹  
	          } catch (Exception e) {  
	            e.printStackTrace();   
	          }  
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
	            if (temp.isDirectory()) {  
	               delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件  
	               delFolder(path + "/" + tempList[i]);//再删除空文件夹  
	               flag = true;  
	            }  
	         }  
	         return flag;  
	       }  
	  }  

