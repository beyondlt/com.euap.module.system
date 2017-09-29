<div id="instTree" data-options="region:'west',width:'250'">
    <el-input placeholder="机构编号/名称/拼音/简拼" icon="search" v-model="filterParam" style="width:250px"></el-input>
    <el-tree
            ref="instTree"
            :data="queryResult"
            :props="nodeProps"
            style="height:100%;overflow-y: auto"
            :highlight-current=true
            :filter-node-method="nodeMatch"
            @node-click="nodeClick"
    >
    </el-tree>
</div>

<div id="instTable" data-options="region:'center'" class="layout">
    <el-form :inline="true" data-options="region:'north',height:'38'" style="padding-left: 15px">
        <el-button type="primary" @click="addInst">新增</el-button>
        <el-button type="primary">删除</el-button>

        <el-input placeholder="机构编号/名称/拼音/简拼" icon="search" style="width:400px"
                  v-model="queryParams.instMatch"></el-input>

        <el-button type="success">查询</el-button>

    </el-form>
    <el-table
            :data="queryResult"
            height="100%"
            style="width: 100%;"
            data-options="region:'center'"
    >
        <el-table-column
                type="selection"
                width="55">
        </el-table-column>
        <el-table-column
                type="index"
                width="100">
        </el-table-column>
        <el-table-column
                prop="instId"
                label="机构编号"
                width="180">
        </el-table-column>
        <el-table-column
                prop="instName"
                label="机构姓名"
                width="180">
            <template scope="scope">
                <el-popover trigger="hover" placement="top">
                    <p>简称: {{ scope.row.instSmpName }}</p>
                    <p>邮政编码: {{ scope.row.address }}</p>
                    <p>电话: {{ scope.row.tel }}</p>
                    <p>传真: {{ scope.row.fax }}</p>
                    <p>备注: {{ scope.row.description }}</p>
                    <div slot="reference" class="name-wrapper">
                        <el-tag>{{ scope.row.instName }}</el-tag>
                    </div>
                </el-popover>
            </template>
        </el-table-column>
        <el-table-column
                prop="parentInstId"
                label="上级机构编码">
        </el-table-column>
        <el-table-column
                prop="address"
                label="地址">
        </el-table-column>
        <el-table-column
                fixed="right"
                label="操作"
                width="150">
            <template scope="scope">
                <el-button @click="editInst(scope.row)" type="text" size="small">编辑</el-button>
                <el-button type="text" size="small">{{scope.row.enabled=='Y'?'禁用':'启用'}}</el-button>
                <el-button @click="removeInst([scope.row])" type="text" size="small">删除</el-button>
            </template>
        </el-table-column>
    </el-table>


    <el-pagination ref="page"
            :current-page="page.currentPage"
            :page-sizes="[20, 30, 40, 50, 100,200]"
            :page-size="page.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="page.count"
            data-options="region:'south',height:'35'"
            @size-change="page_sizeChange"
            @current-change="page_currentChange"
    >
    </el-pagination>
</div>
<div id="instTotal"  data-options="region:'east',width:'150'" style="padding-top: 38px">
   <div style="background-color: #a3de5d;text-align: center;cursor: pointer" >
       <p>
           <strong>有效机构数</strong>
       </p>
       <p>
           <strong>400</strong>
       </p>
   </div>
    <div style="background-color: #6cdebc;text-align: center;cursor: pointer">
        <p>
            <strong>禁用机构数</strong>
        </p>
        <p>
            <strong>10</strong>
        </p>
    </div>
    <div style="background-color:#5bc0de ;text-align: center;cursor: pointer">
        <p>
            <strong>删除机构数</strong>
        </p>
        <p>
            <strong>10</strong>
        </p>
    </div>
    <div style="background-color: #de8c83;text-align: center;cursor: pointer">
        <p>
            <strong>垃圾数据</strong>
        </p>
        <p>
            <strong>10</strong>
        </p>
    </div>
</div>

<el-dialog id="instDialog"
           :title="title"
           :visible.sync="dialogVisible"
           :before-close="handleClose"
           :show-close="false"
>

    <el-form ref="instForm" label-width="100px" style="height:300px;overflow-y: auto;padding-right: 15px" :model="instForm.data"
             :rules="instForm.rules" :show-message="true">
        <el-form-item label="机构编码" prop="instId">
            <el-input v-model="instForm.data.instId" :readonly="action=='update'" ></el-input>
        </el-form-item>
        <el-form-item label="机构名称" prop="instName">
            <el-input v-model="instForm.data.instName"></el-input>
        </el-form-item>
        <el-form-item label="机构简称" prop="instSmpName">
            <el-input v-model="instForm.data.instSmpName"></el-input>
        </el-form-item>
        <el-form-item label="上级机构" prop="parentInstId">
            <ux-picker ref="parentInst" :value="instForm.data.parentInstId" :text="instForm.data.parentInstName">
                <div slot="context" style="width: 100%;height:300px;box-shadow: 1px 1px 1px 1px rgb(236, 227, 227);">
                    <el-tree
                            :data="parentInst.instData"
                            :props="parentInst.nodeProps"
                            :expand-on-click-node="false"
                            style="height:100%;overflow-y: auto"
                            :highlight-current=true
                            @node-click="parentInst_nodeClick"
                    >
                    </el-tree>
                </div>
            </ux-picker>

        </el-form-item>
        <el-form-item label="地址">
            <el-input v-model="instForm.data.address"></el-input>
        </el-form-item>
        <el-form-item label="邮编">
            <el-input v-model="instForm.data.zip"></el-input>
        </el-form-item>
        <el-form-item label="电话">
            <el-input v-model="instForm.data.tel"></el-input>
        </el-form-item>
        <el-form-item label="传真">
            <el-input v-model="instForm.data.fax"></el-input>
        </el-form-item>
        <el-form-item label="备注">
            <el-input v-model="instForm.data.description"></el-input>
        </el-form-item>
        <el-form-item>
            <el-switch v-model="instForm.data.enabled" on-text="启用" off-text="禁用" on-value="Y" off-text="N"></el-switch>
        </el-form-item>
    </el-form>

  <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="save()">确 定</el-button>
  </span>
</el-dialog>