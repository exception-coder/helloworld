package cn.helloworld.gateway.iot;

import cn.helloworld.gateway.dto.iot.atomconnection.yz600.AddressDTO;
import cn.hutool.core.util.NumberUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 原子手环 YZ600 报文解析
 * @author zhangkai
 */
@Slf4j
public class AtomconnectionYZ600 {

    /**
     * 终端心率上传报文 标识
     */
    private final static String HEART_DATA_CODE = "heart";
    /**
     * 上传体温数据报文 标识
     */
    private final static String TEMP_DATA_CODE = "temp";
    /**
     * 位置数据上报报文 标识
     */
    private final static String UD_DATA_CODE = "UD";
    /**
     * 链路保持报文 标识
      */
    private final static String KA_DATA_CODE = "KA";


    /**
     * 终端心率上传报文解析
     *
     * @param reqData
     * @return 返回检测到的心率 保留2位小数
     */
    public static BigDecimal heart(String reqData){
        String[] strArr = reqData.split(",");
        // 获取心率
        String heartRateStr = strArr[1];
        BigDecimal heartRate = NumberUtil.round(heartRateStr,2);
        log.info("用户心率：{}",heartRate);
        return heartRate;
    }

    /**
     *
     * 上传体温数据报文解析
     *
     * @param reqData
     * @return 返回检测到的体温 保留2位小数
     */
    public static  BigDecimal temp(String reqData){
        String[] strArr = reqData.split(",");
        // 获取体温
        String bodyTemperatureStr = strArr[1];
        BigDecimal bodyTemperature = NumberUtil.round(bodyTemperatureStr,2);
        log.info("用户体温：{}",bodyTemperature);
        return bodyTemperature;
    }

    /**
     * 位置数据上报报文解析
     *
     * @param reqData
     * @return 返回地址经纬度
     */
    public static AddressDTO ud(String reqData){
        String[] strArr = reqData.split(",");
        AddressDTO addressDTO = AddressDTO.builder()
                // 纬度
                .latitude(NumberUtil.round(strArr[4],6))
                // 经度
                .longitude(NumberUtil.round(strArr[6],6)).build();
        log.info("用户地理坐标经纬度：{},{}",addressDTO.getLongitude(),addressDTO.getLatitude());
        return addressDTO;
    }

    /**
     * 报文解析调用
     *
     * @param reqData
     */
    public static void exec(String reqData, ChannelHandlerContext ctx){
        log.info("请求报文：{}",reqData);
        String[] strArr = reqData.split(",");
        String str = strArr[0];
        log.info("请求报文基础信息：{}",str);
        if(str.endsWith(UD_DATA_CODE)){
            ud(reqData);
        }else if(str.endsWith(TEMP_DATA_CODE)){
            temp(reqData);
        }else if( str.endsWith(HEART_DATA_CODE)){
            heart(reqData);
        }else if(str.endsWith(KA_DATA_CODE)){
            // 链路保持
            ka(reqData,ctx);
        }
    }

    private static void ka(String reqData,ChannelHandlerContext ctx) {
        String[] strArr = reqData.split(",");
        String context = StringUtils.removeStart(strArr[0],"[");
        // 样例 51886ff84213d36a1337790320b61eacDW*867228628122607*0010*KA
        strArr = context.split("\\*");
        strArr[2] = "0002";
        String response = "["+StringUtils.join(strArr,"*")+"]";
        log.info("链路保持 返回报文：{}",response);
        ctx.writeAndFlush(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
    }
}
