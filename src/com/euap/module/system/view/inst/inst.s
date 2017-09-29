
var instDialog = new Vue({
    el: '#instDialog',
    data: {
        action: '',
        title: '',
        dialogVisible: false,
        instForm: {
            data: $.extend(true, Inst, {parentInstName: '',enabled:'Y'}),
            rules: {
                instId: [{required: true, message: '机构编码不可为空', tirgger: 'blur'}],
                instName: [{required: true, message: '机构名称不可为空', tirgger: 'blur'}],
                instSmpName: [{required: true, message: '机构简称不可为空', tirgger: 'blur'}],
                parentInstId: [{required: true, message: '上级机构不可为空', tirgger: 'blur'}]
            },
            labelPosition: 'right'
        },
        parentInst: {
            nodeProps: {
                label: 'text',
                children: 'children'
            },
            instData: []
        }


    },
    computed: {
        //title: function () {
        //    if (this.action == 'insert') {
        //        return "新增机构信息"
        //    } else if (this.action == '修改') {
        //        return "修改机构信息"
        //    }
        //}
    },
    watch: {
        'instForm.data.instName': function (value) {
            this.instForm.data.instSmpName = value

        },
        'action': function () {
            if (this.action == 'insert') {
                this.title = "新增机构信息"
            } else if (this.action == 'update') {
                this.title = "修改机构信息"
            }
        }
    },
    mounted: function () {
        var vm = this;
        $.ajax({
            type: "post",
            url: "${webapp}/queryInstTree.ajax",
            async: false,
            success: function (data) {
                vm.parentInst.instData = data['result']
            }
        });
    },
    methods: {
        save: function () {
            var url,vm=this;
            if(this.action=='insert'){
                url="${webapp}/addInst.ajax"
            }else{
                url="${webapp}/editInst.ajax"
            }
            $.ajax({
                type: "post",
                url: url,
                async: true,
                data:this.instForm.data,
                success: function (data) {
                   if(!data['success']){
                       vm.$notify.error({
                           title: '错误',
                           message: data.message
                       });
                   }else{
                        instTable.query();
                       vm.dialogVisible=false;
                   }
                }
            });
        },
        parentInst_nodeClick: function (data) {
            this.instForm.data.parentInstId = data.id;
            this.instForm.data.parentInstName = data.text
            this.$refs.parentInst.collapse();
        },
        handleClose: function () {
            return false;
        }
    }
})

var instTree = new Vue({
    el: '#instTree',
    data: {
        queryResult: [],
        filterParam: '',
        nodeProps: {
            label: 'text',
            children: 'children'
        }
    },
    watch: {
        queryParam: function (value) {
            this.filter(value);
        }
    },
    mounted: function () {
        this.query();
    },
    methods: {
        query: function () {
            var vm = this;
            $.post('${webapp}/queryInstTree.ajax', {}, function (data) {
                vm.queryResult = data['result']
            })
        },
        nodeMatch: function (value, data, node) {
            if (data['extraData']['instMatch'].indexOf(value) != -1) {
                return true;
            }
        },
        nodeClick: function (data) {
            instTable.queryParams.parentInstId = data.id;
            instTable.query();
        }
    }
})

var instTable = new Vue({
    el: '#instTable',
    data: {
        queryParams: $.extend( {order: ''},Inst),
        queryResult: [],
        page: {
            currentPage: 1,
            count: 0,
            pageSize: 20
        }
    },
    watch: {
        'queryParams.instMatch': function (value) {
            this.queryParams.parentInstId = '';
            if (value.length > 2 || value.length == 0) {
                this.query();
            }

        }
    },
    mounted: function () {
        this.query();
    },

    methods: {
        query: function () {
            var vm = this;
            var start = (this.page.currentPage - 1) * this.page.pageSize
            var limit = start + this.page.pageSize;

            $.post('${webapp}/pageQueryInst.ajax', $.extend(this.queryParams, {
                start: start,
                limit: limit
            }), function (data) {
                vm.queryResult = data['result']
                vm.page.count = data['count']
            })
        },
        addInst: function () {
            instDialog.action="insert";
            instDialog.instForm.data= $.extend({parentInstName:'',enabled:'Y'},Inst);
            instDialog.dialogVisible = true;

        },
        editInst: function (record) {
            instDialog.action = 'update';
            instDialog.instForm.data = $.extend({},record);
            instDialog.dialogVisible=true;
        },
        removeInst: function (records) {
            var instIds=new Array();
            records.forEach(function(record){
                instIds.push(record.instId);
            })
            var vm=this;
            $.ajax({
                type:'post',
                url:'${webapp}/removeInst.ajax',
                data:{
                    ids:instIds
                },
                success:function(data){
                    console.log(data)
                    if(!data['success']){
                        vm.$notify.error({
                            title: '错误',
                            message: data.message
                        });
                    }else{
                        instTable.query();

                    }
                }
            })
        },
        page_currentChange: function (currentPage) {
            this.page.currentPage = currentPage;
            this.query();
        },
        page_sizeChange: function (pageSize) {
            this.page.pageSize = pageSize;
            this.page.currentPage = 1;
            this.query();
        }
    }
})


var instTotal=new Vue({
    el:'#instTotal',
    mounted:function(){

        $("#instTotal").addClass("flipInY animated infinite")
        setTimeout(function(){
            $("#instTotal").removeClass()
        },1000)
        $("#instTotal").on('mouseover','div',function(){

            $(this).css('color',"#fff")
        })
        $("#instTotal").on('mouseout','div',function(){

            $(this).css('color',"000")
        })

    },
    methods:{

    }
})
