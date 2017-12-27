package hiapp.modules.dm.dbImport.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.channels.SelectableChannel;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hiapp.modules.dm.dbImport.bean.ImportConfig;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.utils.DbUtil;
import hiapp.utils.base.HiAppContext;
import hiapp.utils.database.BaseRepository;
import com.csvreader.CsvReader;




@Repository
public class DMDbImport  extends BaseRepository {
	@Autowired
	private DataImportJdbc dataImportJdbc;
	
	
	List<ImportConfig> listImportConfig=new ArrayList<ImportConfig>();
	int listCount=0;
	
	public static Timer timer = new Timer();
	public static Timer starttimer = new Timer();
	Connection dbConn=null;
	public static String path="";
	@Autowired
    public void setDBConnectionPool(HiAppContext appContext) {
    	path=appContext.getServletContext().getRealPath("maxTime/maxTime.properties");
    	
    	System.out.println(path);
    	//readcsv();
    	start();
    }
	
	
	 public FTPClient ftp(String ip, String user, String password) {  
		  
	        FTPClient ftpClient = new FTPClient();  
	        try {  
	            ftpClient.connect(ip);  
	            ftpClient.login(user, password);  
	        } catch (SocketException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	  
	        if (!ftpClient.isConnected()) {  
	            ftpClient = null;  
	        }  
	  
	        return ftpClient;  
	    }  
   
  
    /** 
     * <b>将一个IO流解析，转化数组形式的集合<b> 
     *  
     * @param in 文件inputStream流 
     * @throws SQLException 
     */  
    public ArrayList<String[]> csv(InputStream in) {  
        ArrayList<String[]> csvList = new ArrayList<String[]>();  
        if (null != in) {  
        	CsvReader reader = new CsvReader(in, ',', Charset.forName("GBK"));  
            try {  
            	Statement stmt = null;
            	
				dbConn=this.getDbConnection();
				dbConn.setAutoCommit(false);  

	       		stmt = dbConn.createStatement();
				
            	reader.readHeaders();
                //遍历每一行，若有#注释部分，则不处理，若没有，则加入csvList  
            	
            	
                while (reader.readRecord()) { 
                	
                	reader.getRawRecord(); 
                	String sql="insert into biao  values('"+reader.get(0)+"','"+reader.get("项目名称")+"','"+reader.get("项目编号")+"','"+reader.get("客户名称")+"','"+reader.get("证件号码")+"','"+reader.get("产品名称")+"','"+reader.get("产品类型")+"','"+reader.get("贷款金额")+"','"+reader.get("放款日期")+"','"+reader.get("贷款期限")+"','"+reader.get("贷款用途")+"','"+reader.get("居住地址")+"','"+reader.get("手机号")+"','"+reader.get("还款账号")+"','"+reader.get("还款账号所属银行")+"','"+reader.get("还款日期")+"','"+reader.get("月还款日")+"','"+reader.get("月还款金额")+"','"+reader.get("合作机构")+"','"+reader.get("押品类型")+"','"+reader.get("押品描述")+"');";
                	stmt.addBatch("insert into biao  values ('"+reader.get(0)+"','"+reader.get("项目名称")+"','"+reader.get("项目编号")+"','"+reader.get("客户名称")+"','"+reader.get("证件号码")+"','"+reader.get("产品名称")+"','"+reader.get("产品类型")+"','"+reader.get("贷款金额")+"','"+reader.get("放款日期")+"','"+reader.get("贷款期限")+"','"+reader.get("贷款用途")+"','"+reader.get("居住地址")+"','"+reader.get("手机号")+"','"+reader.get("还款账号")+"','"+reader.get("还款账号所属银行")+"','"+reader.get("还款日期")+"','"+reader.get("月还款日")+"','"+reader.get("月还款金额")+"','"+reader.get("合作机构")+"','"+reader.get("押品类型")+"','"+reader.get("押品描述")+"')");

                
                }  
                

       		 stmt.executeBatch();

       		dbConn.commit();  //�ύ����

       		 //long end = System.currentTimeMillis();
       		 
            } catch (IOException e) {  
                e.printStackTrace();  
            }   catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  
            reader.close();  
        }  
        return csvList;  
    }  
	
    
    public void readcsv()
    {
    	 FTPClient ftp1 = ftp("192.168.0.193", "synroute", "1qaz@WSX");
    	 if (null != ftp1) {
    		 try {  
    		  
             // 更改当前工作目录,zgg为文件所在的目录  
             ftp1.changeWorkingDirectory("up");  

             // 从ftp上获取ggw目录下的文件  
             FTPFile[] file = ftp1.listFiles();  

             // 遍历所有文件，匹配需要查找的文件         
             String yearMonth =new SimpleDateFormat("yyyyMMdd").format(new Date());
             String fileName = "csv.csv";
             for (int i = 0; i < file.length; i++) {  
            	   
                 // 匹配到则进入  
                 if (file[i].getName().contains(fileName)) {  
                     // 将匹配到的文件流传入接口，转化成数组集合  
                     ArrayList<String[]> csvList = csv(ftp1  
                             .retrieveFileStream(file[i].getName()));  

                     // 将csv文件里的数据打印出来  
                     // 遍历每一行  
                     for (int row = 0; row < csvList.size(); row++) {  
                         // 遍历每一行中的每一列  
                         for (int j = 0; j < csvList.get(row).length; j++) {  
                             System.out.print(csvList.get(row)[j] + "\t|");  
                         }  
                         System.out.println();  
                     }  
                 }  
             }  
         } catch (IOException e) {  
             e.printStackTrace();  
         }  
     }  
    }
	
	
	
	public void start(){
		
		
		TimerTask starttask = new TimerTask() {
	        @Override
	        public void run() {
	        	getlist();
	        	 startFor();
	        }
	    };
	   
	    
		 //设置执行时间
        Calendar startcalendar = Calendar.getInstance();
        int startyear = startcalendar.get(Calendar.YEAR);
        int startmonth = startcalendar.get(Calendar.MONTH);
        int startday = startcalendar.get(Calendar.DAY_OF_MONTH);//每天
        //定制每天的21:09:00执行，
        startcalendar.set(startyear, startmonth, startday, 05, 00, 00);
        Date startdate = startcalendar.getTime();
        
        int period = 24 * 3600000;
        //每天的date时刻执行task，每隔2秒重复执行
        starttimer.schedule(starttask, startdate, period);
        
       
       
        
	}
	
	public void startFor(){
		timer.cancel();
		timer = new Timer();
        for(int i=0;i<listImportConfig.size();i++)
		{
        	final ImportConfig importConfig=listImportConfig.get(i);
        	TimerTask task = new TimerTask() {
    	        @Override
    	        public void run() {
    	        	try {
    	        		String paths=path;
    	        		int s=importConfig.getTemplateId();
						dataImportJdbc.insertDataByDb(importConfig.getTemplateId(),importConfig.getBizId(),path,"0","DB");
					} catch (NumberFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        }
    	    };
    	   
    	    
    		 //设置执行时间
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
            
            Date date = calendar.getTime();
            
            int periods = importConfig.getIntervalTime() * 60000;
            //每天的date时刻执行task，每隔2秒重复执行
            timer.schedule(task, date, periods);
			
		}
	}
	
	
	public boolean add(int bizid,int templateId,int intervalTime){
		try{
			ImportConfig importConfig=new ImportConfig();
			importConfig.setBizId(bizid);
			importConfig.setTemplateId(templateId);
			importConfig.setIntervalTime(intervalTime);
			listImportConfig.add(importConfig);
			startFor();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		} finally {
			
		}
		return true;
	}

	public boolean delete(int bizid,int templateId){
		try{
			for(int i=0;i<listImportConfig.size();i++)
			{
				ImportConfig importConfig=listImportConfig.get(i);
				if (importConfig.getBizId()==bizid&&importConfig.getTemplateId()==templateId) {
					listImportConfig.remove(i);
					startFor(); 
					return true;
				}
				
			}
			return false;
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		} finally {
			
		}
		
	}
	
	private List<ImportConfig> getlist()
	{
	
		listImportConfig=new ArrayList<ImportConfig>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			dbConn=this.getDbConnection();
		String sql="select BusinessID,TemplateID,Xml from HASYS_DM_BIZTEMPLATEIMPORT where SourceType='DB'";
		stmt = dbConn.prepareStatement(sql);
		rs = stmt.executeQuery();
		while(rs.next())
		{
			
			if(rs.getString(3)!=null)
			{
				JsonObject jsonObject=new JsonParser().parse(rs.getString(3)).getAsJsonObject();
				if(jsonObject.has("ImportConfig"))
				 {
					JsonObject jsonObject_ImportConfig=jsonObject.get("ImportConfig").getAsJsonObject();
					if(jsonObject_ImportConfig.get("IsStart").getAsInt()==1)
					{
						ImportConfig importConfig=new ImportConfig();
						importConfig.setBizId(rs.getInt(1));
						importConfig.setTemplateId(rs.getInt(2));
						importConfig.setIntervalTime(jsonObject_ImportConfig.get("Time").getAsInt());
						listImportConfig.add(importConfig);
					}
				 }
			}
		}
		/*listCount=listImportConfig.size();
		 for(int i=0;i<listImportConfig.size();i++)
			{
	        	ImportConfig importConfig=listImportConfig.get(i);
	        	TimerTask task = new TimerTask() {
	    	        @Override
	    	        public void run() {
	    	        	try {
							dataImportJdbc.insertDataByDb(importConfig.getBizId(), importConfig.getTemplateId(),path,"0","DB");
						} catch (NumberFormatException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	    	        }
	    	    };
	    	   
	    	    
	    		 //设置执行时间
	            Calendar calendar = Calendar.getInstance();
	            int year = calendar.get(Calendar.YEAR);
	            int month = calendar.get(Calendar.MONTH);
	            int day = calendar.get(Calendar.DAY_OF_MONTH);//每天
	            
	            Date date = calendar.getTime();
	            Timer timer = new Timer();
	            int periods = importConfig.getIntervalTime() * 60000;
	            //每天的date时刻执行task，每隔2秒重复执行
	            timer.schedule(task, date, periods);
				
			}*/
		} catch (SQLException e) {
				e.printStackTrace();
			} finally {
			
			DbUtil.DbCloseExecute(stmt);
		}
		
		return listImportConfig;
		
	}
	
}
