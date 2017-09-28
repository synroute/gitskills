package hiapp.modules.dm.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
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
    public Boolean getAllActiveShareBatchItems(/*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                                        "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STATE ='").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");

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

    /**
     *
     */
    public Boolean getActiveShareBatchItems(List<String> shareBatchIdList, /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                    "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STATE = '").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");
            sqlBuilder.append(" SHAREID IN (").append(stringListToCommaSplitString(shareBatchIdList)).append(")");

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
     *  取得当天启用 且未处理的共享批次, 处理后续启用的共享批次
     */
    public Boolean getNeedActiveShareBatchItems( /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                    "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID WHERE ");
            sqlBuilder.append(" STARTTIME <= ").append("TO_DATE('").append(getNextDaySqlString()).append("','yyyy/mm/dd')");
            sqlBuilder.append(" AND STATE = '").append(ShareBatchStateEnum.ENABLE.getName()).append("'");

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

    public  Boolean activateShareBatchByStartTime() {

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            // 激活共享批次
            StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = '").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");
            sqlBuilder.append(" WHERE STARTTIME <= ").append("TO_DATE('").append(getCurDaySqlString()).append("','yyyy-mm-dd hh24:mi:ss')");
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
    public  Boolean expireShareBatchsByEndTime(/*OUT*//*List<String> expiredShareBatchIds*/) {

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

    /**
     *
     * @param bizId
     * @param sourceId  :  来源编号是指分配编号或共享编号
     * @param importBatchId
     * @param customerId
     * @param modifyId
     * @param modifyUserId
     * @param dialType  :  拨打类型 拨打提交；修改提交
     * @param dialTime
     * @param customerCallId  :  呼叫流水号
     * @return
     */
    public Boolean insertDMResult(int bizId, String sourceId, String importBatchId, String customerId, int modifyId,
                                  String modifyUserId, String dialType, String dialTime, String customerCallId) {

        String tableName = String.format("HAU_DM_B%dC_Result", bizId);

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID, SOURCEID, IID, CID, MODIFYID, MODIFYUSERID, MODIFYTIME, MODIFYLAST, DIALTYPE, " +
                                "DIALTIME, CUSTOMERCALLID) VALUES ( ");
        sqlBuilder.append("'").append("S_" + tableName + ".nextval").append("',");
        sqlBuilder.append("'").append(sourceId).append("',");
        sqlBuilder.append("'").append(importBatchId).append("',");
        sqlBuilder.append("'").append(customerId).append("',");
        sqlBuilder.append(modifyId).append(",");
        sqlBuilder.append("'").append(modifyUserId).append("',");
        sqlBuilder.append("sysdate").append(",");
        sqlBuilder.append("1").append(","); // MODIFYLAST
        sqlBuilder.append("'").append(dialType).append("',");
        sqlBuilder.append("'").append(dialTime).append("',");
        sqlBuilder.append("'").append(customerCallId).append("'");

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

    public Boolean updateDMResult(int bizId, String shareBatchId, String importBatchId, String customerId) {

        String tableName = String.format("HAU_DM_B%dC_Result", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET MODIFYLAST = ").append("0");
        sqlBuilder.append(" WHERE BUSINESSID = ").append(bizId);
        sqlBuilder.append(" SOURCEID = ").append(shareBatchId);
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

    public Boolean insertPresetItem(int bizId, DMBizPresetItem presetItem) {

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PresetTime", bizId);

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO" + presetTimeTableName);
        sqlBuilder.append(" (ID, SOURCEID, IID, CID, PRESETTIME, STATE, COMMENT, MODIFYID, MODIFYUSERID, MODIFYTIME, " +
                                      "MODIFYDESC，MODIFYLAST, PHONETYPE) VALUES ( ");

        sqlBuilder.append("'").append("S_" + presetTimeTableName + ".nextval").append("',");
        sqlBuilder.append("'").append(presetItem.getSourceId()).append("',");
        sqlBuilder.append("'").append(presetItem.getImportId()).append("',");
        sqlBuilder.append("'").append(presetItem.getCustomerId()).append("',");
        sqlBuilder.append("'").append(presetItem.getPresetTime()).append("',");
        sqlBuilder.append("'").append(presetItem.getState()).append("',");
        sqlBuilder.append("'").append(presetItem.getComment()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyId()).append("',");
        sqlBuilder.append("'").append(presetItem.getModifyUserId()).append("',");
        sqlBuilder.append("sysdate").append(",");
        sqlBuilder.append("'").append(presetItem.getModifyDesc()).append("',");
        sqlBuilder.append("1").append("',");
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

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PresetTime", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + presetTimeTableName);
        sqlBuilder.append(" SET STATE = ").append(newState);
        sqlBuilder.append(" MODIFYLAST = ").append("0");
        sqlBuilder.append(" WHERE BUSINESSID = ").append(bizId);
        sqlBuilder.append(" SOURCEID = ").append(shareBatchId);
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
        curDay.set(Calendar.HOUR_OF_DAY, 23);
        curDay.set(Calendar.MINUTE, 59);
        curDay.set(Calendar.SECOND, 59);
        curDay.set(Calendar.MILLISECOND, 0);
        String strCurDay = curDay.get(Calendar.YEAR) + "/" + (curDay.get(Calendar.MONTH)+1) + "/" + curDay.get(Calendar.DAY_OF_MONTH)
                + " " + curDay.get(Calendar.HOUR_OF_DAY) + ":" + curDay.get(Calendar.MINUTE) + ":" + curDay.get(Calendar.SECOND);
        return strCurDay;
    }

    private String stringListToCommaSplitString(List<String> stringList) {
        StringBuilder sb = new StringBuilder();
        for (int indx = 0; indx < stringList.size(); indx++) {
            sb.append("'").append(stringList.get(indx)).append("'");
            if (indx < (stringList.size() - 1))
                sb.append(",");
        }

        return sb.toString();
    }

}


