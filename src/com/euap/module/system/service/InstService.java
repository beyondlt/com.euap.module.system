package com.euap.module.system.service;

import com.euap.common.cache.*;
import com.euap.common.entity.ServiceBody;
import com.euap.common.utils.PinYinUtil;
import com.euap.module.system.entity.Inst;
import com.euap.module.system.mapper.InstMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Cache(name = "机构信息")

public class InstService {
    private static final Logger logger = LogManager.getLogger(InstService.class);

    @Autowired
    private InstMapper instMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ServiceBody pageQueryInst(Inst inst, RowBounds rowBounds, String order) {
        ServiceBody serviceBody = new ServiceBody();
        try {
            List<Inst> insts = new ArrayList<>();
            insts = instMapper.select(rowBounds, inst, order);
            int count = instMapper.count(inst);
            serviceBody.setResult(insts);
            serviceBody.setCount(count);
        } catch (Exception e) {
            serviceBody.setException(this.getClass(), e);
        }
        return serviceBody;
    }

    /**
     * 添加机构信息
     *
     * @param inst
     * @return
     */
    public ServiceBody addInst(Inst inst) {
        ServiceBody serviceBody = new ServiceBody();
        try {
            if (cacheService.hasKey(Const.INST, inst.getInstId())) {
                serviceBody.setSuccess(false);
                serviceBody.setMessage("机构号[" + inst.getInstId() + "]已经存在");
            } else {
                //设置添加机构的机构路径
                String parentInstId = inst.getParentInstId();
                Inst parentInst = (Inst) cacheService.get(Const.INST, parentInstId, Inst.class);
                inst.setInstPath(parentInst.getInstPath().concat("/").concat(inst.getInstId()));
                //设置进行机构模糊查询的被匹配内容
                String match = PinYinUtil.getPingYin(inst.getInstName()).concat("-").concat(inst.getInstId()).concat("-").concat(PinYinUtil.getPinYinHeadChar(inst.getInstName()));
                inst.setInstMatch(match);
                instMapper.insert(inst);
                cacheService.put(Const.INST, inst.getInstId(), inst);
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            serviceBody.setException(this.getClass(), e);
        }
        return serviceBody;
    }

    /**
     * 修改机构信息
     *
     * @param inst
     * @return
     */
    public ServiceBody editInst(Inst inst) {
        ServiceBody serviceBody = new ServiceBody();
        try {
            Inst oldInst = null;
            if (cacheService.hasKey(Const.INST, inst.getInstId())) {
                oldInst = (Inst) cacheService.get(Const.INST, inst.getInstId(), Inst.class);
                instMapper.updateByPrimaryKeySelective(inst);
                if (oldInst.getParentInstId().equals(inst.getParentInstId())) {
                    cacheService.put(Const.INST, inst.getInstId(), inst);
                }
                //机构所属机构发生改变
                else {
                    instMapper.updateInstPath(oldInst, inst.getInstPath());
                    loadInstCache();
                }
            } else {
                serviceBody.setSuccess(false);
                serviceBody.setMessage("机构数据异常，请手动刷新机构缓存");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            serviceBody.setException(this.getClass(), e);
        }
        return serviceBody;
    }


    /**
     * 删除机构信息
     *
     * @param instIds
     * @return
     */
    public ServiceBody removeInst(final String[] instIds) {
        ServiceBody serviceBody = new ServiceBody();
        //从缓存中获取机构信息，判断所要删除的机构是否有下属机构
        List<Inst> cacheInsts = cacheService.getAll(Const.INST, Inst.class);
        StringBuilder notDeleteBuilder = new StringBuilder();
        try {
            if(instIds!=null) {
                for (String id : instIds) {
                    boolean notDelete = false;
                    for (Inst cacheInst : cacheInsts) {
                        if (cacheInst.getInstPath().indexOf("/".concat(id).concat("/")) != -1) {
                            notDelete = true;
                            break;
                        }
                    }
                    if (notDelete) {
                        notDeleteBuilder.append(id).append("|");
                    }
                }
                if (notDeleteBuilder.length() > 0) {
                    serviceBody.setSuccess(false);
                    serviceBody.setMessage("以下机构含有下级机构无法删除\r\n"+notDeleteBuilder.substring(0, notDeleteBuilder.length() - 1));
                    return serviceBody;
                }
            }
            //批量删除机构信息

            String sql = "delete from base_inst where inst_id=?";
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, instIds[i]);
                }

                @Override
                public int getBatchSize() {
                    return instIds.length;
                }
            });
            cacheService.evict(Const.INST, instIds);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            serviceBody.setException(this.getClass(), e);
        }
        return serviceBody;
    }

    /**
     * 获取机构信息数据
     *
     * @return
     */
    public ServiceBody queryInst(Inst inst) {
        ServiceBody serviceBody = new ServiceBody();
        try {
            List<Inst> insts = instMapper.select(inst, "inst_path");
            serviceBody.setResult(insts);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            serviceBody.setException(this.getClass(), e);
        }
        return serviceBody;
    }

    @RefreshCache(desc = "刷新机构信息")
    public ServiceBody loadInstCache() {
        ServiceBody serviceBody = new ServiceBody();
        try {
            cacheService.clear(Const.INST);
//           Inst inst=new Inst();
//           inst.setDeleted("N");
//           inst.setEnabled("Y");
            List<Inst> insts = instMapper.select(null, "inst_Path");
            cacheService.batchPut(Const.INST, insts, new ICachePipeLinedHandler() {
                @Override
                public String getHashkey(Object object) {
                    Inst inst = (Inst) object;
                    return inst.getInstId();
                }
            });
        } catch (Exception e) {
            serviceBody.setException(this.getClass(), e);
        }
        return serviceBody;
    }


}
