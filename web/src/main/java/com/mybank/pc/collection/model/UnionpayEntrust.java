package com.mybank.pc.collection.model;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.JsonKit;
import com.mybank.pc.collection.model.base.BaseUnionpayEntrust;
import com.mybank.pc.kits.unionpay.acp.AcpService;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class UnionpayEntrust extends BaseUnionpayEntrust<UnionpayEntrust> {
	public static final UnionpayEntrust dao = new UnionpayEntrust().dao();

	private Map<String, String> entrustReqData = null;
	private Map<String, String> entrustRspData = null;

	public void toEntrust() {
		SDK sdk = SDK.getByMerId(getMerId());
		SDKConfig sdkConfig = sdk.getSdkConfig();

		setVersion(sdkConfig.getVersion());
		setTxnType("72");
		setTxnSubType("11");
		setAccType("01");
		setAccessType("0");
		setBizType("000501");
		setChannelType("07");
	}

	public void toRealAuthBack() {
		SDK sdk = SDK.getByMerId(getMerId());
		SDKConfig sdkConfig = sdk.getSdkConfig();

		setVersion(sdkConfig.getVersion());
		setTxnType("72");
		setTxnSubType("01");
		setAccType("01");
		setAccessType("0");
		setBizType("000501");
		setChannelType("07");
	}

	public Map<String, String> assemblyEntrustRequest() {
		Map<String, String> contentData = new HashMap<String, String>();

		SDK sdk = SDK.getByMerId(getMerId());
		SDKConfig sdkConfig = sdk.getSdkConfig();
		AcpService acpService = sdk.getAcpService();

		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		// 版本号
		contentData.put("version", sdkConfig.getVersion());
		// 字符集编码 可以使用UTF-8,GBK两种方式
		contentData.put("encoding", SDKConstants.UTF_8_ENCODING);
		// 签名方法 目前只支持01-RSA方式证书加密
		contentData.put("signMethod", sdkConfig.getSignMethod());
		// 交易类型
		contentData.put("txnType", getTxnType());
		// 交易子类型
		contentData.put("txnSubType", getTxnSubType());
		// 业务类型
		contentData.put("bizType", getBizType());
		// 渠道类型
		contentData.put("channelType", getChannelType());

		/*** 商户接入参数 ***/
		// 商户号码
		contentData.put("merId", getMerId());
		// 接入类型，商户接入固定填0，不需修改
		contentData.put("accessType", getAccessType());
		// 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
		contentData.put("orderId", getOrderId());
		// 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		contentData.put("txnTime", getTxnTime());
		// 账号类型
		contentData.put("accType", getAccType());

		// 姓名，证件类型+证件号码至少二选一必送，手机号可选，贷记卡的cvn2,expired可选。
		Map<String, String> customerInfoMap = new HashMap<String, String>();
		customerInfoMap.put("certifTp", getCertifTp()); // 证件类型
		customerInfoMap.put("certifId", getCertifId()); // 证件号码
		customerInfoMap.put("customerNm", getCustomerNm()); // 姓名

		customerInfoMap.put("phoneNo", getPhoneNo()); // 手机号
		// 当卡号为贷记卡的时候cvn2,expired可选上送
		customerInfoMap.put("cvn2", getCvn2()); // 卡背面的cvn2三位数字
		customerInfoMap.put("expired", getExpired()); // 有效期 年在前月在后

		// 如果商户号开通了【商户对敏感信息加密】的权限那么需要对
		// accNo，pin和phoneNo，cvn2，expired加密（如果这些上送的话），对敏感信息加密使用：
		String accNoEnc = acpService.encryptData(getAccNo(), SDKConstants.UTF_8_ENCODING); // 这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		contentData.put("accNo", accNoEnc);
		contentData.put("encryptCertId", acpService.getEncryptCertId()); // 加密证书的certId，配置在acp_sdk.properties文件
																			// acpsdk.encryptCert.path属性下
		String customerInfoStr = acpService.getCustomerInfoWithEncrypt(customerInfoMap, null,
				SDKConstants.UTF_8_ENCODING);

		// 如果商户号未开通【商户对敏感信息加密】权限那么不需对敏感信息加密使用：
		// contentData.put("accNo", "6216261000000000018");
		// //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号
		// String customerInfoStr =
		// DemoBase.getCustomerInfo(customerInfoMap,null);

		contentData.put("customerInfo", customerInfoStr);
		this.setReq(JsonKit.toJson(contentData));
		// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		entrustReqData = acpService.sign(contentData, SDKConstants.UTF_8_ENCODING);
		return entrustReqData;
	}

	public Map<String, String> sendEntrustRequest() throws Exception {
		SDK sdk = SDK.getByMerId(getMerId());
		SDKConfig sdkConfig = sdk.getSdkConfig();
		AcpService acpService = sdk.getAcpService();

		// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的acpsdk.backTransUrl
		String requestBackUrl = sdkConfig.getBackRequestUrl();
		// 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;
		// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		if (entrustReqData == null) {
			assemblyEntrustRequest();
		}
		entrustRspData = acpService.post(this.entrustReqData, requestBackUrl, SDKConstants.UTF_8_ENCODING); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		this.setResp(JsonKit.toJson(entrustRspData));
		return entrustRspData;
	}
}
