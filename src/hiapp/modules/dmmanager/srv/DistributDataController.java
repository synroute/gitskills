package hiapp.modules.dmmanager.srv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomerPool;
import hiapp.modules.dm.manualmode.dao.ManualModeDAO;
import hiapp.modules.dmmanager.UserItem;
import hiapp.modules.dmmanager.bean.DistributeTemplate;
import hiapp.modules.dmmanager.bean.OutputFirstRow;
import hiapp.modules.dmmanager.data.DataDistributeJdbc;
import hiapp.system.buinfo.Permission;
import hiapp.system.buinfo.RoleInGroupSet;
import hiapp.system.buinfo.User;
import hiapp.system.buinfo.data.PermissionRepository;
import hiapp.system.buinfo.data.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
public class DistributDataController {
    @Autowired
    private DataDistributeJdbc dataDistributeJdbc;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有分配模板
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/srv/DistributDataController/getAllDisTemplate.srv")
    public void getAllDisTemplate(HttpServletRequest request, HttpServletResponse response) {
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        List<DistributeTemplate> disTemplates = dataDistributeJdbc.getAllDisTemplate(bizId);
        String jsonObject = new Gson().toJson(disTemplates);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取前台展示的列
     *
     * @param request
     * @param response
     */

    @RequestMapping(value = "/srv/DistributDataController/getTemplateColums.srv")
    public void getTemplateColums(HttpServletRequest request, HttpServletResponse response) {
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        Integer templateId = Integer.valueOf(request.getParameter("templateId"));
        List<OutputFirstRow> columns = dataDistributeJdbc.getAllColumn(bizId, templateId);
        String jsonObject = new Gson().toJson(columns);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 根据时间获取未分配数据并保存到临时表中
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/srv/DistributDataController/getNotDisDataByTime.srv")
    public void getNotDisDataByTime(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String userId = String.valueOf(user.getId());
        RoleInGroupSet roleInGroupSet = userRepository.getRoleInGroupSetByUserId(userId);
        Permission permission = permissionRepository.getPermission(roleInGroupSet);
        int permissionId = permission.getId();
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        Integer templateId = Integer.valueOf(request.getParameter("templateId"));
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        Integer pageNum = Integer.valueOf(request.getParameter("page"));
        Integer pageSize = Integer.valueOf(request.getParameter("rows"));
        String tempTableName = "HAU_DM_" + bizId + "_" + userId;
        dataDistributeJdbc.getNotDisDatByTime(userId, bizId, templateId, startTime, endTime, permissionId);
        Map<String, Object> resultMap = dataDistributeJdbc.getTempNotDisData(bizId, templateId, userId, pageNum, pageSize, tempTableName);
        String jsonObject = new Gson().toJson(resultMap);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 从临时表中查询要展示的数据
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/srv/DistributDataController/getTempNotDisData.srv")
    public void getTempNotDisData(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String userId = String.valueOf(user.getId());
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        Integer templateId = Integer.valueOf(request.getParameter("templateId"));
        Integer pageNum = Integer.valueOf(request.getParameter("page"));
        Integer pageSize = Integer.valueOf(request.getParameter("rows"));
        String tempTableName = "HAU_DM_" + bizId + "_" + userId;
        Map<String, Object> resultMap = dataDistributeJdbc.getTempNotDisData(bizId, templateId, userId, pageNum, pageSize, tempTableName);
        String jsonObject = new Gson().toJson(resultMap);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取所有数据池
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/srv/DistributeDataController/getAllDataPool.srv")
    public void getAllDataPool(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String userId = String.valueOf(user.getId());
        RoleInGroupSet roleInGroupSet = userRepository.getRoleInGroupSetByUserId(userId);
        Permission permission = permissionRepository.getPermission(roleInGroupSet);
        int permissionId = permission.getId();
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        UserItem userItem = dataDistributeJdbc.getTreePoolByBizId(bizId, userId, permissionId);
        String jsonObject = new Gson().toJson(userItem);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将数据保存到正式表中
     *
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/srv/DistributeDataController/saveDistributeDataToDB.srv")
    public void saveDistributeDataToDB(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String userId = String.valueOf(user.getId());
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        String disName = request.getParameter("disName");
        String description = request.getParameter("description");
        String dataPools = request.getParameter("dataPools");
        List<Map<String, Object>> dataPoolList = new Gson().fromJson(dataPools, List.class);
        String tempIds = request.getParameter("tempIds");
        Integer action = Integer.valueOf(request.getParameter("action"));
        String tempTableName = "HAU_DM_" + bizId + "_" + userId;
        dataDistributeJdbc.updateTempData(bizId, userId, tempIds, action, tempTableName);
        Map<String, Object> resultMap = dataDistributeJdbc.saveDistributeDataToDB(bizId, userId, disName, description, tempIds, dataPoolList);
        String jsonObject = new Gson().toJson(resultMap);

        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 保存共享数据
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/srv/DistributeDataController/saveShareDataToDB.srv")
    public void saveShareDataToDB(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String userId = String.valueOf(user.getId());
        RoleInGroupSet roleInGroupSet = userRepository.getRoleInGroupSetByUserId(userId);
        Permission permission = permissionRepository.getPermission(roleInGroupSet);
        int permissionId = permission.getId();
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        String shareName = request.getParameter("shareName");
        String description = request.getParameter("description");
        String dataPoolIds = request.getParameter("dataPoolIds");
        String dataPoolNames = request.getParameter("dataPoolNames");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        Integer model = Integer.valueOf(request.getParameter("model"));
        String shareId = request.getParameter("shareId");
        String tempIds = request.getParameter("tempIds");
        Integer action = Integer.valueOf(request.getParameter("action"));
        String tempTableName = "HAU_DM_" + bizId + "_" + userId;
        dataDistributeJdbc.updateTempData(bizId, userId, tempIds, action, tempTableName);
        Map<String, Object> resultMap = dataDistributeJdbc.saveShareDataToDB(bizId, userId, shareName, description, startTime, endTime, dataPoolIds, dataPoolNames, model, shareId, permissionId);
        String jsonObject = new Gson().toJson(resultMap);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 判断当前数据池下面是否有坐席池
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/srv/DistributeDataController/ifCurPoolChildrens.srv")
    public void ifCurPoolChildrens(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String userId = String.valueOf(user.getId());
        RoleInGroupSet roleInGroupSet = userRepository.getRoleInGroupSetByUserId(userId);
        Permission permission = permissionRepository.getPermission(roleInGroupSet);
        int permissionId = permission.getId();
        Integer bizId = Integer.valueOf(request.getParameter("bizId"));
        Map<String, Object> resultMap = dataDistributeJdbc.ifCurPoolChildrens(bizId, permissionId);
        String jsonObject = new Gson().toJson(resultMap);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.print(jsonObject);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
