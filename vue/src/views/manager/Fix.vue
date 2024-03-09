<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入报修说明查询" style="width: 200px" v-model="descr"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>


    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="studentName" label="报修人" show-overflow-tooltip></el-table-column>
        <el-table-column prop="descr" label="报修说明" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="报修时间"></el-table-column>
        <el-table-column prop="labName" label="实验室">
          <template v-slot="scope">
            {{scope.row.typeName}}-{{scope.row.labName}}
          </template>
        </el-table-column>
        <el-table-column prop="teacherName" label="实验室管理员"></el-table-column>
        <el-table-column prop="status" label="处理状态"></el-table-column>
        <el-table-column prop="fixTime" label="处理时间"></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini" v-if="user.role==='ADMIN'&& scope.row.status==='待处理'">处理</el-button>
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
    <el-dialog title="请填写检修信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username" label="检修人">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="phone" label="联系电话">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="content" label="检修说明">
          <el-input v-model="form.content" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="department" label="检修单位">
          <el-input v-model="form.department" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="time" label="检修时间">
          <el-date-picker style="width: 100%"
            v-model="form.time"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择日期时间"
          >

          </el-date-picker>
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
  name: "Fix",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      descr: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        username: [
          {required: true, message: '请输入检修人', trigger: 'blur'},
        ],
        content: [
          {required: true, message: '请输入检修信息', trigger: 'blur'},
        ],
        phone: [
          {required: true, message: '请输入联系电话', trigger: 'blur'},
        ],
        time: [
          {required: true, message: '请输入检修时间', trigger: 'blur'},
        ],
        department: [
          {required: true, message: '请输入检修单位', trigger: 'blur'},
        ],

      },
      ids: [],
      id:null,
      time:null,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    submit(){
      this.$request.post('/check/add/' ,this.form).then(res => {
        if (res.code === '200') {   // 表示操作成功
          this.$message.success('操作成功')
          this.fromVisible=false
          this.form.id=this.id
          this.form.time=this.time
          this.form.status='已处理'
          this.$request.put('/fix/update/' ,this.form).then(res => {
            if (res.code === '200') {
              this.id=null
              this.time=null
              this.load(1)
            }else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.id=row.id
      this.time=row.time
      this.form.labId=row.labId
      this.form.fixId=row.id
      this.fromVisible = true   // 打开弹窗
    },

    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/fix/delete/' + id).then(res => {
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
        this.$request.delete('/fix/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/fix/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          descr: this.descr,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.descr = null
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
