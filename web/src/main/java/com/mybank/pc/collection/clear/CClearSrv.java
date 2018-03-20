package com.mybank.pc.collection.clear;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mybank.pc.Consts;
import com.mybank.pc.collection.model.CollectionClear;
import com.mybank.pc.collection.model.CollectionCleartotle;
import com.mybank.pc.collection.model.CollectionTrade;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.merchant.model.MerchantInfo;
import org.junit.Before;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CClearSrv {

    /**
     * 交易数据统计 按商户分组
     * @return
     */
    public List<TradeModel> collectionTradeByMerchant(Date time){
        String sql=Db.getSql("collection_clear.collectTradeByMerchant");
        List<Record> list=Db.find(sql,time);
        List<TradeModel> ret=new ArrayList<>();
        TradeModel tm=null;
        for (Record record:list){
            tm=new TradeModel();
            tm.setmId(record.getInt("mId"));
            tm.setmName(record.getStr("mName"));
            tm.setmNo(record.getStr("mNo"));
            tm.setTradeAmount(record.getBigDecimal("amount"));
            tm.setTradeCount(record.getInt("tradeCount"));
            tm.setTradeFee(record.getBigDecimal("fee"));
            tm.setTradeBankFee(record.getBigDecimal("bankFee"));
            ret.add(tm);
        }
        return ret;
    }


    /**
     * 清分处理，
     * @param date 设定清分日期
     *
     *
     *             查询所有等待清分的交易数据，以系统设定的日切时间为准，
     *             计算清分的金额，先从商户预付中扣除手续费，如果不够则先扣掉预存的，然后再在交易金额中扣除。
     *             在将当日所有的清分数据汇总保存到汇总表中。
     *             最后更新交易数据的清分状态。
     *
     *             整个处理过程需要保证数据的一致性，故所有的数据库操作，在一个事务中进行。
     */
    public void doClear(Date date){
        LogKit.info("日切处理开始，开始清分交易数据======》》》》");
        Date now=date==null?new Date():date;
        LogKit.info("清分处理时间====》》》"+ DateKit.dateToStr(now,DateKit.format4Login));
        List<TradeModel> list=collectionTradeByMerchant(now);
        Integer allCount=0;//总清分数量
        BigDecimal allFee=Consts.ZERO;//商户总手续费
        BigDecimal allAmount=Consts.ZERO;//总金额
        BigDecimal allBankFee=Consts.ZERO;//银行总手续费
        BigDecimal allAccountFee=Consts.ZERO;//预存账户扣除手续费金额
        BigDecimal allTradeFee=Consts.ZERO;//交易金额中扣除手续费金额
        BigDecimal allAmountOff=Consts.ZERO;//出账总金额
        BigDecimal tradeOff=Consts.ZERO;//交易金额中扣除的手续费
        BigDecimal amountFee=Consts.ZERO;//预存账户中扣除的手续费
        BigDecimal realFee=Consts.ZERO;
        CollectionClear collectionClear=null;
        CollectionCleartotle collectionCleartotle=new CollectionCleartotle();
        MerchantInfo merchantInfo=null;
        CollectionTrade collectionTrade=null;
        List<CollectionClear> collectionClears=new ArrayList<>();
        List<CollectionTrade> collectionTrades=new ArrayList<>();
        LogKit.info("开始计算清分数据");
        for(TradeModel tradeModel:list){
            collectionClear=new CollectionClear();
            allCount+=tradeModel.getTradeCount();
            allFee=allFee.add(tradeModel.getTradeFee());
            allAmount=allAmount.add(tradeModel.getTradeAmount());
            allBankFee=allBankFee.add(tradeModel.getTradeBankFee());
            collectionClear.setCat(new Date());
            collectionClear.setBankFee(tradeModel.getTradeBankFee());
            collectionClear.setMerID(tradeModel.getmId());
            collectionClear.setClearNo(System.currentTimeMillis()+"");
            collectionClear.setTradeCount(tradeModel.getTradeCount());
            collectionClear.setAmountSum(tradeModel.getTradeAmount());
            collectionClear.setAmountFeeSum(tradeModel.getTradeFee());
            collectionClear.setBankFee(tradeModel.getTradeBankFee());
            collectionClear.setChargeOff(Consts.YORN_STR.no.getVal());
            collectionClear.setClearTime(new Date());
            merchantInfo=MerchantInfo.dao.findById(tradeModel.getmId());
            //预存账户不足的情况
            if(merchantInfo.getFeeAmount().compareTo(tradeModel.getTradeFee())<=0){
                amountFee=merchantInfo.getFeeAmount();
                merchantInfo.setFeeAmount(Consts.ZERO);//商户预存账户余额
                collectionClear.setTradeFee(tradeModel.getTradeFee().subtract(amountFee));//从交易中扣除手续费金额
                merchantInfo.update();//更新商户预存手续费当前金额
                collectionClear.setAmountOff(tradeModel.getTradeAmount().subtract(tradeModel.getTradeFee()).add(amountFee));
            }else{
                merchantInfo.setFeeAmount(merchantInfo.getFeeAmount().subtract(tradeModel.getTradeFee()));
                merchantInfo.update();
                collectionClear.setAccountFee(tradeModel.getTradeFee());
                collectionClear.setTradeFee(Consts.ZERO);
                collectionClear.setAmountOff(tradeModel.getTradeAmount());
            }
            allAccountFee=allAccountFee.add(collectionClear.getAccountFee());
            allTradeFee=allTradeFee.add(collectionClear.getTradeFee());
            allAmountOff=allAmount.add(collectionClear.getAmountOff());
            collectionClears.add(collectionClear);
        }
        LogKit.info("清分数据计算完成，准备增加当日的清分汇总数据====>>>>");

        //生成清分汇总数据

        collectionCleartotle.setAccountFee(allAccountFee);
        collectionCleartotle.setAmountFeeSum(allFee);
        collectionCleartotle.setAmountOff(allAmountOff);
        collectionCleartotle.setAmountSum(allAmount);
        collectionCleartotle.setBankFee(allBankFee);
        collectionCleartotle.setCat(new Date());
        collectionCleartotle.setCleartotleTime(new Date());
        collectionCleartotle.setTradeCount(allCount);
        collectionCleartotle.setTradeFee(allTradeFee);
        collectionCleartotle.save();
        LogKit.info("准备增加当日商户清分汇总数据");

        for(CollectionClear collectionClear1 : collectionClears){
            collectionClear1.setCleartotleID(collectionCleartotle.getId());
            collectionClear1.save();
            updateTradeClearStatus(collectionClear1.getMerID(),collectionClear1.getId(),date);

        }
        LogKit.info("每日清分处理结束，一共处理了:"+collectionClears.size()+"个商户的清分数据");
    }

    /**
     * 更新清分完成的交易数据的清分状态
     * @param merchantID
     * @param ccId
     * @param date
     */
    private void updateTradeClearStatus(Integer merchantID,Integer ccId,Date date){
        String sql=Db.getSql("collection_clear.findByMerchant");
        List<CollectionTrade> collectionTrades=CollectionTrade.dao.find(sql,date,merchantID);
        for (CollectionTrade collectionTrade:collectionTrades){
            collectionTrade.setClearID(ccId);
            collectionTrade.setClearDate(new Date());
            collectionTrade.setClearStatus(Consts.YORN_STR.yes.getVal());
            collectionTrade.update();
        }
    }


    public class TradeModel{
        private String mName;
        private String mNo;
        private Integer mId;
        private Integer tradeCount;
        private BigDecimal tradeAmount;
        private BigDecimal tradeFee;

        public BigDecimal getTradeBankFee() {
            return tradeBankFee;
        }

        public void setTradeBankFee(BigDecimal tradeBankFee) {
            this.tradeBankFee = tradeBankFee;
        }

        private BigDecimal tradeBankFee;


        public Integer getTradeCount() {
            return tradeCount;
        }

        public void setTradeCount(Integer tradeCount) {
            this.tradeCount = tradeCount;
        }

        public BigDecimal getTradeAmount() {
            return tradeAmount;
        }

        public void setTradeAmount(BigDecimal tradeAmount) {
            this.tradeAmount = tradeAmount;
        }

        public BigDecimal getTradeFee() {
            return tradeFee;
        }

        public void setTradeFee(BigDecimal tradeFee) {
            this.tradeFee = tradeFee;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public String getmNo() {
            return mNo;
        }

        public void setmNo(String mNo) {
            this.mNo = mNo;
        }

        public Integer getmId() {
            return mId;
        }

        public void setmId(Integer mId) {
            this.mId = mId;
        }


        @Override
        public String toString() {
            return "TradeModel{" +
                    "mName='" + mName + '\'' +
                    ", mNo='" + mNo + '\'' +
                    ", mId=" + mId +
                    ", tradeCount=" + tradeCount +
                    ", tradeAmount=" + tradeAmount +
                    ", tradeFee=" + tradeFee +
                    ", tradeBankFee=" + tradeBankFee +
                    '}';
        }
    }
}