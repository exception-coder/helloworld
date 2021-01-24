package com.yonyou.dmscloud.part.controller.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yonyou.dmscloud.framework.DAO.DAOUtil;
import com.yonyou.dmscloud.framework.service.CommonNoService;
import com.yonyou.dmscloud.framework.util.FrameworkUtil;
import com.yonyou.dmscloud.part.controller.gms.partPurchase.PurchaseController;
import com.yonyou.dmscloud.part.service.epcInterface.InterfaceEpcDataServiceImpl;
import com.yonyou.f4.mvc.annotation.TxnConn;
import com.yonyou.f4.mvc.annotation.UseReadDb;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/app")
public class AGP15Controller {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AGP15Controller.class);

    @Autowired
    private CommonNoService commonNoService;


    @TxnConn
    @RequestMapping(value = "/arrival_sign/interAspect", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject arrival_sign(@RequestBody String data) {
        logger.info("agp15 接口调用   开始");
        JSONObject responsesMap = new JSONObject();
        responsesMap.put("status", 1);
        try {
            JSONArray jsonArray = JSONArray.parseArray(data);
            JSONObject jsonObject = new JSONObject();
//            JSONObject requestData = JSONObject.parseObject(data);
            JSONObject requestData = jsonArray.getJSONObject(0);
            // 发运单号
            Object waybillNo = requestData.get("WAYBILL_NO");
            Object signStatus = requestData.get("SIGN_STATUS");
            Object signName = requestData.get("SIGN_NAME");
            Object signTime = requestData.get("SIGN_TIME");
            if (waybillNo == null || signName == null || signTime == null) {
                responsesMap.put("message", "缺少必填参数");
                return responsesMap;
            }
            if (signStatus == null) {
                // 已签收
                signStatus = "60161004";
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(String.valueOf(signTime));
            StringBuilder sql = new StringBuilder();

            sql.append("select * from tt_pt_ec_delivery_order tpdo where tpdo.DELIVERY_NO  = ?");
            List<Object> params = new ArrayList<>();
            params.add(waybillNo);
            Map map = DAOUtil.findFirst(sql.toString(), params);
            if (map == null) {
                responsesMap.put("message", "发运单不存在 : " + String.valueOf(waybillNo));
                return responsesMap;
            }
            // 发运单ID
            Object deliveryOrderId = map.get("DELIVERY_ORDER_ID");

            // 更新配件销售发运单发运状态 签收人 签收时间 默认已签收
            sql = new StringBuilder();
            sql.append("update tt_pt_ec_delivery_order tpdo set tpdo.STATUS =  #[STATUS], \n" +
                    "tpdo.PURCHASER = #[PURCHASER] ,tpdo.PURCHASER_DATE  =  #[PURCHASER_DATE] \n" +
                    "where DELIVERY_NO  =  #[DELIVERY_NO] ");
            Map paramterMap = new HashMap();
            paramterMap.put("STATUS", String.valueOf(signStatus));
            paramterMap.put("PURCHASER", String.valueOf(signName));
            paramterMap.put("PURCHASER_DATE", date);
            paramterMap.put("DELIVERY_NO", waybillNo);
            DAOUtil.execSqlByParamter(sql.toString(), paramterMap);


            // 更新配件发运单明细

            sql = new StringBuilder();
            sql.append("select * from tt_pt_ec_delivery_order_item where  EC_DELIVERY_ORDER_ID = ?");
            params.clear();
            params.add(String.valueOf(deliveryOrderId));
            List<Map> mapList = DAOUtil.findAll(sql.toString(), params);
            sql = new StringBuilder();
            sql.append("update tt_pt_delivery_order_item tpdoi \n" +
                    "set \n" +
                    "tpdoi.THIS_SIGN_QUANTITY =  #[THIS_SIGN_QUANTITY] ,\n" +
                    "tpdoi.LJ_SIGN_QUANTITY = #[LJ_SIGN_QUANTITY] where EC_DELIVERY_ORDER_ID = #[EC_DELIVERY_ORDER_ID]  ");
            for (Map map1 : mapList) {
                Object itemId = map1.get("ITEM_ID");
                Object thisSignQuantity = map1.get("THIS_SIGN_QUANTITY");
                Object ljSignQuantity = map1.get("LJ_SIGN_QUANTITY");
                Double ljSignQuantityDouble = Double.valueOf(String.valueOf(ljSignQuantity));


                Double thisSignQuantityDouble = Double.valueOf(String.valueOf(thisSignQuantity));
                ljSignQuantityDouble = ljSignQuantityDouble + thisSignQuantityDouble;

                paramterMap.clear();
                paramterMap.put("THIS_SIGN_QUANTITY", thisSignQuantityDouble);
                paramterMap.put("LJ_SIGN_QUANTITY", ljSignQuantityDouble);
                paramterMap.put("EC_DELIVERY_ORDER_ID", String.valueOf(deliveryOrderId));
                DAOUtil.execSqlByParamter(sql.toString(), paramterMap);

                // 登记签收记录
                sql = new StringBuilder();
                sql.append("INSERT into tt_pt_delivery_order_sign(SIGN_NO,ITEM_ID,THIS_SIGN_QUANTITY,RECORD_VERSION,CREATED_BY,CREATED_BYNAME,CREATED_AT)\n");
                sql.append("SELECT ?,? ,?,0,?,?,NOW()");
                params = new ArrayList<>();
                String SNO = commonNoService.getSystemOrderNo("SI" + DAOUtil.getSessionDealerCode());
                params.add(SNO);
                params.add(String.valueOf(itemId));
                params.add(thisSignQuantityDouble);
                params.add("gap15app");
                params.add("gap15app");
                DAOUtil.execBatchPreparement(sql.toString(), params);
            }


        } catch (Exception e) {
            logger.error("app-grt gap15 接口处理失败 : " + e.getMessage());
            responsesMap.put("ERROR_MSG", e.getMessage());
            return responsesMap;

        }
        responsesMap.put("status", "0");
        responsesMap.put("message", "请求处理成功!");
        return responsesMap;
    }

}
