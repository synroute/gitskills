package hiapp.modules.dm.manualmode.dao;

import hiapp.modules.dm.bo.ShareBatchItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerItem;
import hiapp.modules.dm.singlenumbermode.bo.SingleNumberModeShareCustomerStateEnum;
import hiapp.modules.dm.util.SQLUtil;
import hiapp.modules.dmmanager.AreaTypeEnum;
import hiapp.modules.dmmanager.DataPoolRecord;
import hiapp.modules.dmmanager.DataPoolRecordOperation;
import hiapp.utils.DbUtil;
import hiapp.utils.database.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ManualModeDAO extends BaseRepository {

    public Boolean getGivenBizShareCustomers(int bizId, List<ShareBatchItem> ShareBatchItems,
                                            /*OUT*/ List<DataPoolRecord> shareCustomerItems) {

        Connection dbConn = null;
        PreparedStatement stmt = null;

        Map<String, ShareBatchItem> mapShareBatchIdVsShareBatchItem = new HashMap<String, ShareBatchItem>();
        for (ShareBatchItem shareBatchItem : ShareBatchItems) {
            mapShareBatchIdVsShareBatchItem.put(shareBatchItem.getShareBatchId(), shareBatchItem);
        }

        String tableName = String.format("HAU_DM_B%dC_POOL", bizId);

        try {
            dbConn = this.getDbConnection();

            //
            StringBuilder sqlBuilder = new StringBuilder("SELECT ID, SOURCEID, IID, CID, DATAPOOlIDLAST, DATAPOOlIDCUR, " +
                                  "AREALAST, AREACUR, ISRECOVER, MODIFYUSERID, MODIFYTIME FROM " + tableName);
            sqlBuilder.append(" WHERE SOURCEID IN (").append(SQLUtil.shareBatchItemlistToSqlString(ShareBatchItems)).append(")");
            sqlBuilder.append(" AND AREACUR = ").append(1);

            System.out.println(sqlBuilder.toString());

            stmt = dbConn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DataPoolRecord item = new DataPoolRecord();
                item.setId(rs.getInt(1));
                item.setSourceId(rs.getString(2));
                item.setImportId(rs.getString(3));
                item.setCustomerId(rs.getString(4));
                item.setDataPoolIdLast(rs.getInt(5));
                item.setDataPoolIdCur(rs.getInt(6));
                item.setAreaLast(AreaTypeEnum.getFromInt(rs.getInt(7)));
                item.setAreaCur(AreaTypeEnum.getFromInt(rs.getInt(8)));
                item.setIsRecover(rs.getInt(9));
                item.setModifyUserId(rs.getString(10));
                item.setModifyTime(rs.getDate(11));
                shareCustomerItems.add(item);
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


    public Boolean insertPoolOperation(DataPoolRecordOperation item) {

        String tableName = String.format("HAU_DM_B%dC_POOL_ORE", item.getSourceId());

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + tableName);
        sqlBuilder.append(" (ID,SourceID,IID,CID,OperationName,DataPoolIDLast,DataPoolIDCur,AreaLast,AreaCur,ISRecover,ModifyUserID,ModifyTime ) VALUES ( ");

        sqlBuilder.append("S_" + tableName + ".NEXTVAL").append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getSourceId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getImportId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getCustomerId())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getOperationName().getName())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getDataPoolIDLast())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getDataPoolIDCur())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getAreaLast())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getAreaCur())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getiSRecover())).append(",");
        sqlBuilder.append(SQLUtil.getSqlString(item.getModifyUserID())).append(",");
        sqlBuilder.append("sysdate").append(",");
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

}
