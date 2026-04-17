<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
        <el-form-item label="业务ID" prop="businessId">
          <el-input v-model="queryParams.businessId" placeholder="请输入业务ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="流程状态" prop="flowStatus">
          <el-select v-model="queryParams.flowStatus" placeholder="请选择状态" clearable>
            <el-option label="审批中" value="1" />
            <el-option label="审批通过" value="2" />
            <el-option label="已完成" value="8" />
            <el-option label="已退回" value="9" />
            <el-option label="已终止" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleStart">发起流程</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="instanceList" row-key="id">
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => index + 1" />
        <el-table-column label="业务ID" prop="businessId" min-width="120" />
        <el-table-column label="当前节点" prop="nodeName" min-width="150" />
        <el-table-column label="流程状态" prop="flowStatusText" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.flowStatus === '1'" type="primary">{{ scope.row.flowStatusText }}</el-tag>
            <el-tag v-else-if="scope.row.flowStatus === '2' || scope.row.flowStatus === '8'" type="success">{{
              scope.row.flowStatusText }}</el-tag>
            <el-tag v-else-if="scope.row.flowStatus === '9'" type="danger">{{ scope.row.flowStatusText }}</el-tag>
            <el-tag v-else type="info">{{ scope.row.flowStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建人" prop="createBy" width="100" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="结束时间" prop="endTime" width="180" />
        <el-table-column label="操作" align="center" width="260" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleDiagram(scope.row)">流程图</el-button>
            <el-button link type="primary" @click="handleHistory(scope.row)">审批历史</el-button>
            <el-button link type="danger" @click="handleTerminate(scope.row)"
              v-if="scope.row.flowStatus !== '2' && scope.row.flowStatus !== '4' && scope.row.flowStatus !== '8'"
              v-hasPermi="['flow:instance:terminate']">终止</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 发起流程对话框 -->
    <el-dialog title="发起流程" v-model="startOpen" width="500px" append-to-body>
      <el-form ref="startFormRef" :model="startForm" :rules="startRules" label-width="80px">
        <el-form-item label="流程名称" prop="flowCode">
          <el-select v-model="startForm.flowCode" placeholder="请选择流程" style="width: 100%">
            <el-option v-for="item in flowDefOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务ID" prop="businessId">
          <el-input v-model="startForm.businessId" placeholder="请输入业务ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startOpen = false">取消</el-button>
        <el-button type="primary" @click="submitStart">确定</el-button>
      </template>
    </el-dialog>

    <!-- 流程图对话框 -->
    <el-dialog title="流程图" v-model="diagramOpen" width="900px" append-to-body destroy-on-close>
      <div class="diagram-container">
        <iframe :src="diagramUrl" style="width: 100%; height: 100%; border: none;" />
      </div>
    </el-dialog>

    <!-- 审批历史对话框 -->
    <el-dialog title="审批历史" v-model="historyOpen" width="700px" append-to-body destroy-on-close>
      <div class="history-container" v-loading="historyLoading">
        <ApprovalTimeline :history="historyList" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { listFlowInstance, startFlowInstance, terminateFlowInstance, listFlowDef, getApprovalHistory } from '@/api/flow'
import { getToken } from '@/utils/auth'
import ApprovalTimeline from '@/components/ApprovalTimeline.vue'

const { proxy } = getCurrentInstance()

const data = reactive({
  loading: false,
  showSearch: true,
  total: 0,
  instanceList: [],
  startOpen: false,
  diagramOpen: false,
  diagramUrl: '',
  historyOpen: false,
  historyList: [],
  historyLoading: false,
  flowDefOptions: [],
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    businessId: undefined,
    flowStatus: undefined
  },
  startForm: {
    flowCode: '',
    businessId: ''
  },
  startRules: {
    flowCode: [{ required: true, message: '流程编码不能为空', trigger: 'change' }],
    businessId: [{ required: true, message: '业务ID不能为空', trigger: 'blur' }]
  }
})

const { loading, showSearch, total, instanceList, startOpen, diagramOpen, diagramUrl, historyOpen, historyList, historyLoading, flowDefOptions, queryParams, startForm, startRules } = toRefs(data)
const queryFormRef = ref(null)
const startFormRef = ref(null)

/** 查询流程实例列表 */
function getList() {
  data.loading = true
  listFlowInstance(data.queryParams).then(res => {
    data.instanceList = res.data
    data.total = res.data.length
    data.loading = false
  })
}

/** 搜索按钮 */
function handleQuery() {
  data.queryParams.pageNum = 1
  getList()
}

/** 重置按钮 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 查询已发布的流程定义列表 */
function getFlowDefList() {
  listFlowDef({ isPublish: 1 }).then(res => {
    data.flowDefOptions = res.data.map(item => ({
      value: item.flowCode,
      label: item.flowName
    }))
  })
}

/** 发起流程按钮 */
function handleStart() {
  data.startForm = {
    flowCode: '',
    businessId: ''
  }
  getFlowDefList()
  data.startOpen = true
}

/** 提交发起流程 */
function submitStart() {
  startFormRef.value.validate(valid => {
    if (valid) {
      startFlowInstance(data.startForm).then(res => {
        ElMessage.success('发起成功，实例ID: ' + res.data)
        data.startOpen = false
        getList()
      })
    }
  })
}

/** 查看流程图 */
function handleDiagram(row) {
  // 使用 Warm-Flow 的流程图 UI，type=FlowChart 表示查看流程图模式
  // token-name 配置为 sa-token，所以参数名是 sa-token
  data.diagramUrl = `/api/warm-flow-ui/index.html?id=${row.id}&type=FlowChart&sa-token=${getToken()}&t=${Date.now()}`
  data.diagramOpen = true
}

/** 终止流程 */
function handleTerminate(row) {
  ElMessageBox.confirm('是否确认终止该流程实例?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    terminateFlowInstance(row.id).then(() => {
      ElMessage.success('终止成功')
      getList()
    })
  })
}

/** 查看审批历史 */
function handleHistory(row) {
  data.historyLoading = true
  data.historyOpen = true
  getApprovalHistory(row.id).then(res => {
    data.historyList = res.data
    data.historyLoading = false
  }).catch(() => {
    data.historyLoading = false
  })
}

getList()
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100%;
}

.content-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
}

.tool-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.search-bar {
  margin-bottom: 16px;
}

.diagram-container {
  height: 500px;
}

.history-container {
  max-height: 500px;
  overflow-y: auto;
}
</style>