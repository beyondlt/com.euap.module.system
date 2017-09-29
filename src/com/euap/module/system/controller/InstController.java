package com.euap.module.system.controller;

import com.euap.common.cache.CacheService;
import com.euap.common.entity.ServiceBody;
import com.euap.common.template.Vue;
import com.euap.common.tree.Tree;
import com.euap.common.tree.TreeNode;
import com.euap.module.system.Const;
import com.euap.module.system.entity.Inst;
import com.euap.module.system.service.InstService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/9/21 0021.
 */
@RestController
public class InstController {

    @Autowired
    private InstService instService;

    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/inst")
    String goInst() {
        return new Vue(Const.BASE_TEMPLATE_PATH.concat("inst/inst.t"))
                .registerPageScript(new String[]{Const.BASE_TEMPLATE_PATH.concat("inst/inst.s")})
                .registerStyle(new String[]{"library/animate/animate-3.1.0.min.css"})
                .registerEntity(new Class[]{Inst.class})
                .registerComponent(new String[]{com.euap.common.Const.GLOBAL_VUE_COMPONENT_PATH.concat("ux-combox.c")})
                .toHtml();
    }

    @RequestMapping(value = "/pageQueryInst.ajax")
    @ResponseBody
    ServiceBody pageQueryInst(Inst inst, int start, int limit, String order) {
        return instService.pageQueryInst(inst, new RowBounds(start, limit), order);
    }

    @RequestMapping(value = "/queryInstTree.ajax")
    @ResponseBody
    ServiceBody queryInstTree() {
        Inst inst = new Inst();
        inst.setEnabled("Y");
        inst.setDeleted("N");
        ServiceBody serviceBody = instService.queryInst(inst);
        Object result = serviceBody.getResult();
        if (null != result) {
            Tree tree = new Tree();
            List<Inst> insts = (List<Inst>) result;
            for (Inst tmp : insts) {
                TreeNode node = new TreeNode(tmp.getInstId(), tmp.getInstName().concat("-").concat(tmp.getInstId()), false, null, null);
                node.setExtraData(tmp);
                tree.addNode(node, tmp.getParentInstId());
            }
            serviceBody.setResult(tree);
        }

        return serviceBody;
    }

    @RequestMapping(value = "/addInst.ajax")
    @ResponseBody
    ServiceBody addInst(Inst inst) {
        inst.setDeleted("N");
        ServiceBody serviceBody = instService.addInst(inst);
        return serviceBody;
    }

    @RequestMapping(value = "/editInst.ajax")
    @ResponseBody
    ServiceBody editInst(Inst inst) {
        return instService.editInst(inst);
    }

    @RequestMapping(value = "/removeInst.ajax")
    @ResponseBody
    ServiceBody removeInst(@RequestParam("ids[]") String[] instIds) {
        return instService.removeInst(instIds);
    }
}
