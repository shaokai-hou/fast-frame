<template>
  <div class="approval-timeline">
    <el-timeline>
      <el-timeline-item
        v-for="item in history"
        :key="item.id"
        :timestamp="formatTime(item.updateTime)"
        placement="top"
        :type="getTimelineType(item.skipType)"
        :icon="getTimelineIcon(item.skipType)"
      >
        <div class="timeline-card">
          <div class="timeline-header">
            <span class="node-name">{{ item.nodeName }}</span>
            <span
              v-if="item.targetNodeName"
              class="target-node"
            > → {{ item.targetNodeName }}</span>
          </div>
          <div class="timeline-body">
            <div class="approver">
              <span class="label">审批人:</span>
              <span class="value">{{ formatApprover(item.approver) }}</span>
            </div>
            <div
              class="result"
              :class="getResultClass(item.skipType)"
            >
              <span class="label">审批结果:</span>
              <span class="value">{{ item.skipTypeText }}</span>
            </div>
            <div
              v-if="item.message"
              class="message"
            >
              <span class="label">审批意见:</span>
              <span class="value">{{ item.message }}</span>
            </div>
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>
    <div
      v-if="!history || history.length === 0"
      class="empty-tip"
    >
      <el-empty
        description="暂无审批记录"
        :image-size="60"
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { CircleCheckFilled, CircleCloseFilled, RemoveFilled } from '@element-plus/icons-vue'

const props = defineProps({
  history: {
    type: Array,
    default: () => []
  }
})

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

function formatApprover(approver) {
  if (!approver) return '未知'
  if (approver.startsWith('role:')) {
    const roleMap = {
      'role:branch_manager': '分公司经理',
      'role:dept_manager': '部门经理',
      'role:employee': '普通员工',
      'role:admin': '管理员'
    }
    return roleMap[approver] || approver
  }
  return approver
}

function getTimelineType(skipType) {
  if (skipType === 'PASS') return 'success'
  if (skipType === 'REJECT') return 'danger'
  return 'info'
}

function getTimelineIcon(skipType) {
  if (skipType === 'PASS') return CircleCheckFilled
  if (skipType === 'REJECT') return CircleCloseFilled
  return RemoveFilled
}

function getResultClass(skipType) {
  if (skipType === 'PASS') return 'pass'
  if (skipType === 'REJECT') return 'reject'
  return 'none'
}
</script>

<style scoped lang="scss">
.approval-timeline {
  padding: 12px 0;
}

.timeline-card {
  background: var(--color-surface);
  border-radius: 8px;
  padding: 12px 16px;
  border: 1px solid var(--color-border-light);
}

.timeline-header {
  font-weight: 500;
  margin-bottom: 8px;
  color: var(--color-text);

  .node-name {
    font-size: 14px;
  }

  .target-node {
    color: var(--color-text-secondary);
    font-size: 13px;
  }
}

.timeline-body {
  font-size: 13px;

  .approver,
  .result,
  .message {
    margin-top: 4px;
  }

  .label {
    color: var(--color-text-secondary);
  }

  .value {
    margin-left: 4px;
    color: var(--color-text);
  }

  .result.pass .value {
    color: var(--el-color-success);
  }

  .result.reject .value {
    color: var(--el-color-danger);
  }
}

.empty-tip {
  padding: 20px 0;
}
</style>