package hiapp.modules.dm.dao;

import com.google.gson.JsonObject;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.bo.ShareBatchStateEnum;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dm.util.DateUtil;
import hiapp.modules.dmmanager.data.DataImportJdbc;
import hiapp.modules.dmsetting.DMBizOutboundModelEnum;
import hiapp.modules.dmsetting.DMBizPresetItem;
import hiapp.modules.dmsetting.DMBusiness;
import hiapp.modules.dmsetting.data.DmWorkSheetRepository;
import hiapp.system.buinfo.User;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DMDAO extends BaseRepository {

    @Autowired
    DataImportJdbc dataImportJdbc;

    @Autowired
    private DmWorkSheetRepository dmWorkSheetRepository;


    /*
     *  所有业务的共享批次
     */
    public Boolean getAllActiveShareBatchItems(/*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        List<ShareBatchStateEnum> shareBatchStateList = new ArrayList<ShareBatchStateEnum>();
        shareBatchStateList.add(ShareBatchStateEnum.ACTIVE);
        shareBatchStateList.add(ShareBatchStateEnum.RECOVER);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT a.ID, a.BUSINESSID, a.SHAREID, a.SHARENAME, a.CREATEUSERID, a.CREATETIME, a.DESCRIPTION, a.STATE, a.STARTTIME, a.ENDTIME, b.OUTBOUNDMDDEID" +
                    "  FROM HASYS_DM_SID a " +
                    "  LEFT JOIN HASYS_DM_BUSINESS b ON a.BUSINESSID = b.BUSINESSID " +
                    "WHERE ");
            sqlBuilder.append(" STATE IN (").append(SQLUtil.shareBatchStatelistToCommaSplitString(shareBatchStateList)).append(")");

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
                item.setCreateTime(rs.getDate(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                item.setOutboundModeId(rs.getInt(11));
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
     *  指定外呼模式的业务的共享批次
     */
    public Boolean getGivenModeActiveShareBatchItems(int modeId, /*OUT*/List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                    " CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID " +
                    " LEFT JOIN HASYS_DM_BUSINESS ON HASYS_DM_SID.BUSINESSID = HASYS_DM_BUSINESS.BUSINESSID " +
                    " WHERE ");
            sqlBuilder.append(" STATE ='").append(ShareBatchStateEnum.ACTIVE.getName()).append("'");
            sqlBuilder.append(" OUTBOUNDMDDEID = ").append(modeId);

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
                item.setCreateTime(rs.getDate(6));
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
            sqlBuilder.append(" AND SHAREID IN (").append(SQLUtil.stringListToSqlString(shareBatchIdList)).append(")");

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
                item.setCreateTime(rs.getDate(6));
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
    public List<ShareBatchItem> getCurDayNeedActiveShareBatchItems( int bizId, List<String> shareBatchIds) {

        List<ShareBatchItem> shareBatchItems = new ArrayList<ShareBatchItem>();

        Connection dbConn = null;
        PreparedStatement stmt = null;

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, BUSINESSID, SHAREID, SHARENAME, CREATEUSERID, " +
                    "CREATETIME, DESCRIPTION, STATE, STARTTIME, ENDTIME FROM HASYS_DM_SID");
            sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(bizId));
            sqlBuilder.append("   AND STARTTIME <= ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));
            sqlBuilder.append("   AND STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));
            sqlBuilder.append("   AND SHAREID IN (").append(SQLUtil.stringListToSqlString(shareBatchIds)).append(")");

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
                item.setCreateTime(rs.getDate(6));
                item.setDescription(rs.getString(7));
                item.setState(ShareBatchStateEnum.getFromString(rs.getString(8)) );
                item.setStartTime(rs.getDate(9));
                item.setEndTime(rs.getDate(10));
                shareBatchItems.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return shareBatchItems;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return shareBatchItems;
    }

    public  Boolean activateShareBatchByStartTime() {

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            // 激活共享批次
            StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID ");
            sqlBuilder.append(" SET STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ACTIVE.getName()));
            sqlBuilder.append(" WHERE STARTTIME <= ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));
            sqlBuilder.append("   AND STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));

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

    public  Boolean activateShareBatchByStartTime(int bizId, List<ShareBatchItem> shareBatchItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;
        try {
            dbConn = this.getDbConnection();

            // 激活共享批次
            StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ACTIVE.getName()));
            sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(bizId));
            sqlBuilder.append("   AND STARTTIME <= ").append(SQLUtil.getSqlString(DateUtil.getNextDaySqlString()));
            sqlBuilder.append("   AND STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));
            sqlBuilder.append("   AND SHAREID IN (").append(SQLUtil.shareBatchItemlistToSqlString(shareBatchItems)).append(")");

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
            sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.EXPIRED.getName()));
            sqlBuilder.append(" WHERE (STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ENABLE.getName()));
            sqlBuilder.append(" OR STATE = ").append(SQLUtil.getSqlString(ShareBatchStateEnum.ACTIVE.getName()));
            sqlBuilder.append(") AND ENDTIME < ").append(SQLUtil.getSqlString(DateUtil.getCurDayStartSqlString()));

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
                                  String modifyUserId, String dialType, Date dialTime, String customerCallId,
                                  String resultCodeType, String resultCode, Map<String, String> mapCustomizedResultColumn) {

        String tableName = String.format("HAU_DM_B%dC_RESULT", bizId);

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID, SOURCEID, IID, CID, MODIFYID, MODIFYUSERID, MODIFYTIME, MODIFYLAST, DIALTYPE, " +
                                "DIALTIME, CUSTOMERCALLID, ENDCODETYPE, ENDCODE");
        if (null != mapCustomizedResultColumn && !mapCustomizedResultColumn.isEmpty()) {
            sqlBuilder.append(", " + SQLUtil.stringCollectionToFieldString(mapCustomizedResultColumn.keySet()));
        }

        sqlBuilder.append(" ) VALUES ( ");
        sqlBuilder.append("S_" + tableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(sourceId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(importBatchId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(customerId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(modifyId)).append(",");;
        sqlBuilder.append(SQLUtil.getSqlString(modifyUserId)).append(",");;
        sqlBuilder.append("sysdate").append(",");
        sqlBuilder.append("1").append(","); // MODIFYLAST
        sqlBuilder.append(SQLUtil.getSqlString(dialType)).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(dialTime)).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(customerCallId)).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(resultCodeType)).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(resultCode));
        if (null != mapCustomizedResultColumn && !mapCustomizedResultColumn.isEmpty()) {
            sqlBuilder.append(", " + SQLUtil.stringCollectionToSqlString(mapCustomizedResultColumn.values()));
        }

        sqlBuilder.append(")");

        System.out.println(sqlBuilder.toString());

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

        dataImportJdbc.insertDataToResultTable(bizId, sourceId, importBatchId, customerId, modifyUserId, modifyId,
                dialType, dialTime, customerCallId, resultCodeType, resultCode);

        return true;
    }

    public Boolean updateDMResult(int bizId, String shareBatchId, String importBatchId, String customerId, int modifyId) {

        String tableName = String.format("HAU_DM_B%dC_Result", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName);
        sqlBuilder.append(" SET MODIFYLAST = ").append("0");
        sqlBuilder.append(" WHERE IID = ").append(SQLUtil.getSqlString(importBatchId));
        sqlBuilder.append(" AND SOURCEID = ").append(SQLUtil.getSqlString(shareBatchId));
        sqlBuilder.append(" AND CID = ").append(SQLUtil.getSqlString(customerId));
        sqlBuilder.append(" AND MODIFYID = ").append(SQLUtil.getSqlString(modifyId));

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

    public boolean getPresetcount(int bizId, DMBizPresetItem presetItem)
	{
    	String presetTimeTableName = String.format("HASYS_DM_B%dC_PRESETTIME", bizId);
		String sql="";
		Connection dbConn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count=0;
		sql="select count(*) from "+presetTimeTableName+" where CID='"+presetItem.getCustomerId()+"' and IID='"+presetItem.getImportId()+"' and SOURCEID='"+presetItem.getSourceId()+"' ";
		try {
			dbConn =this.getDbConnection();
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()){
				count=rs.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }
			
		
		if(count>0)
		{
			return true;
		}else {
			return false;
		}
	}
    
    public Boolean insertPresetItem(int bizId, DMBizPresetItem presetItem) {

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PRESETTIME", bizId);

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + presetTimeTableName);
        sqlBuilder.append(" (ID, SOURCEID, IID, CID, PRESETTIME, STATE, PRESETCOMMENT, MODIFYID, MODIFYUSERID, " +
                            "MODIFYTIME, MODIFYDESC, MODIFYLAST, PHONETYPE) VALUES ( ");

        sqlBuilder.append("S_" + presetTimeTableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getSourceId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getImportId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getCustomerId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getPresetTime())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getState())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getComment())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getModifyId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getModifyUserId())).append(",");
        sqlBuilder.append("sysdate").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getModifyDesc())).append(",");
        sqlBuilder.append("1").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(presetItem.getPhoneType()));
        sqlBuilder.append(")");

        System.out.println(sqlBuilder.toString());

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
    public Boolean updatePresetItem(int bizId, DMBizPresetItem presetItem,String state) {

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PRESETTIME", bizId);

        StringBuilder sqlBuilder = new StringBuilder("");
        if(state.equals("使用中"))
        {
        	sqlBuilder.append("update "+presetTimeTableName+" set PRESETTIME=" + SQLUtil.getSqlString(presetItem.getPresetTime()) + ",ModifyTime=sysdate where CID='"+presetItem.getCustomerId()+"' and IID='"+presetItem.getImportId()+"' and SOURCEID='"+presetItem.getSourceId()+"' ");
        }else {
        	sqlBuilder.append("update "+presetTimeTableName+" set STATE='预约完成',ModifyTime=sysdate where CID='"+presetItem.getCustomerId()+"' and IID='"+presetItem.getImportId()+"' and SOURCEID='"+presetItem.getSourceId()+"' ");
		}
        System.out.println(sqlBuilder.toString());

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
    public Boolean updatePresetState(int bizId, String shareBatchId, String importBatchId, String customerId, int modifyId, String newState) {

        String presetTimeTableName = String.format("HASYS_DM_B%dC_PRESETTIME", bizId);

        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + presetTimeTableName);
        sqlBuilder.append(" SET STATE = ").append(SQLUtil.getSqlString(newState));
        sqlBuilder.append("  MODIFYLAST = 0");
        sqlBuilder.append(" WHERE IID = ").append(SQLUtil.getSqlString(importBatchId));
        sqlBuilder.append("  SOURCEID = ").append(SQLUtil.getSqlString(shareBatchId));
        sqlBuilder.append("  CID = ").append(SQLUtil.getSqlString(customerId));
        sqlBuilder.append("  MODIFYID = ").append(SQLUtil.getSqlString(modifyId));

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

    public Boolean updateShareBatchState(int bizId, List<String> shareBatchIds, String state) {
        PreparedStatement stmt = null;
        Connection dbConn = null;
        try {
            dbConn=this.getDbConnection();
            StringBuilder sqlBuilder = new StringBuilder("UPDATE HASYS_DM_SID SET ");
            sqlBuilder.append(" STATE = ").append(SQLUtil.getSqlString(state));
            sqlBuilder.append(" WHERE BUSINESSID = ").append(SQLUtil.getSqlString(bizId));
            sqlBuilder.append("   AND SHAREID IN (").append(SQLUtil.stringListToSqlString(shareBatchIds)).append(")");

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return true;
    }

    public boolean getAllDMBusiness(List<DMBusiness> listDMBusiness) {
        Connection dbConn = null;
        String szSql = "";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            dbConn = this.getDbConnection();
            szSql = String.format("SELECT BUSINESSID,NAME,DESCRIPTION,OWNERGROUPID,OUTBOUNDMDDEID,CONFIGJSON FROM HASYS_DM_BUSINESS " +
                    "WHERE ISDELETE=0 AND OUTBOUNDMDDEID in (2,5,6) ORDER BY BUSINESSID ");
            stmt = dbConn.prepareStatement(szSql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                DMBusiness DMBusiness = new DMBusiness();
                DMBusiness.setBizId(rs.getInt(1));
                DMBusiness.setName(rs.getString(2));
                DMBusiness.setDesc(rs.getString(3));
                DMBusiness.setOwnerGroupId(rs.getString(4));


                DMBusiness.setModeId(rs.getInt(5));
                DMBizOutboundModelEnum modelEnum = DMBizOutboundModelEnum.values()[DMBusiness.getModeId()-1];
                DMBusiness.setModeInfo(modelEnum.getOutboundID()+";"+modelEnum.getOutboundType()+";"+modelEnum.getOutboundMode());

                DMBusiness.setConfigJson(rs.getString(6));

                listDMBusiness.add(DMBusiness);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DbUtil.DbCloseConnection(dbConn);
            DbUtil.DbCloseQuery(rs, stmt);
        }
        return true;
    }

    public Integer getModifyIdFromImportTable(int bizId, String importBatchId, String customerId)
    {
        Connection dbConn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String,String> map=new HashMap<String, String>();
        JsonObject jsonObject=new JsonObject();

        String tableName = String.format("HAU_DM_B%dC_IMPORT", bizId);

        try {
            dbConn =this.getDbConnection();

            StringBuilder sqlBuilder = new StringBuilder("SELECT MODIFYID FROM ").append(tableName);
            sqlBuilder.append(" WHERE IID = ").append(SQLUtil.getSqlString(importBatchId));
            sqlBuilder.append("   AND CID = ").append(SQLUtil.getSqlString(customerId));
            sqlBuilder.append("   AND MODIFYLAST = 1");

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DbUtil.DbCloseExecute(stmt);
            DbUtil.DbCloseConnection(dbConn);
        }

        return 0;
    }

}


