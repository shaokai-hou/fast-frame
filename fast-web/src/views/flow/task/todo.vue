<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar
      :model="queryParams"
      :visible="showSearch"
      @search="handleQuery"
      @reset="resetQuery"
    >
      <el-form-item
        label="业务ID"
        prop="businessId"
      >
        <el-input
          v-model="queryParams.businessId"
          placeholder="请输入业务ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="流程名称"
        prop="flowName"
      >
        <el-input
          v-model="queryParams.flowName"
          placeholder="请输入流程名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="taskList"
        row-key="id"
      >
        <el-table-column
          type="index"
          label="序号"
          width="60"
          align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1"
        />
        <el-table-column
          label="业务ID"
          prop="businessId"
          min-width="120"
        />
        <el-table-column
          label="当前节点"
          prop="nodeName"
          min-width="150"
        />
        <el-table-column
          label="流程名称"
          prop="flowName"
          min-width="150"
        />
        <el-table-column
          label="创建时间"
          prop="createTime"
          width="180"
        />
        <el-table-column
          label="操作"
          align="center"
          width="250"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              v-hasPermi="['flow:task:approve']"
              link
              type="primary"
              @click="handleApprove(scope.row)"
            >
              审批
            </el-button>
            <el-button
              v-hasPermi="['flow:task:reject']"
              link
              type="danger"
              @click="handleReject(scope.row)"
            >
              驳回
            </el-button>
            <el-button
              v-hasPermi="['flow:task:back']"
              link
              type="warning"
              @click="handleBack(scope.row)"
            >
              退回
            </el-button>
            <el-button
              v-hasPermi="['flow:task:delegate']"
              link
              type="info"
              @click="handleDelegate(scope.row)"
            >
              委派
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </div>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveOpen"
      title="审批通过"
      width="500px"
      append-to-body
    >
      <el-form
        ref="approveFormRef"
        :model="approveForm"
        :rules="approveRules"
        label-width="80px"
      >
        <el-form-item
          label="审批意见"
          prop="message"
        >
          <el-input
            v-model="approveForm.message"
            type="textarea"
            :rows="3"
            placeholder="请输入审批意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveOpen = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="submitApprove"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 驳回对话框 -->
    <el-dialog
      v-model="rejectOpen"
      title="审批驳回"
      width="500px"
      append-to-body
    >
      <el-form
        ref="rejectFormRef"
        :model="rejectForm"
        :rules="rejectRules"
        label-width="80px"
      >
        <el-form-item
          label="驳回原因"
          prop="message"
        >
          <el-input
            v-model="rejectForm.message"
            type="textarea"
            :rows="3"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectOpen = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="submitReject"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 退回对话框 -->
    <el-dialog
      v-model="backOpen"
      title="退回上一节点"
      width="500px"
      append-to-body
    >
      <el-form
        ref="backFormRef"
        :model="backForm"
        :rules="backRules"
        label-width="80px"
      >
        <el-form-item
          label="退回原因"
          prop="message"
        >
          <el-input
            v-model="backForm.message"
            type="textarea"
            :rows="3"
            placeholder="请输入退回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="backOpen = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="submitBack"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 委派对话框 -->
    <el-dialog
      v-model="delegateOpen"
      title="委派任务"
      width="500px"
      append-to-body
    >
      <el-form
        ref="delegateFormRef"
        :model="delegateForm"
        :rules="delegateRules"
        label-width="80px"
      >
        <el-form-item
          label="委派用户"
          prop="deputeUser"
        >
          <el-input
            v-model="delegateForm.deputeUser"
            placeholder="请输入委派用户ID"
          />
        </el-form-item>
        <el-form-item
          label="委派说明"
          prop="message"
        >
          <el-input
            v-model="delegateForm.message"
            type="textarea"
            :rows="3"
            placeholder="请输入委派说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delegateOpen = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="submitDelegate"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listTodoTask, approveTask, rejectTask, backTask, delegateTask } from '@/api/flow'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const taskList = ref([])
const approveOpen = ref(false)
const rejectOpen = ref(false)
const backOpen = ref(false)
const delegateOpen = ref(false)
const approveFormRef = ref(null)
const rejectFormRef = ref(null)
const backFormRef = ref(null)
const delegateFormRef = ref(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  businessId: undefined,
  flowName: undefined
})

// 审批表单
const approveForm = reactive({
  taskId: null,
  message: ''
})

const rejectForm = reactive({
  taskId: null,
  message: ''
})

const backForm = reactive({
  taskId: null,
  message: ''
})

const delegateForm = reactive({
  taskId: null,
  deputeUser: '',
  message: ''
})

const approveRules = {
  message: [{ required: true, message: '审批意见不能为空', trigger: 'blur' }]
}

const rejectRules = {
  message: [{ required: true, message: '驳回原因不能为空', trigger: 'blur' }]
}

const backRules = {
  message: [{ required: true, message: '退回原因不能为空', trigger: 'blur' }]
}

const delegateRules = {
  deputeUser: [{ required: true, message: '委派用户不能为空', trigger: 'blur' }],
  message: [{ required: true, message: '委派说明不能为空', trigger: 'blur' }]
}

/** 查询待办任务列表 */
const getList = () => {
  loading.value = true
  listTodoTask(queryParams).then(res => {
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
  handleQuery()
}

/** 审批按钮 */
const handleApprove = (row) => {
  approveForm.taskId = row.id
  approveForm.message = ''
  approveOpen.value = true
}

/** 提交审批 */
const submitApprove = () => {
  approveFormRef.value.validate(valid => {
    if (valid) {
      approveTask(approveForm).then(() => {
        ElMessage.success('审批成功')
        approveOpen.value = false
        getList()
      })
    }
  })
}

/** 驳回按钮 */
const handleReject = (row) => {
  rejectForm.taskId = row.id
  rejectForm.message = ''
  rejectOpen.value = true
}

/** 提交驳回 */
const submitReject = () => {
  rejectFormRef.value.validate(valid => {
    if (valid) {
      rejectTask(rejectForm).then(() => {
        ElMessage.success('驳回成功')
        rejectOpen.value = false
        getList()
      })
    }
  })
}

/** 退回按钮 */
const handleBack = (row) => {
  backForm.taskId = row.id
  backForm.message = ''
  backOpen.value = true
}

/** 提交退回 */
const submitBack = () => {
  backFormRef.value.validate(valid => {
    if (valid) {
      backTask(backForm).then(() => {
        ElMessage.success('退回成功')
        backOpen.value = false
        getList()
      })
    }
  })
}

/** 委派按钮 */
const handleDelegate = (row) => {
  delegateForm.taskId = row.id
  delegateForm.deputeUser = ''
  delegateForm.message = ''
  delegateOpen.value = true
}

/** 提交委派 */
const submitDelegate = () => {
  delegateFormRef.value.validate(valid => {
    if (valid) {
      delegateTask(delegateForm).then(() => {
        ElMessage.success('委派成功')
        delegateOpen.value = false
        getList()
      })
    }
  })
}

getList()
</script>
