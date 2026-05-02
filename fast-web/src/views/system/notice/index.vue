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
        label="公告标题"
        prop="noticeTitle"
      >
        <el-input
          v-model="queryParams.noticeTitle"
          placeholder="请输入公告标题"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="公告类型"
        prop="noticeType"
      >
        <el-select
          v-model="queryParams.noticeType"
          placeholder="全部"
          clearable
        >
          <el-option
            label="通知"
            value="1"
          />
          <el-option
            label="公告"
            value="2"
          />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button
          v-hasPermi="['system:notice:add']"
          type="primary"
          plain
          :icon="Plus"
          @click="handleAdd"
        >
          新增
        </el-button>
        <el-button
          v-hasPermi="['system:notice:delete']"
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >
          删除
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="noticeList"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
        />
        <el-table-column
          type="index"
          label="序号"
          width="60"
          align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1"
        />
        <el-table-column
          label="公告标题"
          prop="noticeTitle"
          show-overflow-tooltip
        />
        <el-table-column
          label="公告类型"
          prop="noticeType"
          width="100"
        >
          <template #default="scope">
            <el-tag
              v-if="scope.row.noticeType === '1'"
              type="success"
            >
              通知
            </el-tag>
            <el-tag
              v-else
              type="info"
            >
              公告
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          prop="status"
          width="80"
        >
          <template #default="scope">
            <el-tag
              v-if="scope.row.status === '0'"
              type="success"
            >
              正常
            </el-tag>
            <el-tag
              v-else
              type="warning"
            >
              关闭
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="创建时间"
          prop="createTime"
          width="180"
        />
        <el-table-column
          label="操作"
          align="center"
          width="240"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              link
              type="primary"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-hasPermi="['system:notice:edit']"
              link
              type="primary"
              @click="handleUpdate(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPermi="['system:notice:delete']"
              link
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
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

    <!-- 新增/修改对话框 -->
    <el-dialog
      v-model="open"
      :title="title"
      width="800px"
      append-to-body
    >
      <el-form
        ref="noticeFormRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item
          label="公告标题"
          prop="noticeTitle"
        >
          <el-input
            v-model="form.noticeTitle"
            placeholder="请输入公告标题"
          />
        </el-form-item>
        <el-form-item
          label="公告类型"
          prop="noticeType"
        >
          <el-select
            v-model="form.noticeType"
            placeholder="请选择公告类型"
          >
            <el-option
              label="通知"
              value="1"
            />
            <el-option
              label="公告"
              value="2"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="状态"
          prop="status"
        >
          <el-radio-group v-model="form.status">
            <el-radio value="0">
              正常
            </el-radio>
            <el-radio value="1">
              关闭
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="公告内容"
          prop="noticeContent"
        >
          <RichTextEditor
            v-model="form.noticeContent"
            placeholder="请输入公告内容"
            height="300px"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="submitForm"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看公告对话框 -->
    <el-dialog
      v-model="viewOpen"
      title="查看公告"
      width="700px"
      append-to-body
    >
      <el-descriptions
        :column="1"
        border
      >
        <el-descriptions-item label="公告标题">
          {{ viewData.noticeTitle }}
        </el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag
            v-if="viewData.noticeType === '1'"
            type="success"
          >
            通知
          </el-tag>
          <el-tag
            v-else
            type="info"
          >
            公告
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            v-if="viewData.status === '0'"
            type="success"
          >
            正常
          </el-tag>
          <el-tag
            v-else
            type="warning"
          >
            关闭
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ viewData.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="公告内容">
          <RichTextViewer :content="viewData.noticeContent" />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue'
import { listNotice, addNotice, updateNotice, deleteNotice } from '@/api/system/notice'
import RichTextEditor from '@/components/RichTextEditor/index.vue'
import RichTextViewer from '@/components/RichTextViewer/index.vue'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const noticeList = ref([])
const title = ref('')
const open = ref(false)
const ids = ref([])
const viewOpen = ref(false)
const viewData = ref({})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  noticeTitle: undefined,
  noticeType: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  noticeTitle: [{ required: true, message: '公告标题不能为空', trigger: 'blur' }],
  noticeType: [{ required: true, message: '公告类型不能为空', trigger: 'change' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listNotice(queryParams)
    noticeList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.noticeTitle = undefined
  queryParams.noticeType = undefined
  queryParams.status = undefined
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  ids.value = selection.map((item) => item.id)
  multiple.value = !selection.length
}

// 新增
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '新增公告'
}

// 查看
const handleView = (row) => {
  viewData.value = { ...row }
  viewOpen.value = true
}

// 修改
const handleUpdate = async (row) => {
  reset()
  form.value = { ...row }
  open.value = true
  title.value = '修改公告'
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的公告?', '警告', { type: 'warning' })
  await deleteNotice(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.noticeFormRef.validate()
  if (form.value.id) {
    await updateNotice(form.value)
    ElMessage.success('修改成功')
  } else {
    await addNotice(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    noticeTitle: undefined,
    noticeType: '1',
    noticeContent: undefined,
    status: '0'
  }
}

onMounted(() => {
  getList()
})
</script>

