package com.mybank.pc.advance.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUnionpayAdvance<M extends BaseUnionpayAdvance<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setVersion(java.lang.String version) {
		set("version", version);
	}

	public java.lang.String getVersion() {
		return getStr("version");
	}

	public void setEncoding(java.lang.String encoding) {
		set("encoding", encoding);
	}

	public java.lang.String getEncoding() {
		return getStr("encoding");
	}

	public void setMerId(java.lang.String merId) {
		set("merId", merId);
	}

	public java.lang.String getMerId() {
		return getStr("merId");
	}

	public void setOrderId(java.lang.String orderId) {
		set("orderId", orderId);
	}

	public java.lang.String getOrderId() {
		return getStr("orderId");
	}

	public void setAccNo(java.lang.String accNo) {
		set("accNo", accNo);
	}

	public java.lang.String getAccNo() {
		return getStr("accNo");
	}

	public void setTxnTime(java.lang.String txnTime) {
		set("txnTime", txnTime);
	}

	public java.lang.String getTxnTime() {
		return getStr("txnTime");
	}

	public void setTxnAmt(java.lang.String txnAmt) {
		set("txnAmt", txnAmt);
	}

	public java.lang.String getTxnAmt() {
		return getStr("txnAmt");
	}

	public void setCertifTp(java.lang.String certifTp) {
		set("certifTp", certifTp);
	}

	public java.lang.String getCertifTp() {
		return getStr("certifTp");
	}

	public void setCertifId(java.lang.String certifId) {
		set("certifId", certifId);
	}

	public java.lang.String getCertifId() {
		return getStr("certifId");
	}

	public void setCustomerNm(java.lang.String customerNm) {
		set("customerNm", customerNm);
	}

	public java.lang.String getCustomerNm() {
		return getStr("customerNm");
	}

	public void setTxnType(java.lang.String txnType) {
		set("txnType", txnType);
	}

	public java.lang.String getTxnType() {
		return getStr("txnType");
	}

	public void setTxnSubType(java.lang.String txnSubType) {
		set("txnSubType", txnSubType);
	}

	public java.lang.String getTxnSubType() {
		return getStr("txnSubType");
	}

	public void setQueryId(java.lang.String queryId) {
		set("queryId", queryId);
	}

	public java.lang.String getQueryId() {
		return getStr("queryId");
	}

	public void setBizType(java.lang.String bizType) {
		set("bizType", bizType);
	}

	public java.lang.String getBizType() {
		return getStr("bizType");
	}

	public void setChannelType(java.lang.String channelType) {
		set("channelType", channelType);
	}

	public java.lang.String getChannelType() {
		return getStr("channelType");
	}

	public void setAccessType(java.lang.String accessType) {
		set("accessType", accessType);
	}

	public java.lang.String getAccessType() {
		return getStr("accessType");
	}

	public void setAcqInsCode(java.lang.String acqInsCode) {
		set("acqInsCode", acqInsCode);
	}

	public java.lang.String getAcqInsCode() {
		return getStr("acqInsCode");
	}

	public void setMerCatCode(java.lang.String merCatCode) {
		set("merCatCode", merCatCode);
	}

	public java.lang.String getMerCatCode() {
		return getStr("merCatCode");
	}

	public void setMerName(java.lang.String merName) {
		set("merName", merName);
	}

	public java.lang.String getMerName() {
		return getStr("merName");
	}

	public void setMerAbbr(java.lang.String merAbbr) {
		set("merAbbr", merAbbr);
	}

	public java.lang.String getMerAbbr() {
		return getStr("merAbbr");
	}

	public void setSubMerId(java.lang.String subMerId) {
		set("subMerId", subMerId);
	}

	public java.lang.String getSubMerId() {
		return getStr("subMerId");
	}

	public void setSubMerName(java.lang.String subMerName) {
		set("subMerName", subMerName);
	}

	public java.lang.String getSubMerName() {
		return getStr("subMerName");
	}

	public void setSubMerAbbr(java.lang.String subMerAbbr) {
		set("subMerAbbr", subMerAbbr);
	}

	public java.lang.String getSubMerAbbr() {
		return getStr("subMerAbbr");
	}

	public void setAccType(java.lang.String accType) {
		set("accType", accType);
	}

	public java.lang.String getAccType() {
		return getStr("accType");
	}

	public void setCurrencyCode(java.lang.String currencyCode) {
		set("currencyCode", currencyCode);
	}

	public java.lang.String getCurrencyCode() {
		return getStr("currencyCode");
	}

	public void setReqReserved(java.lang.String reqReserved) {
		set("reqReserved", reqReserved);
	}

	public java.lang.String getReqReserved() {
		return getStr("reqReserved");
	}

	public void setReq(java.lang.String req) {
		set("req", req);
	}

	public java.lang.String getReq() {
		return getStr("req");
	}

	public void setRespCode(java.lang.String respCode) {
		set("respCode", respCode);
	}

	public java.lang.String getRespCode() {
		return getStr("respCode");
	}

	public void setRespMsg(java.lang.String respMsg) {
		set("respMsg", respMsg);
	}

	public java.lang.String getRespMsg() {
		return getStr("respMsg");
	}

	public void setResp(java.lang.String resp) {
		set("resp", resp);
	}

	public java.lang.String getResp() {
		return getStr("resp");
	}

	public void setResultCode(java.lang.String resultCode) {
		set("resultCode", resultCode);
	}

	public java.lang.String getResultCode() {
		return getStr("resultCode");
	}

	public void setResultMsg(java.lang.String resultMsg) {
		set("resultMsg", resultMsg);
	}

	public java.lang.String getResultMsg() {
		return getStr("resultMsg");
	}

	public void setResult(java.lang.String result) {
		set("result", result);
	}

	public java.lang.String getResult() {
		return getStr("result");
	}

	public void setTraceNo(java.lang.String traceNo) {
		set("traceNo", traceNo);
	}

	public java.lang.String getTraceNo() {
		return getStr("traceNo");
	}

	public void setTraceTime(java.lang.String traceTime) {
		set("traceTime", traceTime);
	}

	public java.lang.String getTraceTime() {
		return getStr("traceTime");
	}

	public void setSettleAmt(java.lang.String settleAmt) {
		set("settleAmt", settleAmt);
	}

	public java.lang.String getSettleAmt() {
		return getStr("settleAmt");
	}

	public void setSettleCurrencyCode(java.lang.String settleCurrencyCode) {
		set("settleCurrencyCode", settleCurrencyCode);
	}

	public java.lang.String getSettleCurrencyCode() {
		return getStr("settleCurrencyCode");
	}

	public void setSettleDate(java.lang.String settleDate) {
		set("settleDate", settleDate);
	}

	public java.lang.String getSettleDate() {
		return getStr("settleDate");
	}

	public void setFinalCode(java.lang.String finalCode) {
		set("finalCode", finalCode);
	}

	public java.lang.String getFinalCode() {
		return getStr("finalCode");
	}

	public void setReserved(java.lang.String reserved) {
		set("reserved", reserved);
	}

	public java.lang.String getReserved() {
		return getStr("reserved");
	}

	public void setMerchantID(java.lang.String merchantID) {
		set("merchantID", merchantID);
	}

	public java.lang.String getMerchantID() {
		return getStr("merchantID");
	}

	public void setMerFee(java.lang.String merFee) {
		set("merFee", merFee);
	}

	public java.lang.String getMerFee() {
		return getStr("merFee");
	}

	public void setExceInfo(java.lang.String exceInfo) {
		set("exceInfo", exceInfo);
	}

	public java.lang.String getExceInfo() {
		return getStr("exceInfo");
	}

	public void setQueryResultCount(java.lang.Integer queryResultCount) {
		set("queryResultCount", queryResultCount);
	}

	public java.lang.Integer getQueryResultCount() {
		return getInt("queryResultCount");
	}

	public void setCat(java.util.Date cat) {
		set("cat", cat);
	}

	public java.util.Date getCat() {
		return get("cat");
	}

	public void setMat(java.util.Date mat) {
		set("mat", mat);
	}

	public java.util.Date getMat() {
		return get("mat");
	}

	public void setOperID(java.lang.String operID) {
		set("operID", operID);
	}

	public java.lang.String getOperID() {
		return getStr("operID");
	}

}
