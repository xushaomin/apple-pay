package com.appleframework.pay;

import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService2;
import com.egzosn.pay.wx.bean.RedpackOrder;
import com.egzosn.pay.wx.bean.WxSendredpackType;
import com.egzosn.pay.wx.bean.WxTransactionType;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 *
 *
 * 微信
 * @author egan
 * @email egzosn@gmail.com
 * @date 2017/8/18
 */
public class WxPayTest3 {

    public static void main(String[] args) {
        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setAppId("wxc931e1a7b994af8a");

        wxPayConfigStorage.setMchId("1498815502");
        //以下两个参数在 服务商版模式中必填--------
//        wxPayConfigStorage.setSubAppid("子商户公众账号ID ");
//        wxPayConfigStorage.setSubMchId("微信支付分配的子商户号 ");
        
        //wxPayConfigStorage.setSubMchId("1607855620 ");
        //-----------------------------------------------
        wxPayConfigStorage.setKeyPrivate("R1129zYsqb2L3EnBe2yGxkC8iIxehQ8z");
        //wxPayConfigStorage.setKeyPublic("R1129zYsqb2L3EnBe2yGxkC8iIxehQ8z");
        wxPayConfigStorage.setSecretKey("082d7139f439736d38e57c91b5f5401e");
        //5R9coSknYwPfuX228jzCwpZSNFAuhb8i
        //1aa7a19b6a4159e535bb132fe46ad3e2
        wxPayConfigStorage.setNotifyUrl("http://gateway.pay.appleframework.com/web-gateway/scanPayNotify/notify/WEIXIN");
        wxPayConfigStorage.setReturnUrl("http://gateway.pay.appleframework.com/web-gateway/scanPayNotify/notify/WEIXIN");
        wxPayConfigStorage.setSignType("MD5");
        wxPayConfigStorage.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境 此处暂未实现
        wxPayConfigStorage.setTest(false);
        //支付服务
        WxPayService2 service =  new WxPayService2(wxPayConfigStorage);
        //支付订单基础信息
        PayOrder payOrder = new PayOrder("订单title", "摘要",  BigDecimal.valueOf(0.01) , UUID.randomUUID().toString().replace("-", ""));
        payOrder.setExpirationTime(new Date(System.currentTimeMillis() + 3600));
        /*-----------APP-------------------*/
        payOrder.setTransactionType(WxTransactionType.APP);
        //获取APP支付所需的信息组，直接给app端就可使用
        Map appOrderInfo = service.orderInfo(payOrder);
        System.out.println(appOrderInfo);
        /*-----------/APP-------------------*/

        /*----------- WAP 网页支付-------------------*/

        payOrder.setTransactionType(WxTransactionType.MWEB); //  网页支付
        //获取支付所需的信息
        Map directOrderInfo = service.orderInfo(payOrder);
        //获取表单提交对应的字符串，将其序列化到页面即可,
        String directHtml = service.buildRequest(directOrderInfo, MethodType.POST);
        /*-----------/ WAP 网页支付-------------------*/



        /*-----------条码付 刷卡付-------------------*/
        payOrder.setTransactionType(WxTransactionType.MICROPAY);//条码付
        payOrder.setAuthCode("条码信息");
        // 支付结果
        Map params = service.microPay(payOrder);

        /*-----------/条码付 刷卡付-------------------*/

        /*-----------回调处理-------------------*/
//        HttpServletRequest request
//        params = service.getParameter2Map(request.getParameterMap(), request.getInputStream());
        if (service.verify(params)){
            System.out.println("支付成功");
            return;
        }
        System.out.println("支付失败");


        /*-----------回调处理-------------------*/

       HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
        //ssl 退款证书相关
        httpConfigStorage.setKeystore("D:/work/pay/src/main/resources/certificates/1220429901_apiclient_cert.p12");
        httpConfigStorage.setStorePassword("默认商户号");
        //设置ssl证书对应的存储方式，这里默认为文件地址
        httpConfigStorage.setCertStoreType(CertStoreType.PATH);
        service.setRequestTemplateConfigStorage(httpConfigStorage);

        RedpackOrder redpackOrder = new RedpackOrder();

        redpackOrder.setSendName("测试");
        //faymanwang- opid
        redpackOrder.setReOpenid("om3rxjhD1rhGrP6oLydMgLcN5n10");
        //红包流水
        redpackOrder.setMchBillno("red202005181");
        redpackOrder.setTotalAmount(new BigDecimal(1.5));
        redpackOrder.setSceneId("PRODUCT_1");
        //现金红包，小程序默认为1  裂变默认为3
        redpackOrder.setTotalNum(4);
        redpackOrder.setWishing("请勿领取");
        redpackOrder.setActName("请勿领取测试红包");
        redpackOrder.setRemark("测试支付-by fayman");
        //设置发红包方式
        redpackOrder.setTransferType(WxSendredpackType.SENDGROUPREDPACK);
        Map<String, Object> sendredpack = service.sendredpack(redpackOrder);
        System.out.println(sendredpack);
    }
}
