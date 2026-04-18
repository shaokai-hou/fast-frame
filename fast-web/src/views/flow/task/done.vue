<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="业务ID" prop="businessId">
        <el-input v-model="queryParams.businessId" placeholder="请输入业务ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="流程名称" prop="flowName">
        <el-input v-model="queryParams.flowName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="审批结果" prop="flowStatus">
        <el-select v-model="queryParams.flowStatus" placeholder="请选择结果" clearable>
          <el-option label="审批通过" value="2" />
          <el-option label="已退回" value="9" />
          <el-option label="自动完成" value="3" />
          <el-option label="拿回" value="11" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="taskList" row-key="id">
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="业务ID" prop="businessId" min-width="120" />
        <el-table-column label="节点名称" prop="nodeName" min-width="150" />
        <el-table-column label="流程名称" prop="flowName" min-width="150" />
        <el-table-column label="审批结果" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.flowStatus === '2'" type="success">{{ scope.row.flowStatusText }}</el-tag>
            <el-tag v-else-if="scope.row.flowStatus === '9'" type="danger">{{ scope.row.flowStatusText }}</el-tag>
            <el-tag v-else type="info">{{ scope.row.flowStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批时间" prop="approveTime" width="180" />
        <el-table-column label="审批意见" prop="message" min-width="200" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listDoneTask } from '@/api/flow'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const taskList = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  businessId: undefined,
  flowName: undefined,
  flowStatus: undefined
})

/** 查询已办任务列表 */
const getList = () => {
  loading.value = true
  listDoneTask(queryParams).then(res => {
    taskList.value = res.data
    total.value = res.data.length
    loading.value = false
  })
}

/** 搜索 */
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

/** 重置 */
const resetQuery = () => {
  queryParams.businessId = undefined
  queryParams.flowName = undefined
  queryParams.flowStatus = undefined
  handleQuery()
}

getList()
</script>
