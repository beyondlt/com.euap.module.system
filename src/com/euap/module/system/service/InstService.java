package com.euap.module.system.service;

import com.euap.common.cache.*;
import com.euap.common.entity.ServiceBody;
import com.euap.module.system.entity.Inst;
import com.euap.module.system.mapper.InstMapper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
@Transactional(rollbackFor=Exception.class)
@Cache(name="机构信息")
public class InstService {
    private static final Logger logger = LogManager.getLogger(InstService.class);

    @Autowired
    private InstMapper instMapper;

    @Autowired
    private CacheService cacheService;

    public ServiceBody pageQueryInsts(Inst inst, RowBounds rowBounds,String order){
        ServiceBody serviceBody= new ServiceBody();
       try{
           List<Inst> insts = instMapper.select(rowBounds, inst, order);
           int count=instMapper.count(inst);
           serviceBody.setResult(insts);
           serviceBody.setCount(count);
       }catch (Exception e){
           String error=ExceptionUtils.getMessage(e);
           serviceBody.setException(error);
           logger.error(error);
       }
        return serviceBody;
    }

    public ServiceBody addInst(Inst inst){
        ServiceBody serviceBody=new ServiceBody();
        try{
            if(cacheService.hasKey(Const.INST,inst.getInstId())){
                serviceBody.setSuccess(false);
                serviceBody.setMessage("机构号["+inst.getInstId()+"]已经存在");
            }else{
                instMapper.insert(inst);
                cacheService.put(Const.INST,inst.getInstId(),inst);
            }
        }catch (Exception e){
            String error=ExceptionUtils.getMessage(e);
            serviceBody.setException(error);
            logger.error(error);
        }
        return serviceBody;
    }
    @RefreshCache(desc = "刷新机构信息")
    @RequestMapping
    public ServiceBody loadInstCache(){
        ServiceBody serviceBody=new ServiceBody();
        cacheService.clear(Const.INST);
        List<Inst> insts=instMapper.select(null,null);
       cacheService.batchPut(Const.INST, insts, new ICachePipeLinedHandler() {
           @Override
           public String getHashkey(Object object) {
               Inst inst= (Inst) object;
               return inst.getInstId();
           }
       });
        return serviceBody;
    }


}
