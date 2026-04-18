<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="流程编码" prop="flowCode">
        <el-input v-model="queryParams.flowCode" placeholder="请输入流程编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="流程名称" prop="flowName">
        <el-input v-model="queryParams.flowName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="发布状态" prop="isPublish">
        <el-select v-model="queryParams.isPublish" placeholder="发布状态" clearable>
          <el-option label="未发布" :value="0" />
          <el-option label="已发布" :value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleDesigner">设计流程</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="defList" row-key="id">
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => index + 1" />
        <el-table-column label="流程编码" prop="flowCode" min-width="120" />
        <el-table-column label="流程名称" prop="flowName" min-width="150" />
        <el-table-column label="版本" prop="version" width="120" align="center" />
        <el-table-column label="发布状态" align="center" width="160">
          <template #default="scope">
            <el-switch v-model="scope.row.isPublish" :active-value="1" :inactive-value="0"
              @change="handlePublishChange(scope.row)" v-hasPermi="['flow:def:publish']" />
          </template>
        </el-table-column>
        <el-table-column label="激活状态" align="center" width="160">
          <template #default="scope">
            <el-tag v-if="scope.row.activityStatus === 1" type="success">激活</el-tag>
            <el-tag v-else type="warning">挂起</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleDesign(scope.row)">设计</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)"
              v-hasPermi="['flow:def:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 详情对话框 -->
    <el-dialog title="流程定义详情" v-model="detailOpen" width="500px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程编码">{{ detailData.flowCode }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ detailData.flowName }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ detailData.version }}</el-descriptions-item>
        <el-descriptions-item label="发布状态">
          <el-tag v-if="detailData.isPublish === 1" type="success">已发布</el-tag>
          <el-tag v-else type="info">未发布</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="激活状态">
          <el-tag v-if="detailData.activityStatus === 1" type="success">激活</el-tag>
          <el-tag v-else type="warning">挂起</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { listFlowDef, getFlowDef, publishFlowDef, unpublishFlowDef, deleteFlowDef } from '@/api/flow'

const { proxy } = getCurrentInstance()

const data = reactive({
  loading: false,
  showSearch: true,
  total: 0,
  defList: [],
  detailOpen: false,
  detailData: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    flowCode: '',
    flowName: '',
    isPublish: null
  }
})

const { loading, showSearch, total, defList, detailOpen, detailData, queryParams } = toRefs(data)

/** 查询流程定义列表 */
function getList() {
  data.loading = true
  listFlowDef(data.queryParams).then(res => {
    data.defList = res.data
    data.total = res.data.length
    data.loading = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  data.queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryFormRef')
  handleQuery()
}

/** 打开流程设计器 */
function handleDesigner() {
  proxy.$router.push('/flow/designer')
}

/** 查看详情 */
function handleView(row) {
  getFlowDef(row.id).then(res => {
    data.detailData = res.data
    data.detailOpen = true
  })
}

/** 设计流程 */
function handleDesign(row) {
  proxy.$router.push(`/flow/designer?id=${row.id}`)
}

/** 发布状态切换 */
function handlePublishChange(row) {
  const text = row.isPublish === 1 ? "发布" : "取消发布"
  ElMessageBox.confirm(`是否确认${text}流程"${row.flowName}"?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (row.isPublish === 1) {
      publishFlowDef(row.id).then(() => {
        ElMessage.success('发布成功')
      })
    } else {
      unpublishFlowDef(row.id).then(() => {
        ElMessage.success('取消发布成功')
      })
    }
  }).catch(() => {
    row.isPublish = row.isPublish === 1 ? 0 : 1
  })
}

/** 删除流程 */
function handleDelete(row) {
  ElMessageBox.confirm('是否确认删除流程"' + row.flowName + '"?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteFlowDef(row.id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

getList()
</script>

