package com.appleframework.pay.trade.utils.alipay.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;  

/* *
 *类名：ApplePayNotify
 *功能：苹果通知处理类
 *详细：处理苹果各接口通知返回
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考

 *************************注意*************************
 *调试通知返回时，可查看或改写log日志的写入TXT里的数据，来检查通知返回是否正常
 */
public class ApplePayNotify {

	private static final Logger logger = LoggerFactory.getLogger(ApplePayNotify.class);
	
	/**
	 * APPLE消息验证地址
	 */
	private final static String VERIFY_URL_SANDBOX = "https://sandbox.itunes.apple.com/verifyReceipt";

	private final static String VERIFY_URL_RELEASE  = "https://buy.itunes.apple.com/verifyReceipt";

	/**
	 * 苹果服务器验证
	 * 
	 * @param receipt 账单
	 * @url 要验证的地址
	 * @return null 或返回结果 沙盒 https://sandbox.itunes.apple.com/verifyReceipt
	 * 
	 */
	public static String buyAppVerify(String receipt, boolean isSandbox) {
		String url = VERIFY_URL_RELEASE;
		if (isSandbox == true) {
			url = VERIFY_URL_SANDBOX;
		}
		//String buyCode = Base64.decodeString(receipt);
		String buyCode = receipt;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
			URL console = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-type", "text/json");
			conn.setRequestProperty("Proxy-Connection", "Keep-Alive");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			BufferedOutputStream hurlBufOus = new BufferedOutputStream(conn.getOutputStream());

			String str = String.format(Locale.CHINA, "{\"receipt-data\":\"" + buyCode + "\"}");
			hurlBufOus.write(str.getBytes());
			hurlBufOus.flush();

			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return null;
	}

	/**
	 * 根据原始收据返回苹果的验证地址: * 沙箱 https://sandbox.itunes.apple.com/verifyReceipt
	 * 真正的地址 https://buy.itunes.apple.com/verifyReceipt
	 * 
	 * @param receipt
	 * @return Sandbox 测试单 Real 正式单
	 */
	public static String getEnvironment(String receipt) {
		try {
			JSONObject job = JSONObject.parseObject(receipt);
			if (job.containsKey("environment")) {
				String evvironment = job.getString("environment");
				return evvironment;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "Real";
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

}
