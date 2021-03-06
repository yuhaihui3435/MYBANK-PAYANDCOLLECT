package com.mybank.pc.collection.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.mybank.pc.collection.model.base.BaseUnionpayBatchCollection;
import com.mybank.pc.collection.model.sender.BatchRequestBuilder;
import com.mybank.pc.collection.model.sender.SendProxy;
import com.mybank.pc.kits.unionpay.acp.SDK;
import com.mybank.pc.kits.unionpay.acp.SDKConfig;
import com.mybank.pc.kits.unionpay.acp.SDKConstants;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.BatchCollectionRequest;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.RequestContent;
import com.mybank.pc.kits.unionpay.acp.file.collection.model.RequestHead;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * Generated by JFinal.
 */
public class UnionpayBatchCollection extends BaseUnionpayBatchCollection<UnionpayBatchCollection> {
	private static final long serialVersionUID = 1L;

	/**
	 * 一般批量请求交易发起10分钟后，批量交易查询还是返回34，则可以认为批量请求交易失败<br>
	 * 保险起见设置为15分钟
	 * 
	 */
	public static final int TIMEOUT_MINUTE = 15;

	public static final UnionpayBatchCollection dao = new UnionpayBatchCollection().dao();

	private SendProxy sendProxy;

	public UnionpayBatchCollection assemblyBatchRequest() {
		this.sendProxy = new BatchRequestBuilder(this).build();
		setReq(JsonKit.toJson(sendProxy.getReqData()));
		return this;
	}

	public SendProxy sendBatchOrder() throws Exception {
		if (sendProxy == null) {
			assemblyBatchRequest();
		}
		sendProxy.send();
		this.setResp(JsonKit.toJson(sendProxy.getRspData()));
		return sendProxy;
	}

	public boolean validateBatchOrderResp() {
		return sendProxy.validateResp();
	}

	public UnionpayBatchCollectionQuery buildQuery() {
		Date now = new Date();
		UnionpayBatchCollectionQuery query = new UnionpayBatchCollectionQuery();
		String reqReserved = "from=pac";

		query.setVersion(getVersion());
		query.setEncoding(getEncoding());
		query.setTxnType("22");
		query.setTxnSubType("02");
		query.setBizType("000501");
		query.setChannelType("07");
		query.setAccessType("0");
		query.setMerId(getMerId());
		query.setBatchNo(getBatchNo());
		query.setTxnTime(getTxnTime());
		query.setReqReserved(reqReserved);
		query.setCat(now);
		query.setMat(now);

		query.assemblyBatchQueryRequest();
		return query;
	}

	public static UnionpayBatchCollection buildUnionpayBatchCollection(Date now, String txnTime, String batchNo,
			String merId, List<UnionpayCollection> toBeSentOrder) {
		// 银联调用相关参数
		String txnType = "21"; // 交易类型
		String txnSubType = "02"; // 交易子类型
		String channelType = "07";// 渠道类型
		String accessType = "0";// 接入类型，商户接入固定填0，不需修改
		String bizType = "000501";// 业务类型

		// 平台调用相关参数
		SDK sdk = SDK.getByMerId(merId);
		SDKConfig sdkConfig = sdk.getSdkConfig();

		String finalCode = "3";// 最终处理结果：0成功 1处理中 2失败 3待初始化
		String status = "0";// 0 待查询 1 查询中
		String reqReserved = "from=pac";

		UnionpayBatchCollection unionpayBatchCollection = new UnionpayBatchCollection();
		unionpayBatchCollection.setVersion(sdkConfig.getVersion());
		unionpayBatchCollection.setEncoding(SDKConstants.UTF_8_ENCODING);
		unionpayBatchCollection.setTxnType(txnType);
		unionpayBatchCollection.setTxnSubType(txnSubType);
		unionpayBatchCollection.setBizType(bizType);
		unionpayBatchCollection.setChannelType(channelType);
		unionpayBatchCollection.setAccessType(accessType);
		unionpayBatchCollection.setMerId(sdk.getMerId());
		unionpayBatchCollection.setBatchNo(batchNo);
		unionpayBatchCollection.setTxnTime(txnTime);
		unionpayBatchCollection.setQueryResultCount(0);
		unionpayBatchCollection.setFinalCode(finalCode);
		unionpayBatchCollection.setReqReserved(reqReserved);
		unionpayBatchCollection.setReqReserved1(reqReserved);
		unionpayBatchCollection.setStatus(status);
		unionpayBatchCollection.setCat(now);
		unionpayBatchCollection.setMat(now);

		if (CollectionUtils.isNotEmpty(toBeSentOrder)) {
			unionpayBatchCollection.claimToBeSentOrder(toBeSentOrder);
		}

		return unionpayBatchCollection;
	}

	public UnionpayBatchCollection claimToBeSentOrder(List<UnionpayCollection> toBeSentOrder) {
		BatchCollectionRequest batchCollectionRequest = buildBatchCollectionRequest(toBeSentOrder);
		RequestHead requestHead = batchCollectionRequest.getHead();

		setTotalQty(requestHead.getTotalNumber());
		setTotalAmt(requestHead.getTotalAmount());
		setRequestFileContent(batchCollectionRequest.getTxtFileContent());
		setQueryResultCount(0);
		return this;
	}

	private BatchCollectionRequest buildBatchCollectionRequest(List<UnionpayCollection> unionpayCollections) {
		BatchCollectionRequest batchCollectionRequest = new BatchCollectionRequest();
		RequestHead requestHead = new RequestHead();

		BigDecimal totalAmt = new BigDecimal("0");
		int totalNumber = unionpayCollections.size();
		RequestContent requestContent = null;
		String txnAmt = null;
		for (UnionpayCollection unionpayCollection : unionpayCollections) {
			requestContent = new RequestContent();
			txnAmt = unionpayCollection.getTxnAmt();

			requestContent.setOrderId(unionpayCollection.getOrderId());
			requestContent.setCurrencyCode(unionpayCollection.getCurrencyCode());
			requestContent.setTxnAmt(txnAmt);
			requestContent.setAccType(unionpayCollection.getAccType());
			requestContent.setAccNo(unionpayCollection.getAccNo());
			requestContent.setCustomerNm(unionpayCollection.getCustomerNm());
			requestContent.setBizType(unionpayCollection.getBizType());
			requestContent.setCertifTp(unionpayCollection.getCertifTp());
			requestContent.setCertifId(unionpayCollection.getCertifId());
			requestContent.setPhoneNo(unionpayCollection.getPhoneNo());
			requestContent.setPostscript(unionpayCollection.getPostscript());
			requestContent.setReqReserved1(unionpayCollection.getReqReserved1());

			batchCollectionRequest.addContent(requestContent);
			totalAmt = totalAmt.add(new BigDecimal(txnAmt));
		}
		requestHead.setTotalAmount(String.valueOf(totalAmt.longValue()));
		requestHead.setTotalNumber(String.valueOf(totalNumber));
		requestHead.setReqReserved1(getReqReserved1());
		requestHead.setReqReserved2(getReqReserved2());
		batchCollectionRequest.setHead(requestHead);

		return batchCollectionRequest;
	}

	static int[] blankingTime = new int[] { 120, 30, 100, 50, 60, 90 };

	public void setNextAllowQueryDate() {
		Date nextQueryTime = null;
		if ((nextQueryTime = getNextQueryTime()) == null) {
			nextQueryTime = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nextQueryTime);

		Integer queryResultCount = getQueryResultCount();
		if (queryResultCount == null || queryResultCount < 1) {
			queryResultCount = 1;
		}
		calendar.add(Calendar.MINUTE, blankingTime[(queryResultCount - 1) % blankingTime.length]);
		setNextQueryTime(calendar.getTime());
	}

	public boolean allowQuery() {
		Integer queryResultCount = getQueryResultCount();
		Date nextQueryTime = getNextQueryTime();
		// 查询次数低于指定次数
		boolean accum = (queryResultCount == null || queryResultCount <= 10);
		// 适当的查询时间
		long betweenMinute = DateUtil.between(nextQueryTime == null ? getCat() : nextQueryTime, new Date(),
				DateUnit.MINUTE, false);
		accum = accum && betweenMinute >= (nextQueryTime == null ? 120 : 0);

		return accum;
	}

	public static UnionpayBatchCollection findByIdOrBizColumn(String id, String merId, String batchNo, String txnTime) {
		UnionpayBatchCollection unionpayBatchCollection = dao.findById(id);
		if (unionpayBatchCollection == null) {
			unionpayBatchCollection = UnionpayBatchCollection.findUnionpayBatchCollectionOne(
					Kv.by("merId", merId).set("batchNo", batchNo).set("txnTime", txnTime));
		}
		return unionpayBatchCollection;
	}

	public BatchCollectionRequest toBatchCollectionRequest() {
		try {
			return new BatchCollectionRequest(getRequestFileContent());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int updateNeedQueryBatchCollectionPrepareOne(Kv kv) {
		SqlPara sqlPara = Db.getSqlPara("collection_batch.updateNeedQueryBatchCollectionPrepareOne", kv);
		return Db.update(sqlPara);
	}

	public static int updateNeedQueryBatchCollectionPrepare(Kv kv) {
		SqlPara sqlPara = Db.getSqlPara("collection_batch.updateNeedQueryBatchCollectionPrepare", kv);
		return Db.update(sqlPara);
	}

	public static List<UnionpayBatchCollection> findNeedQueryBatchCollectionBySysQueryId(Kv kv) {
		SqlPara sqlPara = Db.getSqlPara("collection_batch.findNeedQueryBatchCollectionBySysQueryId", kv);
		return UnionpayBatchCollection.dao.find(sqlPara);
	}

	public static List<UnionpayBatchCollection> findUnionpayBatchCollection(Kv kv) {
		SqlPara sqlPara = Db.getSqlPara("collection_batch.findUnionpayBatchCollection", kv);
		return UnionpayBatchCollection.dao.find(sqlPara);
	}

	public static UnionpayBatchCollection findUnionpayBatchCollectionOne(Kv kv) {
		SqlPara sqlPara = Db.getSqlPara("collection_batch.findUnionpayBatchCollection", kv);
		return UnionpayBatchCollection.dao.findFirst(sqlPara);
	}

	public List<UnionpayBatchCollectionQuery> findQueryHistory() {
		return UnionpayBatchCollectionQuery.find(getTxnTime(), getBatchNo(), getMerId());
	}

	@JSONField(serialize = false)
	public SendProxy getSendProxy() {
		return sendProxy;
	}

}
