#sql("collectTradeByMerchant")
  select ct.merchantID as mId,(select merchantName from merchant_info where id=ct.merchantID) as mName,
  (select merchantNo from merchant_info where id=ct.merchantID) as mNo,
  count(ct.id) as tradeCount,sum(ct.amount) as amount,sum(ct.merFee) as fee,
  sum(ct.bankFee) as bankFee
   from collection_trade ct WHERE ct.finalCode='0' and ct.tradeTime<?
   AND ct.clearStatus!='0' AND ct.dat is NULL GROUP BY ct.merchantID
#end
#sql("collectAllTrade")
  select
  count(ct.id) as tradeCount,sum(ct.amount) as amount,sum(ct.merFee) as fee,
  sum(ct.bankFee) as bankFee
   from collection_trade ct WHERE ct.finalCode='0' and ct.tradeTime<?
   AND ct.clearStatus!='0' AND ct.dat is NULL
#end

#sql("findByMerchant")
select *
from collection_trade ct WHERE ct.finalCode='0' and ct.tradeTime<? AND ct.merchantID=?
                               AND ct.clearStatus!='0' AND ct.dat is NULL
#end

#sql("findPage")
SELECT *,(cc.amountFeeSum-cc.bankFee) as profit  FROM collection_clear cc WHERE 1=1
    #for(x : cond)
       AND #(x.key) #para(x.value)
    #end
  ORDER BY cc.cat DESC

#end


#sql("findTotalPage")
SELECT *,(cc.amountFeeSum-cc.bankFee) as profit FROM collection_cleartotle cc WHERE 1=1
#for(x : cond)
  AND #(x.key) #para(x.value)
#end
ORDER BY cc.cat DESC

#end


#sql("sum")
SELECT
  sum(cc.tradeCount) as tradeCount,
  sum(cc.amountSum) as amountSum,
  sum(cc.amountFeeSum) as amountFeeSum,
  sum(cc.accountFee) as accountFee,
  sum(cc.tradeFee) as tradeFee,
  sum(cc.amountOff) as amountOff,
  sum(cc.bankFee) as bankFee,
  sum(cc.amountFeeSum-cc.bankFee) as profit,
  cc.merNO,(select merchantName from merchant_info where id=cc.merID) as merName
FROM collection_clear cc WHERE 1=1
#for(x : cond)
 AND  #(x.key) #para(x.value)
#end
GROUP BY merID

#end

#sql("sumTotal")
SELECT sum(cc.tradeCount) as tradeCount,
  sum(cc.amountSum) as amountSum,
  sum(cc.amountFeeSum) as amountFeeSum,
  sum(cc.accountFee) as accountFee,
  sum(cc.tradeFee) as tradeFee,
  sum(cc.amountOff) as amountOff,
  sum(cc.bankFee) as bankFee,
  sum(cc.amountFeeSum-cc.bankFee) as profit
FROM collection_cleartotle cc WHERE 1=1
#for(x : cond)
 AND  #(x.key) #para(x.value)
#end

#end