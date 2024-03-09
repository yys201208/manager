<template>
  <div>
    <div class="search">
      <el-select v-model="status" placeholder="请选择审核状态查询" style="width: 200px">
        <el-option label="待审核" value="待审核"> </el-option>
        <el-option label="审核通过" value="审核通过"> </el-option>
        <el-option label="审核不通过" value="审核不通过"> </el-option>
        <el-option label="已结束" value="已结束"> </el-option>

      </el-select>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>


    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="labName" label="实验室" show-overflow-tooltip></el-table-column>
        <el-table-column prop="teacherName" label="实验室管理员" show-overflow-tooltip></el-table-column>
        <el-table-column prop="studentName" label="预约人"></el-table-column>
        <el-table-column prop="time" label="操作时间"></el-table-column>
        <el-table-column prop="start" label="开放时间">
          <template v-slot="scope">
            {{scope.row.start}} ~ {{scope.row.end}}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="预约状态"></el-table-column>
        <el-table-column prop="dostatus" label="使用状态"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="warning" size="mini" @click="del(scope.row.id)" v-if="user.role==='STUDENT' && scope.row.status==='待审核'">取消预约</el-button>
            <el-button plain type="warning" size="mini" @click="changStatus(scope.row,'审核通过')" v-if="user.role!=='STUDENT' && scope.row.status==='待审核'">通过</el-button>
            <el-button plain type="warning" size="mini" @click="changStatus(scope.row,'审核不通过')" v-if="user.role!=='STUDENT' && scope.row.status==='待审核'">不通过</el-button>
            <el-button plain type="warning" size="mini" @click="changStatus(scope.row,'已结束')" v-if="scope.row.dostatus==='使用中'">结束使用</el-button>
            <el-button plain type="warning" size="mini" @click="initDialog(scope.row)" v-if="user.role==='STUDENT' && scope.row.dostatus==='已结束'">我要报修</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>


    <el-dialog title="报修反馈信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">

        <el-form-item prop="descr" label="报修说明">
          <el-input type="textarea" :rows="5" v-model="form.descr" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
export default {
  name: "Reserve",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      status: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        descr: [
          {required: true, message: '请输入报修说明', trigger: 'blur'},
        ],
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    initDialog(row){
      this.form=JSON.parse(JSON.stringify(row))
      this.fromVisible=true
    },
    submit(){
      let data = {
        descr:this.form.descr,
        studentId: this.user.id,
        labId:this.form.labId,
        teacherId:this.form.teacherId,
        status:'待处理'
      }
      this.$request.post('/fix/add',data).then(res=>{
        if (res.code==='200'){
          this.$message.success('报修成功')
          this.fromVisible=false
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    changStatus(row,status){
      let date = JSON.parse(JSON.stringify(row))
      if ('审核通过'===status){
        date.dostatus='使用中'
        date.status=status
      }
      if ('审核不通过'===status){
        date.dostatus=status
        date.status=status
      }
      if ('已结束'===status){
        date.dostatus='已结束'
      }
      date.status = status
      this.$request.put('/reserve/update',date).then(res=>{
        if (res.code==='200'){
          this.$message.success('操作成功')
          this.load(1)
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/reserve/update' : '/reserve/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定取消预约吗？', '确认取消', {type: "warning"}).then(response => {
        this.$request.delete('/reserve/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/reserve/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/reserve/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: this.status,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.status = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
