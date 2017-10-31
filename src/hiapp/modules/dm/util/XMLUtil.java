package hiapp.modules.dm.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.*;

import com.google.gson.Gson;
import hiapp.modules.dm.multinumbermode.bo.BizConfig;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

public class XMLUtil {

    public  static void main(String[] args  ) {
//        String str = bulidxml();
//
//        parseXml(str);
//
//        parseXml("<Msg JobId=\"13\" Count=\"10\"></Msg>");

        BizConfig cfg = new BizConfig();
        cfg.setBusinessID("1");
        cfg.setDialMode("6");
        cfg.setDialPrefix("88");
        cfg.setDialRatio("8");
        cfg.setIVRScript("ivrscript");
        cfg.setMaxCallingCount("1");
        cfg.setMaxCountPerTimeUnit("2");
        cfg.setMaxRingCount("1");
        cfg.setServiceNo("9");
        cfg.setTimeUnitLong("2");
        cfg.setMaxReadyAgentCount("5");

        Map<String, String> map = new HashMap<String, String>();
        map.put("TimeStart", "07:30");
        map.put("TimeEnd", "18:00");
        cfg.setPermissionCallTime(map);

        //List<String> shareBatchIds = new Gson().fromJson(strShareBatchIds, List.class);
        String str = new Gson().toJson(cfg, BizConfig.class);
        System.out.println(str);

        BizConfig cfg2 = new Gson().fromJson(str, BizConfig.class);
        System.out.println(cfg2);

        Date date = new Date("2017/10/31 07:30:00");

        if ("18:30:00".compareTo("18:00:00")>0)
            System.out.println("1");
        else
            System.out.println("2");

        System.out.println(date);
    }

    /**
     * 创建xml文件
     */
    public static String bulidxml(){
        try {
            Document doc = new Document();

            Element root = new Element("根元素");
            doc.setRootElement(root);

            Element el1 = new Element("元素一");
            el1.setAttribute("属性", "属性一");
            Text text1 = new Text("元素值");
            Text text2 = new Text("元素值2");

            Element em = new Element("元素二").addContent("第二个元素");
            el1.addContent(text1);
            el1.addContent(em);
            el1.addContent(text2);

            Element el2 = new Element("元素三").addContent("第三个元素");

            root.addContent(el1);
            root.addContent(el2);

            XMLOutputter outputter = null;
            Format format = Format.getCompactFormat();
            format.setEncoding("UTF-8");
            format.setIndent("    ");
            outputter = new XMLOutputter(format);

            //outputter.output(doc, new FileOutputStream("/Users/eight/tmp/a.xml"));
            ByteArrayOutputStream byteRsp = new ByteArrayOutputStream();
            outputter.output(doc, byteRsp);

            String str = byteRsp.toString("UTF-8");
            System.out.println(str);

            return str;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 解析xml字符串成List<Map>
     *
     * @param
     * @return List
     */
    public static List parseXml(String xmlDoc) {
        // 创建一个新的字符串
        StringReader xmlString = new StringReader(xmlDoc);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(xmlString);
        // 创建一个新的SAXBuilder
        SAXBuilder saxb = new SAXBuilder();

        List result = null;
        try {
            result = new ArrayList();
            // 通过输入源构造一个Document
            Document doc = saxb.build(source);
            // 取的根元素
            Element root = doc.getRootElement();

            // 得到根元素所有子元素的集合
            List node = root.getChildren();
            Element et = null;
            for (int i = 0; i < node.size(); i++) {
                et = (Element) node.get(i);// 循环依次得到子元素
                List subNode = et.getChildren(); // 得到内层子节点
                Map map = new HashMap();
                Element subEt = null;
                for (int j = 0; j < subNode.size(); j++) {
                    subEt = (Element) subNode.get(j); // 循环依次得到子元素
                    map.put(subEt.getName(), subEt.getText()); // 装入到Map中
                }

                // Map获取到值时才装入
                if (map.size() > 0)
                    result.add(map);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}