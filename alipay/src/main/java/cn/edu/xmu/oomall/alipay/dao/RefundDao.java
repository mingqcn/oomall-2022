package cn.edu.xmu.oomall.alipay.dao;

import cn.edu.xmu.oomall.alipay.mapper.AlipayRefundPoMapper;
import cn.edu.xmu.oomall.alipay.model.bo.Refund;
import cn.edu.xmu.oomall.alipay.model.po.AlipayRefundPo;
import cn.edu.xmu.oomall.alipay.model.po.AlipayRefundPoExample;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RefundDao {
    @Autowired
    private AlipayRefundPoMapper alipayRefundPoMapper;

    private Logger logger = LoggerFactory.getLogger(RefundDao.class);

    public List<AlipayRefundPo> selectRefundByOutTradeNo(String outTradeNo)
    {
        try{
            AlipayRefundPoExample alipayRefundPoExample = new AlipayRefundPoExample();
            AlipayRefundPoExample.Criteria criteria = alipayRefundPoExample.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            return alipayRefundPoMapper.selectByExample(alipayRefundPoExample);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Refund selectRefundByOutRequestNo(String outRequestNo)
    {
        try{
            AlipayRefundPoExample alipayRefundPoExample = new AlipayRefundPoExample();
            AlipayRefundPoExample.Criteria criteria = alipayRefundPoExample.createCriteria();
            criteria.andOutRequestNoEqualTo(outRequestNo);
            List<AlipayRefundPo> alipayRefundPoList= alipayRefundPoMapper.selectByExample(alipayRefundPoExample);
            if(alipayRefundPoList.size()==0)
            {
                return null;
            }
            return cloneVo(alipayRefundPoList.get(0),Refund.class);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }


    public void insertRefund(Refund refund)
    {
        try{
            AlipayRefundPo alipayRefundPo= cloneVo(refund,AlipayRefundPo.class);
            alipayRefundPoMapper.insertSelective(alipayRefundPo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }
}
