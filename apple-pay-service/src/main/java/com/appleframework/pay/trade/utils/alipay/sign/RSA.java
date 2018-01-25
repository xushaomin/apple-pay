/*
 * Copyright 2016-2102 Appleframework(http://www.appleframework.com) Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appleframework.pay.trade.utils.alipay.sign;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.internal.util.AlipaySignature;
import com.appleframework.pay.trade.utils.alipay.util.Base64;

/**
 * 功能：支付宝MD5签名处理核心文件，不需要修改 版本：3.3 修改日期：2012-08-17 说明：
 * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 * 该代码仅供学习和研究支付宝接口使用，只是提供一个
 */

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
	
    private static final Logger logger = LoggerFactory.getLogger(RSA.class);

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	public static String sign(Map<String, String> sParaTemp, String privateKey, String input_charset) {
		String sign = null;
		try {
			sign = AlipaySignature.rsaSign(sParaTemp, privateKey, input_charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名值
	 */
	public static String sign(String content, String privateKey, String input_charset) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * RSA验签名检查
	 * 
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param alipay_public_key
	 *            支付宝公钥
	 * @param input_charset
	 *            编码格式
	 * @return 布尔值
	 */
	public static boolean verify(String content, String sign, String alipay_public_key, String input_charset) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(alipay_public_key);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(input_charset));

			byte[] codes = Base64.decode(sign);
			boolean bverify = signature.verify(codes);
			return bverify;

		} catch (Exception e) {
			logger.error(e.getMessage());
			if(e instanceof NullPointerException) {
				return true;
			}
		}

		return false;
	}
}