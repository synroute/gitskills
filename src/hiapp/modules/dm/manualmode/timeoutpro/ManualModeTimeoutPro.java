package hiapp.modules.dm.manualmode.timeoutpro;

import com.alibaba.fastjson.JSON;
import hiapp.modules.dm.bo.CustomerBasic;
import hiapp.modules.dm.manualmode.bo.ManualModeCustomer;
import hiapp.modules.dm.util.GenericitySerializeUtil;
import hiapp.modules.dmsetting.DMTimeoutManagement;
import hiapp.modules.dmsetting.data.DmBizDataPoolRepository;
import hiapp.modules.dmsetting.data.DmTimeoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

import static java.util.Calendar.DAY_OF_MONTH;

/**
 * Created by shizhenshuang on 2018/6/14.
 */
@Component
public class ManualModeTimeoutPro {

    @Autowired
    private JedisSentinelPool jedisSentinelPool;

    private Jedis jedis;

    @Autowired
    private DmTimeoutRepository dmTimeoutRepository;

    @Autowired
    private DmBizDataPoolRepository dmBizDataPoolRepository;

    private TimeoutConfig timeoutConfig;

    @Scheduled(cron = "0 0/1 * * * ?") //每30分钟执行一次
    public void manualModeTimeTask() {

        if (jedis == null) {
            jedis = jedisSentinelPool.getResource();
        }
        Set<byte[]> keys = jedis.keys("*manualMode*".getBytes());
        for (byte[] key : keys) {
            String byteString = null;
            try {
                byteString = GenericitySerializeUtil.unserialize(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //字符串不包含manualMode直接結束
            if (byteString == null || !byteString.contains("manualMode")){
                continue;
            }
            //截取出bizId
            String bizId = byteString.substring(byteString.lastIndexOf("e") + 1);
            TimeoutConfig timeoutConfig = null;
            try {
                timeoutConfig = getTimeoutConfig(bizId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (timeoutConfig == null) {
                continue;
            }
            Map<byte[], byte[]> map = jedis.hgetAll(key);
            Set<byte[]> bytes = map.keySet();
            for (byte[] fieldByte : bytes) {
                PriorityBlockingQueue<ManualModeCustomer> oneShareBatchCustomerSharePool = GenericitySerializeUtil.unserialize(jedis.hget(key, fieldByte));
                Iterator<ManualModeCustomer> customerIterator = oneShareBatchCustomerSharePool.iterator();
                while (customerIterator.hasNext()) {
                    ManualModeCustomer customer = customerIterator.next();
                    if (customer == null) {
                        continue;
                    }
                    //判断是否进行超时处理
                    timeoutHandle(bizId, customer, timeoutConfig);
                }
            }

        }
        //keys.clear();

    }

    //根据bizId查询出配置文件
    public TimeoutConfig getTimeoutConfig(String bizId) {
        this.jedis = jedisSentinelPool.getResource();
        List<DMTimeoutManagement> timeoutManagements = dmTimeoutRepository.dmGetAllTimeoutConfig(bizId);
        //如果size为0直接结束
        if (timeoutManagements.size() == 0){
            return null;
        }
        DMTimeoutManagement dmTimeoutManagement = timeoutManagements.get(0);
        if (dmTimeoutManagement != null && dmTimeoutManagement.getIsEnable() == 0) {
            String timeOutConfgi = dmTimeoutManagement.getTimeOutConfgi();
            if (timeOutConfgi != null) {
                timeoutConfig = JSON.parseObject(timeOutConfgi, TimeoutConfig.class);
                return timeoutConfig;
            }
        }
        return null;
    }

    //根据bizId和与之对应的超时配置文件判断是否做超时处理
    /*
     "Dimension": "超时维度",
    "Timekeeping": "超时计时单位", 天、小时、分钟
	"Duration": "超时时长",
    "TimeoutType": "超时提醒人",
    "TimeoutData": "超时提醒语"
    */
    private void timeoutHandle(String bizId, ManualModeCustomer customer, TimeoutConfig timeoutConfig) {
        Calendar currentTime = Calendar.getInstance();
        Calendar timeoutCalendar = Calendar.getInstance();
        timeoutCalendar.setTime(customer.getInitTimeout());
        List<CustomerBasic> customerBasicList = new ArrayList<>();
        CustomerBasic customerBasic = new CustomerBasic();
        //把customer添加到list集合中
        customerBasicList.add(customerBasic);
        customerBasic.setBizId(bizId);
        customerBasic.setSourceId(customer.getSourceId());
        customerBasic.setImportBatchId(customer.getImportBatchId());
        customerBasic.setCustomerId(customer.getCustomerId());
        int duration = Integer.parseInt(timeoutConfig.getDuration());
        String timeoutType = timeoutConfig.getTimeoutType();
        String timekeeping = timeoutConfig.getTimekeeping();
        int typeHandle = timeoutTypeHandle(timeoutType);
        if (typeHandle == -1){
            return;
        }
        if ("天".equals(timekeeping)) {
            //配置时间在系统时间之前做超时处理
            timeoutCalendar.add(DAY_OF_MONTH, duration);
            if (timeoutCalendar.before(currentTime)) {
                System.out.println(new Date().toLocaleString() + "天---执行手动分配超时处理的定时任务......");
                dmBizDataPoolRepository.getDataPool(customerBasicList, timeoutConfig.getTimeoutData(), typeHandle);

            }
        } else if ("小时".equals(timekeeping)) {
            //配置时间在系统时间之前做超时处理
            timeoutCalendar.add(Calendar.HOUR_OF_DAY, duration);
            if (timeoutCalendar.before(currentTime)) {
                System.out.println(new Date().toLocaleString() + "小时---执行手动分配超时处理的定时任务......");
                dmBizDataPoolRepository.getDataPool(customerBasicList, timeoutConfig.getTimeoutData(), typeHandle);
            }
        } else if ("分钟".equals(timekeeping)) {
            //配置时间在系统时间之前做超时处理
            timeoutCalendar.add(Calendar.MINUTE, duration);
            if (timeoutCalendar.before(currentTime)) {
                System.out.println(new Date().toLocaleString() + "分钟---执行手动分配超时处理的定时任务......");
                try {
                    dmBizDataPoolRepository.getDataPool(customerBasicList, timeoutConfig.getTimeoutData(), typeHandle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int timeoutTypeHandle(String timeoutType) {
        if (TimeoutTypeEnum.THE_DATA_POOL.getName().equals(timeoutType)) {
            return 1;
        } else if (TimeoutTypeEnum.UP_DATA_POOL.getName().equals(timeoutType)) {
            return 2;
        } else if (TimeoutTypeEnum.BOTH_DATA_POOL.getName().equals(timeoutType)) {
            return 3;
        }
        return -1;
    }
}
