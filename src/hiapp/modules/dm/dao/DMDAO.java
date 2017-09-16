package hiapp.modules.dm.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class DMDAO extends BaseRepository {

    /*
     *  所有业务的共享批次
     */
    public Boolean getCurDayShareBatchItemsByState(List<ShareBatchStateEnum> shareBatchStateList, /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        int id;
        int bizId;
        String shareBatchId;
        String shareBatchName;
        int  createUserId;
        Date createTime;
        String description;
        ShareBatchStateEnum	state;
        Date StartTime;
        Date EndTime;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                                        "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STATE IN (").append(shareBatchStatelistToCommaSplitString(shareBatchStateList)).append(")");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ShareBatchItem item = new ShareBatchItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setShareBatchName(rs.getString(4));
                item.setCreateUserId(rs.getString(5));
                item.setCreateTime(rs.getTime(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                shareBatchItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    /*
     *  取得当天启用 且未处理的共享批次
     */
    public Boolean getCurDayUsingShareBatchItems( /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        /*
        int id;
        int bizId;
        String shareBatchId;
        String shareBatchName;
        int  createUserId;
        Date createTime;
        String description;
        ShareBatchStateEnum	state;
        Date StartTime;
        Date EndTime;
        */
        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BusinessID, ShareId, ShareName, CreateUserID, " +
                    "CreateTime, Description, State, StartTime, EndTime" +
                    " FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" State = ").append(ShareBatchStateEnum.ENABLE.getName());
            sqlBuilder.append("AND trunc(CreateTime) = trunc(sysdate)");

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ShareBatchItem item = new ShareBatchItem();
                item.setId(rs.getInt(1));
                item.setBizId(rs.getInt(2));
                item.setShareBatchId(rs.getString(3));
                item.setShareBatchName(rs.getString(4));
                item.setCreateUserId(rs.getString(5));
                item.setCreateTime(rs.getTime(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                shareBatchItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return null;
    }

    public  Boolean activateCurDayShareBatchByStartTime() {

        /*
        int id;
        int bizId;
        String shareBatchId;
        String shareBatchName;
        int  createUserId;
        Date createTime;
        String description;
        ShareBatchStateEnum	state;
        Date StartTime;
        Date EndTime;

        ENABLE("enable"), //启用
        ACTIVE("active"), //激活 系统设置
        PAUSE("pause"),
        STOP("stop"),
        EXPIRED("expired"); // 过期 系统设置
        */

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            // 激活共享批次
            StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = '").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");
            sqlBuilder.append(" WHERE STARTTIME <= ").append("TO_DATE('").append(getCurDaySqlString()).append("','yyyy/mm/dd')");
            sqlBuilder.append(" AND STATE = '").append(ShareBatchStateEnum.ENABLE.getName()).append("'");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }



        return true;

    }

    // 获取并标记 过期的共享批次
    public  Boolean expireCurDayShareBatchByEndTime(/*OUT*//*List<String> expiredShareBatchIds*/) {

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            StringBuilder sqlBuilder;
            /*
            // 获取过期的共享批次
            sqlBuilder = new StringBuilder("SELECT shareBatchId FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" (State = ").append(ShareBatchStateEnum.ENABLE.getName());
            sqlBuilder.append(" OR State = ").append(ShareBatchStateEnum.ACTIVE.getName());
            sqlBuilder.append(") AND EndTime < ").append("TO_DATA(").append(curDay).append(",'yyyy-MM-dd')");
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expiredShareBatchIds.add(rs.getString(1));
            }
            */

            // 过期处理..共享批次
            sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = '").append(ShareBatchStateEnum.EXPIRED.getName()).append("'");
            sqlBuilder.append(" WHERE (STATE = '").append(ShareBatchStateEnum.ENABLE.getName()).append("'");
            sqlBuilder.append(" OR STATE = '").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");
            sqlBuilder.append(") AND ENDTIME < ").append("TO_DATE('").append(getNextDaySqlString()).append("','yyyy/mm/dd')");

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public List<ShareBatchItem> getShareBatchItems(List<String> ShareBatchIds) {
        return null;
    }

    public Boolean insertDMResult() {

        //需更新  modifyLast 标记

        int id = 0;
        String sourceID = ""; //来源编号是指分配编号或共享编号
        String importBatchID = ""; //客户导入批次ID
        String customerID = ""; //客户ID
        int modifyID = 0; //
        String 	modifyUserID = "";
        Date modifyTime = null;
        Boolean	modifyLast = false;
        String 	dialType = ""; // 拨打类型 拨打提交；修改提交
        Date 	dialTime = null; //
        String 	customerCallId = ""; // 呼叫流水号

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO HAU_DM_B1C_Result (ID, SourceID, IID," +
                "CID, ModifyId, ModifyUserId, ModifyTime, ModifyLast, DialType, DialTime, CustomerCallId" +
                ") VALUES ( ");

        sqlBuilder.append("'").append(id).append("',");
        sqlBuilder.append("'").append(sourceID).append("',");
        sqlBuilder.append("'").append(importBatchID).append("',");
        sqlBuilder.append("'").append(customerID).append("',");
        sqlBuilder.append("'").append(modifyID).append("',");
        sqlBuilder.append("'").append(modifyUserID).append("',");
        sqlBuilder.append("'").append(modifyTime).append("',");
        sqlBuilder.append("'").append(modifyLast).append("',");
        sqlBuilder.append("'").append(dialType).append("',");
        sqlBuilder.append("'").append(dialTime).append("',");
        sqlBuilder.append("'").append(customerCallId).append("',");

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean insertPresetItem(DMBizPresetItem presetItem) {
//        private int id = 0;				//ID
//        private String sourceId;	//来源编号，指分配编号或共享编号
//        private String importId;	//导入批次ID
//        private String customerId;	//客户号
//        private java.sql.Date presetTime;	//预约时间
//        private String state;		//预约状态
//        private String comment;		//预约备注
//        private int modifyId;		//修改ID
//        private int modifyLast;		//是否为最后一次修改，0：否。1：是
//        private int modifyUserId;	//修改用户ID
//        private java.sql.Date modifyTime;	//修改时间
//        private String modifyDesc;	//修改描述
//        private String phoneType;	//号码类型  枚举

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO HASYS_DM_B1C_PresetTime (ID, SourceID, IID," +
                "CID, PresetTime, State, Comment, ModifyId, ModifyUserId, ModifyTime, ModifyDesc，ModifyLast, PhoneType" +
                ") VALUES ( ");

        sqlBuilder.append("'").append(presetItem.getId()).append("',");
        sqlBuilder.append("'").append(presetItem.getSourceId()).append("',");
        sqlBuilder.append("'").append(presetItem.getImportId()).append("',");
        sqlBuilder.append("'").append(presetItem.getCustomerId()).append("',");
        sqlBuilder.append("'").append(presetItem.getPresetTime()).append("',");
        sqlBuilder.append("'").append(presetItem.getState()).append("',");
        sqlBuilder.append("'").append(presetItem.getComment()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyId()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyUserId()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyTime()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyDesc()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyLast()).append("',");
        sqlBuilder.append("'").append(presetItem.getPhoneType());

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public Boolean updatePresetState(int bizId, String shareBatchId, String customerId, String newState) {
//        private int id = 0;				//ID
//        private String sourceId;	//来源编号，指分配编号或共享编号
//        private String importId;	//导入批次ID
//        private String customerId;	//客户号
//        private java.sql.Date presetTime;	//预约时间
//        private String state;		//预约状态
//        private String comment;		//预约备注
//        private int modifyId;		//修改ID
//        private int modifyLast;		//是否为最后一次修改，0：否。1：是
//        private int modifyUserId;	//修改用户ID
//        private java.sql.Date modifyTime;	//修改时间
//        private String modifyDesc;	//修改描述
//        private String phoneType;	//号码类型  枚举

        StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_B1C_PresetTime");
        sqlBuilder.append(" Set State = ").append(newState);
        sqlBuilder.append(" WHERE BusinessID = ").append(bizId);
        sqlBuilder.append(" SourceID = ").append(shareBatchId);
        sqlBuilder.append(" CID = ").append(customerId);

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();
            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }


    ////////////////////////////////////////////////////////////////////////////////

    private String shareBatchStatelistToCommaSplitString(List<ShareBatchStateEnum> shareBatchStateList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < shareBatchStateList.size(); indx++ ) {
            ShareBatchStateEnum state = shareBatchStateList.get(indx);
            sb.append("'").append(state.getName()).append("'");
            if (indx < (shareBatchStateList.size()-1))
                sb.append(",");
        }

        return sb.toString();
    }

    public String getNextDaySqlString() {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, 1);
        curDay.set(Calendar.HOUR_OF_DAY, 0);
        curDay.set(Calendar.MINUTE, 0);
        curDay.set(Calendar.SECOND, 0);
        curDay.set(Calendar.MILLISECOND, 0);
        String strNextDay = curDay.get(Calendar.YEAR) + "/" + (curDay.get(Calendar.MONTH)+1) + "/" + curDay.get(Calendar.DAY_OF_MONTH);
        return strNextDay;
    }

    public String getCurDaySqlString() {
        Calendar curDay = Calendar.getInstance();
        curDay.setTime(new Date());
        curDay.add(Calendar.DAY_OF_MONTH, 0);
        curDay.set(Calendar.HOUR_OF_DAY, 0);
        curDay.set(Calendar.MINUTE, 0);
        curDay.set(Calendar.SECOND, 0);
        curDay.set(Calendar.MILLISECOND, 0);
        String strCurDay = curDay.get(Calendar.YEAR) + "/" + (curDay.get(Calendar.MONTH)+1) + "/" + curDay.get(Calendar.DAY_OF_MONTH);
        return strCurDay;
    }

}


