package com.euap.module.system.entity;

import com.euap.common.annotation.ClassDesc;
import com.euap.common.annotation.PropertyDesc;
import com.euap.common.annotation.PK;
import java.io.Serializable;

/**
 * 机构信息表
 *
 * @tableName BASE_INST
 * @auther www.hygdsoft.com
 * @date 2017年09月19日
 */
 @ClassDesc(value="机构信息表")
public class Inst implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 机构ID（PK） */
    @PropertyDesc(value="机构ID")
    @PK
    private String instId;

    /** 机构名称 */
    @PropertyDesc(value="机构名称")
    private String instName;

    /** 机构简称 */
    @PropertyDesc(value="机构简称")
    private String instSmpName;

    /** 机构拼音 */
    @PropertyDesc(value="机构拼音")
    private String instPiyin;

    /** 上级ID */
    @PropertyDesc(value="上级ID")
    private String parentInstId;

    /** 机构区域编码 */
    @PropertyDesc(value="机构区域编码")
    private String instRegion;

    /** 机构路径 */
    @PropertyDesc(value="机构路径")
    private String instPath;

    /** 机构地址 */
    @PropertyDesc(value="机构地址")
    private String address;

    /** 邮政编码 */
    @PropertyDesc(value="邮政编码")
    private String zip;

    /** 电话 */
    @PropertyDesc(value="电话")
    private String tel;

    /** 传真 */
    @PropertyDesc(value="传真")
    private String fax;

    /** 备注 */
    @PropertyDesc(value="备注")
    private String description;

    /** 是否启用 */
    @PropertyDesc(value="是否启用")
    private String enabled;

    /** 是否删除 */
    @PropertyDesc(value="是否删除")
    private String deleted;

    /** 机构ID（PK） */
    public String getInstId() {
        return instId;
    }

    /** 机构ID（PK） */
    public void setInstId(String instId) {
        this.instId = instId;
    }

    /** 机构名称 */
    public String getInstName() {
        return instName;
    }

    /** 机构名称 */
    public void setInstName(String instName) {
        this.instName = instName;
    }

    /** 机构简称 */
    public String getInstSmpName() {
        return instSmpName;
    }

    /** 机构简称 */
    public void setInstSmpName(String instSmpName) {
        this.instSmpName = instSmpName;
    }

    /** 机构拼音 */
    public String getInstPiyin() {
        return instPiyin;
    }

    /** 机构拼音 */
    public void setInstPiyin(String instPiyin) {
        this.instPiyin = instPiyin;
    }

    /** 上级ID */
    public String getParentInstId() {
        return parentInstId;
    }

    /** 上级ID */
    public void setParentInstId(String parentInstId) {
        this.parentInstId = parentInstId;
    }

    /** 机构区域编码 */
    public String getInstRegion() {
        return instRegion;
    }

    /** 机构区域编码 */
    public void setInstRegion(String instRegion) {
        this.instRegion = instRegion;
    }

    /** 机构路径 */
    public String getInstPath() {
        return instPath;
    }

    /** 机构路径 */
    public void setInstPath(String instPath) {
        this.instPath = instPath;
    }

    /** 机构地址 */
    public String getAddress() {
        return address;
    }

    /** 机构地址 */
    public void setAddress(String address) {
        this.address = address;
    }

    /** 邮政编码 */
    public String getZip() {
        return zip;
    }

    /** 邮政编码 */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /** 电话 */
    public String getTel() {
        return tel;
    }

    /** 电话 */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /** 传真 */
    public String getFax() {
        return fax;
    }

    /** 传真 */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /** 备注 */
    public String getDescription() {
        return description;
    }

    /** 备注 */
    public void setDescription(String description) {
        this.description = description;
    }

    /** 是否启用 */
    public String getEnabled() {
        return enabled;
    }

    /** 是否启用 */
    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    /** 是否删除 */
    public String getDeleted() {
        return deleted;
    }

    /** 是否删除 */
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

}
