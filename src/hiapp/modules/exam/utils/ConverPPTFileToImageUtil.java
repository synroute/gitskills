package hiapp.modules.exam.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class ConverPPTFileToImageUtil {
	
	@SuppressWarnings("resource")
	public static List<String> toImage2007() throws Exception{  
        FileInputStream is = new FileInputStream("C:/Users/李远/Desktop/WWW.pptx");  
        XMLSlideShow ppt = new XMLSlideShow(is);  
        is.close();  
        Dimension pgsize = ppt.getPageSize();  
        System.out.println(pgsize.width+"--"+pgsize.height);  
        List<String> list=new ArrayList<String>();
        for (int i = 0; i < ppt.getSlides().size(); i++) {  
            try {  
                //防止中文乱码  
                for(XSLFShape shape : ppt.getSlides().get(i).getShapes()){  
                    if(shape instanceof XSLFTextShape) {  
                        XSLFTextShape tsh = (XSLFTextShape)shape;  
                        for(XSLFTextParagraph p : tsh){  
                            for(XSLFTextRun r : p){  
                                r.setFontFamily("宋体");  
                            }  
                        }  
                    }  
                }  
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);  
                Graphics2D graphics = img.createGraphics();  
                // clear the drawing area  
                graphics.setPaint(Color.white);  
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));  
                // render  
                ppt.getSlides().get(i).draw(graphics);  
                // save the output  
    /*            String path="C:/Users/李远/Desktop/ppt/";
                String filename = path + (i+1) + ".jpg";  
                File jpegFile = new File(filename);
                OutputStream out = new FileOutputStream(jpegFile);*/
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageOutputStream imageOutput = ImageIO.createImageOutputStream(byteArrayOutputStream);
                javax.imageio.ImageIO.write(img, "jpeg", imageOutput);  
                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
               
               String a= dowloadImage(inputStream);
               list.add(a);
               imageOutput.close();  
               // out.close();
                inputStream.close();
                imageOutput.close();
            } catch (Exception e) {  
                System.out.println("第"+i+"张ppt转换出错");  
            }  
        }
        return list;
}  
	public static List<String> toImage2003(){  
		 List<String> list=new ArrayList<String>();
        try {  
            HSLFSlideShow ppt = new HSLFSlideShow(new HSLFSlideShowImpl("D:/demo/22.ppt"));  
              
            Dimension pgsize = ppt.getPageSize();  
            for (int i = 0; i < ppt.getSlides().size(); i++) {  
                //防止中文乱码  
                for(HSLFShape shape : ppt.getSlides().get(i).getShapes()){  
                    if(shape instanceof HSLFTextShape) {  
                        HSLFTextShape tsh = (HSLFTextShape)shape;  
                        for(HSLFTextParagraph p : tsh){  
                            for(HSLFTextRun r : p){  
                                r.setFontFamily("宋体");  
                            }  
                        }  
                    }  
                }  
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);  
                Graphics2D graphics = img.createGraphics();  
                // clear the drawing area  
                graphics.setPaint(Color.white);  
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));  
                  
                // render  
                ppt.getSlides().get(i).draw(graphics);  
                  
                // save the output  
             
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageOutputStream imageOutput = ImageIO.createImageOutputStream(byteArrayOutputStream);
                javax.imageio.ImageIO.write(img, "png", imageOutput);  
                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                
                String base64= dowloadImage(inputStream);
                list.add(base64);
                imageOutput.close();  
                inputStream.close();
//              resizeImage(filename, filename, width, height);  
                  
            }  
            System.out.println("3success");  
        } catch (Exception e) {  
           e.printStackTrace();
        }  
        
        return list;
    }  
     /***   
     * 功能 :调整图片大小  
     * @param srcImgPath 原图片路径   
     * @param distImgPath  转换大小后图片路径   
     * @param width   转换后图片宽度   
     * @param height  转换后图片高度   
     */    
    public static void resizeImage(String srcImgPath, String distImgPath,    
            int width, int height) throws IOException {    
    
        File srcFile = new File(srcImgPath);    
        Image srcImg = ImageIO.read(srcFile);    
        BufferedImage buffImg = null;    
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
        buffImg.getGraphics().drawImage(    
                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,    
                0, null);    
    
        ImageIO.write(buffImg, "JPEG", new File(distImgPath));    
    
    }    
	
	
	
	public static String dowloadImage(InputStream inputStream) throws IOException{
		byte[] data = null;
		data = new byte[inputStream.available()];
		inputStream.read(data);
		inputStream.close();
	    BASE64Encoder encoder = new BASE64Encoder();
		String a=encoder.encode(data);
		return a;
	}
	
	 public static void main(String[] args) throws Exception {
		 toImage2007();
	}
}
