<template>
  <div class="map-container">
    <div class="map-header">
      <h2>互花米草扩散区域预测地图</h2>
    </div>

    <div class="map-content">
      <!-- 左侧控制面板 -->
      <div class="control-panel">
        <div class="panel-section">
          <h3>区域</h3>
          <el-select v-model="selectedRegion" placeholder="请选择地区">
            <el-option
                v-for="region in regions"
                :key="region.value"
                :label="region.label"
                :value="region.value"
            />
          </el-select>
        </div>

        <div class="panel-section time-section">
          <h3>时间范围</h3>
          <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
          />

          <el-button
              type="primary"
              @click="handlePrediction"
              class="predict-btn"
              >
            智能预测
          </el-button>
        </div>
      </div>

      <!-- 地图区域 -->
      <div class="map-wrapper">
        <div id="china-map" ref="mapRef"></div>
      </div>

      <!-- 图例 -->
      <div class="map-legend">
        <h4>图例</h4>
        <div v-for="(item, index) in legendItems" :key="index" class="legend-item">
          <div class="legend-color" :style="{backgroundColor: item.color}"></div>
          <span>{{ item.range }}</span>
        </div>
        <div class="scale">比例尺 1:20000000</div>
        <div class="credit">由国家气候中心提供</div>
        <div class="approval">审图号GS(2019)1768号</div>
      </div>
    </div>

    <!-- 预测结果弹窗 -->
    <el-dialog v-model="showResult" title="预测结果" width="50%">
      <div class="prediction-result">
        <p>预测结果：（示例数据，该功能暂未开放）</p>
        <p class="result-content">互花米草原产于美洲的大西洋海岸，在欧洲、亚洲、美洲、非洲和大洋洲均有分布。由于在促淤护滩上具有较高的效率，互花米草最早在1979年从北美人工引种至中国海岸带，截至2020年，已广泛分布于河北、天津、山东、江苏、上海、浙江、福建、广东、广西和海南等沿海各省(直辖市、自治区)。</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import chinaJson from './china.json' // 中国地图GeoJSON数据
import { ElMessage } from 'element-plus';
import { Edit, Search } from '@element-plus/icons-vue'

// 地图实例
const mapRef = ref(null)
let chartInstance = null

// 数据
const selectedRegion = ref('')
const dateRange = ref([])
const searchText = ref('')
const showResult = ref(false)
const isDrawing = ref(false)

// 模拟地区数据
const regions = ref([
  { value: 'a', label: '渤海湾区域' },
  { value: 'b', label: '黄海区域' },
  { value: 'c', label: '东海区域' },
  { value: 'd', label: '南海区域' }
])

// 图例数据
const legendItems = ref([
  { color: '#ff0000', range: '300~600' },
  { color: '#ff9900', range: '300~5000' },
  { color: '#ffff00', range: '100~5000' },
  { color: '#00ff00', range: '100~500' }
])

// 初始化地图
const initMap = () => {
  if (!mapRef.value) return

  // 注册中国地图
  echarts.registerMap('china', chinaJson)

  chartInstance = echarts.init(mapRef.value)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}'
    },
    visualMap: {
      min: 0,
      max: 5000,
      text: ['高', '低'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['#50a3ba', '#eac736', '#d94e5d']
      }
    },
    series: [
      {
        name: '中国',
        type: 'map',
        map: 'china',
        roam: true, // 允许缩放和平移
        emphasis: {
          label: {
            show: true
          }
        },
        data: [
          { name: '北京', value: 1000 },
          { name: '上海', value: 2000 },
          { name: '广东', value: 3000 },
          // 其他省份数据...
        ]
      }
    ]
  }

  chartInstance.setOption(option)

  // 添加地图事件监听
  chartInstance.on('click', (params) => {
    console.log('点击了:', params.name)
  })
}

// 处理预测请求
const handlePrediction = () => {
  if (!selectedRegion.value && !dateRange.value.length) {
    ElMessage.warning('请选择区域和时间范围')
    return
  }

  // 模拟API请求
  setTimeout(() => {
    showResult.value = true
  }, 500)
}

// 生命周期
onMounted(() => {
  initMap()
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', () => {
    chartInstance?.resize()
  })
  chartInstance?.dispose()
})



</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
  background: white;
  display: flex;
  flex-direction: column;
}

.map-header {
  padding: 10px 20px;
  border-bottom: 1px solid #eee;
}

.map-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.control-panel {
  width: 300px;
  padding: 20px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.panel-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.map-wrapper {
  flex: 1;
  position: relative;
}

#china-map {
  width: 100%;
  height: 100%;
}

.map-legend {
  width: 200px;
  padding: 20px;
  border-left: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.predict-btn {
  margin-top: 20px;
  width: 100%;
}

.prediction-result {
  padding: 20px;
}

.result-content {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-top: 10px;
}

.scale, .credit, .approval {
  font-size: 12px;
  color: #999;
}
/* 时间部分专用样式 */
.time-section {
  background-color: #f8fafc;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.time-section h3 {
  color: #409eff;
  margin-bottom: 12px;
  font-size: 14px;
}

.date-range-container {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.custom-date-picker {
  width: 100%;
}

.custom-date-picker .el-range-separator {
  color: #606266;
  font-size: 13px;
}

.predict-btn {
  margin-top: 0;
  width: 100%;
  height: 36px;
  font-size: 14px;
}

.predict-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 区域部分也做相应调整保持统一 */
.panel-section:not(.time-section) {
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  margin-bottom: 15px;
}

.panel-section h3 {
  color: #409eff;
  margin-bottom: 12px;
  font-size: 14px;
}
</style>