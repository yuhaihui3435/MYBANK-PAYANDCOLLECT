package com.mybank.pc.collection.model;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.kit.JsonKit;
import com.mybank.pc.collection.model.base.BaseUnionpayCollectionConsumeUndo;
import com.mybank.pc.collection.model.sender.ConsumeUndoRequestBuilder;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.exception.ValidateUnionpayRespException;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class UnionpayCollectionConsumeUndo extends BaseUnionpayCollectionConsumeUndo<UnionpayCollectionConsumeUndo> {
	public static final UnionpayCollectionConsumeUndo dao = new UnionpayCollectionConsumeUndo().dao();

	private SendProxy sendProxy;

	public UnionpayCollectionConsumeUndo assemblyRequest() {
		this.sendProxy = new ConsumeUndoRequestBuilder(this).build();
		setReq(JsonKit.toJson(sendProxy.getReqData()));
		return this;
	}

	public SendProxy sendRequest() throws Exception {
		if (sendProxy == null) {
			assemblyRequest();
		}
		sendProxy.send();
		this.setResp(JsonKit.toJson(sendProxy.getRspData()));
		return sendProxy;
	}

	public boolean validateResp() throws ValidateUnionpayRespException {
		return sendProxy.validateResp();
	}

	public UnionpayCollectionQuery buildQuery() {
		return buildQuery(null);
	}

	public UnionpayCollectionQuery buildQuery(String operID) {
		UnionpayCollectionQuery query = new UnionpayCollectionQuery();
		Date now = new Date();
		String reqReserved = "from=pac";

		query.setTxnType("00");
		query.setTxnSubType("00");
		query.setBizType("000501");
		query.setAccessType("0");

		query.setMerId(getMerId());
		query.setOrderId(getOrderId());
		query.setTxnTime(getTxnTime());

		query.setPlanId(getPlanId());
		query.setExecutionId(getExecutionId());
		query.setVersion(getVersion());
		query.setEncoding(getEncoding());
		query.setBatchNo(getBatchNo());
		query.setMerchantID(getMerchantID());
		query.setReqReserved(reqReserved);

		query.setCat(now);
		query.setMat(now);
		query.setOperID(operID);

		query.assemblyQueryRequest();
		return query;
	}

	@JSONField(serialize = false)
	public SendProxy getSendProxy() {
		return this.sendProxy;
	}
}
