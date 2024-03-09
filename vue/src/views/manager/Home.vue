<template>
  <div>
    <div class="card" style="padding: 15px">
      您好，{{ user?.name }}！欢迎使用本系统
    </div>

    <div style="display: flex; margin: 10px 0">
      <div style="width: 100%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">公告列表</div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
        <div style="margin-top: 30px">
          <div id="pie" style="height: 400px"></div>
          <div id="bar"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echars from "echarts";

let pieOptions = {
  title: {
    text: '',
    subtext: '',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 0, name: '使用中' },
        { value: 0, name: '空闲中' },
      ],
    }
  ]
}


export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: []
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
    this.loadPie()
  },
  methods:{
    loadPie(){
      this.$request.get('/lab/pie').then(res =>{
        if(res.code === '200'){
          let charDom=document.getElementById('pie');
          let myChart=echars.init(charDom);
          pieOptions.title.text=res.data.text;
          pieOptions.title.subtext=res.data.subtext;
          pieOptions.series[0].name=res.data.name;
          pieOptions.series[0].data=res.data.data;
          myChart.setOption(pieOptions);
        }else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>
