package com.yonyou.dmscloud.part.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yonyou.dmscloud.basedata.common.client.dms.SystemOrderNoGenService;
import com.yonyou.dmscloud.framework.DAO.DAOUtil;
import com.yonyou.dmscloud.framework.service.CommonNoService;
import com.yonyou.dmscloud.part.constants.PartCommonConstants;
import com.yonyou.dmscloud.part.constants.PartDictCodeConstants;
import com.yonyou.dmscloud.part.domains.PO.partPurchase.TtPtDistributorReturnItemPO;
import com.yonyou.dmscloud.part.domains.PO.partPurchase.TtPtDistributorReturnPO;
import com.yonyou.dmscloud.part.service.epcInterface.InterfaceEpcDataServiceImpl;
import com.yonyou.f4.mvc.annotation.TxnConn;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@TxnConn
@RequestMapping("/app")
public class AGP21Controller {

    private static Logger logger = Logger.getLogger(InterfaceEpcDataServiceImpl.class);

    @Autowired
    private CommonNoService commonNoService;


    @Autowired
    SystemOrderNoGenService systemOrderNoGenService;


    @PostMapping("/agp21/interAspect")
    public Map gap15(@RequestBody String data) {
        Map responsesMap = new HashMap<>();
        responsesMap.put("status", 1);

        JSONObject jsonObject = new JSONObject();
        JSONObject requestData = JSONObject.parseObject(data);
        // 电商订单编号
        String businessOrderNo = requestData.getString("BUSINESS_ORDER_NO");
        // 到货签收日期
        String receiveSignDate = requestData.getString("RECEIVE_SIGN_DATE");
        // 退货申请日期
        String refundApplyDate = requestData.getString("REFUND_APPLY_DATE");
        // 退货审核日期
        String refundAuditDate = requestData.getString("REFUND_AUDIT_DATE");


        // 退货说明
        String refundContent = requestData.getString("REFUND_CONTENT");
        // 回运物流单号
        String backTrackingNo = requestData.getString("BACK_TRACKING_NO");
        // 回运物流商
        String backCompany = requestData.getString("BACK_COMPANY");
        // 回运时间
        String backTime = requestData.getString("BACK_TIME");
        // 回运说明
        String backContent = requestData.getString("BACK_CONTENT");


        StringBuffer sql = new StringBuffer();
        sql.append("select * from tt_pt_ec_delivery_order where EC_ORDER_NO = ? ");
        List<Object> queryParam = Lists.newArrayList();
        queryParam.add(businessOrderNo);
        Map ttPtEcDeliveryOrder = DAOUtil.findFirst(sql.toString(), queryParam);
        // 发运单号
        String deliveryNo = String.valueOf(ttPtEcDeliveryOrder.get("DELIVERY_NO"));
        // 店代码
        String dealerCode = String.valueOf(ttPtEcDeliveryOrder.get("DEALER_CODE"));
        // 采购单号
        String orderNo = String.valueOf(ttPtEcDeliveryOrder.get("ORDER_NO"));


        TtPtDistributorReturnPO returnPO = new TtPtDistributorReturnPO();
        returnPO.set("RETURN_ORDER_NO", systemOrderNoGenService.getSystemOrderCustomized(PartCommonConstants.PART_PARTS_RETURN_PREFIX, dealerCode, PartCommonConstants.PART_BUY_IN_FORMAT, PartCommonConstants.PART_BUY_IN_NUMCOUNT));
        returnPO.set("DELIVERY_NO", deliveryNo); //发运单号
//        returnPO.set("FINISHED_DATE",returnItemDto.getFinishedDate());//提报日期
        returnPO.set("ORDER_IN_DATE", receiveSignDate); //到货签收日期
        returnPO.set("RETURN_REMARK", refundContent); //退货说明
        returnPO.set("AUDIT_STATUS", PartDictCodeConstants.PART_RETURN_60621002);//审核状态--已提交
        returnPO.set("ORDER_STATUS", PartDictCodeConstants.IN_ORDER_NOSTATUS);//审核状态--新建

        returnPO.set("DEALER_CODE", dealerCode); //店代码
//        returnPO.set("STORAGE_CODE",returnItemDto.getStorageCode()); //仓库
//        returnPO.set("PREPARED_BY",loginDto.getUserName()); //制单人
        returnPO.set("ORDER_DATE", new Date()); //制单时间
        returnPO.saveIt();


        sql = new StringBuffer();
        sql.append("select * from tt_pt_purchase_order where ORDER_NO = ? ");
        queryParam.clear();
        queryParam.add(orderNo);
        Map ttPtPurchaseOrder = DAOUtil.findFirst(sql.toString(), queryParam);
        String partBuyId = String.valueOf(ttPtPurchaseOrder.get("PART_BUY_ID"));
        ttPtPurchaseOrder.get("");

        sql = new StringBuffer();
        sql.append("select * from tt_pt_purchase_order_item where PART_BUY_ID = ?");
        queryParam.clear();
        queryParam.add(partBuyId);
        List<Map> ttPtPurchaseOrderItems = DAOUtil.findAll(sql.toString(), queryParam);

        for (Map ttPtPurchaseOrderItem : ttPtPurchaseOrderItems) {

            TtPtDistributorReturnItemPO returnItemPO = new TtPtDistributorReturnItemPO();
            returnItemPO.set("PART_RETURN_ID", returnPO.getLongId());
            returnItemPO.set("PART_NO", String.valueOf(ttPtPurchaseOrderItem.get("PART_CODE")));//配件编码
            returnItemPO.set("PART_NAME", String.valueOf(ttPtPurchaseOrderItem.get("PART_NAME"))); //配件名称
            returnItemPO.set("RETURNED_QUANTITY", ttPtPurchaseOrderItem.get("PURCHASED_QUANTITY")); //退货数量
            returnItemPO.set("TAXED_RETURN_PRICE", ttPtPurchaseOrderItem.get("PURCHASED_PRICE")); //退货单价
            returnItemPO.set("ASCRIPTION_COMPANY", ""); //归属公司
            returnItemPO.set("TAXED_RETURN_AMOUNT", ""); //退货单价
            returnItemPO.set("CONTRACT_PRICE", ttPtPurchaseOrderItem.get("CONTRACT_PRICE"));
            returnItemPO.saveIt();
        }


        responsesMap.put("status", "0");
        responsesMap.put("message", "请求处理成功!");
        return responsesMap;
    }
}
