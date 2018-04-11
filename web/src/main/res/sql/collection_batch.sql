#sql("findBatchNo")
	SELECT * FROM unionpay_batch_collection_batchno  WHERE 1=1
		#if(txnTime)
			AND txnDate = LEFT (#para(txnTime), 8)
	   #end
#end
#sql("updateBatchNo")
	UPDATE unionpay_batch_collection_batchno  SET batchNo = #para(newBatchNo) WHERE 1=1 
		AND txnDate = LEFT (#para(txnTime), 8) 
	    AND batchNo = #para(batchNo)
#end
#sql("updateToBeSentUnionpayCollectionBatchId")
	UPDATE unionpay_collection  SET batchId = #para(batchId) , status = '1' , mat = #para(mat) WHERE 
	    txnType = '21' AND txnSubType = '02' AND status = '0' 
	    #if(merId)
	    	AND merId = #para(merId)
	    #end
	    ORDER BY cat 
	    LIMIT 500
#end
#sql("findToBeSentUnionpayCollectionByBatchId")
	SELECT * FROM unionpay_collection  WHERE 
	    txnType = '21' AND txnSubType = '02' AND status = '1'
	    #if(batchId)
	    	AND batchId = #para(batchId)
	    #end
	    #if(merId)
	    	AND merId = #para(merId)
	    #end
#end




#sql("findNeedQueryBatchCollection")
	SELECT ubc.* FROM unionpay_batch_collection ubc WHERE
		respCode = '00' AND finalCode = '1'
		AND (
			ubc.queryResultCount IS NULL
			OR ubc.queryResultCount < 5
		)
		AND round(
			(
				UNIX_TIMESTAMP(now()) - UNIX_TIMESTAMP(cat)
			) / 60
		) > 60
#end

