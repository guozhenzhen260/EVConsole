/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.example.alizhifu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;
import com.alipay.api.internal.util.codec.Base64;

/**
 * 
 * @author runzhi
 */
public class AlipaySignature {

    /** RSA鏈�澶у姞瀵嗘槑鏂囧ぇ灏�  */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** RSA鏈�澶цВ瀵嗗瘑鏂囧ぇ灏�   */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String getSignatureContent(RequestParametersHolder requestHolder) {
        Map<String, String> sortedParams = new TreeMap<String, String>();
        Map appParams = requestHolder.getApplicationParams();
        if (appParams != null && appParams.size() > 0) {
            sortedParams.putAll(appParams);
        }
        Map protocalMustParams = requestHolder.getProtocalMustParams();
        if (protocalMustParams != null && protocalMustParams.size() > 0) {
            sortedParams.putAll(protocalMustParams);
        }
        Map protocalOptParams = requestHolder.getProtocalOptParams();
        if (protocalOptParams != null && protocalOptParams.size() > 0) {
            sortedParams.putAll(protocalOptParams);
        }

        return getSignContent(sortedParams);
    }

    /**
     * 
     * @param sortedParams
     * @return
     */
    public static String getSignContent(Map<String, String> sortedParams) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(keys);
        int index = 0;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sortedParams.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                content.append((index == 0 ? "" : "&") + key + "=" + value);
                index++;
            }
        }
        return content.toString();
    }

    public static String rsaSign(String content, String privateKey, String charset)
                                                                                   throws Exception {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8("RSA",
                new ByteArrayInputStream(privateKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                .getInstance("SHA1WithRSA");

            signature.initSign(priKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
        	  //throw new Exception("RSAcontent = " + content + "; charset = " + charset, e);;
        }
        return "";
    }

    public static String rsaSign(Map<String, String> params, String privateKey, String charset)
                                                                                               throws Exception {
        String signContent = getSignContent(params);

        return rsaSign(signContent, privateKey, charset);

    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins)
                                                                                      throws Exception {
        if (ins == null || StringUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static String getSignCheckContentV1(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove("sign");
        params.remove("sign_type");

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }

    public static String getSignCheckContentV2(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove("sign");

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }

    public static boolean rsaCheckV1(Map<String, String> params, String publicKey, String charset)
                                                                                                  throws Exception {
        String sign = params.get("sign");
        String content = getSignCheckContentV1(params);

        return rsaCheckContent(content, sign, publicKey, charset);
    }

    public static boolean rsaCheckV2(Map<String, String> params, String publicKey, String charset)
                                                                                                  throws Exception {
        String sign = params.get("sign");
        String content = getSignCheckContentV2(params);

        return rsaCheckContent(content, sign, publicKey, charset);
    }

    public static boolean rsaCheckContent(String content, String sign, String publicKey,
                                          String charset) throws Exception {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey
                .getBytes()));

            java.security.Signature signature = java.security.Signature
                .getInstance("SHA1WithRSA");

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new Exception("RSAcontent = " + content + ",sign=" + sign
                                         + ",charset = " + charset, e);
        }
    }

    public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins)
                                                                                   throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    /**
     * 楠岀骞惰В瀵�
     * <p>
     * <b>鐩墠閫傜敤浜庡叕浼楀彿</b><br>
     * params鍙傛暟绀轰緥锛�
     * <br>{
     *    <br>biz_content=M0qGiGz+8kIpxe8aF4geWJdBn0aBTuJRQItLHo9R7o5JGhpic/MIUjvXo2BLB++BbkSq2OsJCEQFDZ0zK5AJYwvBgeRX30gvEj6eXqXRt16/IkB9HzAccEqKmRHrZJ7PjQWE0KfvDAHsJqFIeMvEYk1Zei2QkwSQPlso7K0oheo/iT+HYE8aTATnkqD/ByD9iNDtGg38pCa2xnnns63abKsKoV8h0DfHWgPH62urGY7Pye3r9FCOXA2Ykm8X4/Bl1bWFN/PFCEJHWe/HXj8KJKjWMO6ttsoV0xRGfeyUO8agu6t587Dl5ux5zD/s8Lbg5QXygaOwo3Fz1G8EqmGhi4+soEIQb8DBYanQOS3X+m46tVqBGMw8Oe+hsyIMpsjwF4HaPKMr37zpW3fe7xOMuimbZ0wq53YP/jhQv6XWodjT3mL0H5ACqcsSn727B5ztquzCPiwrqyjUHjJQQefFTzOse8snaWNQTUsQS7aLsHq0FveGpSBYORyA90qPdiTjXIkVP7mAiYiAIWW9pCEC7F3XtViKTZ8FRMM9ySicfuAlf3jtap6v2KPMtQv70X+hlmzO/IXB6W0Ep8DovkF5rB4r/BJYJLw/6AS0LZM9w5JfnAZhfGM2rKzpfNsgpOgEZS1WleG4I2hoQC0nxg9IcP0Hs+nWIPkEUcYNaiXqeBc=,
     *    <br>sign=rlqgA8O+RzHBVYLyHmrbODVSANWPXf3pSrr82OCO/bm3upZiXSYrX5fZr6UBmG6BZRAydEyTIguEW6VRuAKjnaO/sOiR9BsSrOdXbD5Rhos/Xt7/mGUWbTOt/F+3W0/XLuDNmuYg1yIC/6hzkg44kgtdSTsQbOC9gWM7ayB4J4c=,
     *    sign_type=RSA,
     *    <br>charset=UTF-8
     * <br>}
     * </p>
     * @param params
     * @param alipayPublicKey 鏀粯瀹濆叕閽�
     * @param cusPrivateKey   鍟嗘埛绉侀挜
     * @param isCheckSign     鏄惁楠岀
     * @param isDecrypt       鏄惁瑙ｅ瘑
     * @return 瑙ｅ瘑鍚庢槑鏂囷紝楠岀澶辫触鍒欏紓甯告姏鍑�
     * @throws Exception 
     */
    public static String checkSignAndDecrypt(Map<String, String> params, String alipayPublicKey,
                                             String cusPrivateKey, boolean isCheckSign,
                                             boolean isDecrypt) throws Exception {
        String charset = params.get("charset");
        String bizContent = params.get("biz_content");
        if (isCheckSign) {
            if (!rsaCheckV2(params, alipayPublicKey, charset)) {
                throw new Exception("rsaCheck failure:rsaParams=" + params);
            }
        }

        if (isDecrypt) {
            return rsaDecrypt(bizContent, cusPrivateKey, charset);
        }

        return bizContent;
    }

    /**
     * 鍔犲瘑骞剁鍚�<br>
     * <b>鐩墠閫傜敤浜庡叕浼楀彿</b>
     * @param bizContent      寰呭姞瀵嗐�佺鍚嶅唴瀹�
     * @param alipayPublicKey 鏀粯瀹濆叕閽�
     * @param cusPrivateKey   鍟嗘埛绉侀挜
     * @param charset         瀛楃闆嗭紝濡俇TF-8, GBK, GB2312
     * @param isEncrypt       鏄惁鍔犲瘑锛宼rue-鍔犲瘑  false-涓嶅姞瀵�
     * @param isSign          鏄惁绛惧悕锛宼rue-绛惧悕  false-涓嶇鍚�
     * @return 鍔犲瘑銆佺鍚嶅悗xml鍐呭瀛楃涓�
     * <p>
     * 杩斿洖绀轰緥锛�
     * <alipay>
     *  <response>瀵嗘枃</response>
     *  <encryption_type>RSA</encryption_type>
     *  <sign>sign</sign>
     *  <sign_type>RSA</sign_type>
     * </alipay>
     * </p>
     * @throws Exception 
     */
    public static String encryptAndSign(String bizContent, String alipayPublicKey,
                                        String cusPrivateKey, String charset, boolean isEncrypt,
                                        boolean isSign) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isEmpty(charset)) {
            charset = "GBK";
        }
        sb.append("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>");
        if (isEncrypt) {// 鍔犲瘑
            sb.append("<alipay>");
            String encrypted = rsaEncrypt(bizContent, alipayPublicKey, charset);
            sb.append("<response>" + encrypted + "</response>");
            sb.append("<encryption_type>RSA</encryption_type>");
            if (isSign) {
                String sign = rsaSign(encrypted, cusPrivateKey, charset);
                sb.append("<sign>" + sign + "</sign>");
                sb.append("<sign_type>RSA</sign_type>");
            }
            sb.append("</alipay>");
        } else if (isSign) {// 涓嶅姞瀵嗭紝浣嗛渶瑕佺鍚�
            sb.append("<alipay>");
            sb.append("<response>" + bizContent + "</response>");
            String sign = rsaSign(bizContent, cusPrivateKey, charset);
            sb.append("<sign>" + sign + "</sign>");
            sb.append("<sign_type>RSA</sign_type>");
            sb.append("</alipay>");
        } else {// 涓嶅姞瀵嗭紝涓嶅姞绛�
            sb.append(bizContent);
        }
        return sb.toString();
    }

    /**
     * 鍏挜鍔犲瘑
     * 
     * @param content   寰呭姞瀵嗗唴瀹�
     * @param publicKey 鍏挜
     * @param charset   瀛楃闆嗭紝濡俇TF-8, GBK, GB2312
     * @return 瀵嗘枃鍐呭
     * @throws Exception
     */
    public static String rsaEncrypt(String content, String publicKey, String charset)
                                                                                     throws Exception {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] data = StringUtils.isEmpty(charset) ? content.getBytes() : content
                .getBytes(charset);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 瀵规暟鎹垎娈靛姞瀵�  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();

            return StringUtils.isEmpty(charset) ? new String(encryptedData) : new String(
                encryptedData, charset);
        } catch (Exception e) {
            throw new Exception("EncryptContent = " + content + ",charset = " + charset, e);
        }
    }

    /**
     * 绉侀挜瑙ｅ瘑
     * 
     * @param content    寰呰В瀵嗗唴瀹�
     * @param privateKey 绉侀挜
     * @param charset    瀛楃闆嗭紝濡俇TF-8, GBK, GB2312
     * @return 鏄庢枃鍐呭
     * @throws Exception
     */
    public static String rsaDecrypt(String content, String privateKey, String charset)
                                                                                      throws Exception {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8("RSA",
                new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64.decodeBase64(content
                .getBytes()) : Base64.decodeBase64(content.getBytes(charset));
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 瀵规暟鎹垎娈佃В瀵�  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            return StringUtils.isEmpty(charset) ? new String(decryptedData) : new String(
                decryptedData, charset);
        } catch (Exception e) {
            throw new Exception("EncodeContent = " + content + ",charset = " + charset, e);
        }
    }

}
