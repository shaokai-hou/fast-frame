<template>
  <div
    class="rich-text-editor"
    :class="{ 'is-disabled': disabled }"
  >
    <Toolbar
      :editor="editorRef"
      :default-config="toolbarConfig"
      :mode="mode"
      class="toolbar"
    />
    <Editor
      v-model="valueHtml"
      :default-config="editorConfig"
      :mode="mode"
      class="editor"
      @on-created="handleCreated"
      @on-change="handleChange"
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import { ref, shallowRef, onBeforeUnmount, computed, watch } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadFile } from '@/api/system/file'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  height: {
    type: String,
    default: '400px'
  },
  mode: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['update:modelValue'])

const editorRef = shallowRef()

const valueHtml = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const toolbarConfig = {
  excludeKeys: []
}

const editorConfig = computed(() => ({
  placeholder: props.placeholder,
  readOnly: props.disabled,
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const res = await uploadFile(file)
          const url = `/system/file/download/${res.data.fileName}`
          insertFn(url, res.data.originalFilename, url)
        } catch (err) {
          ElMessage.error('图片上传失败')
        }
      },
      maxFileSize: 5 * 1024 * 1024,
      allowedFileTypes: ['image/*']
    }
  }
}))

const handleCreated = (editor) => {
  editorRef.value = editor
}

const handleChange = (editor) => {
  emit('update:modelValue', editor.getHtml())
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
</script>

<style scoped lang="scss">
.rich-text-editor {
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;

  .toolbar {
    border-bottom: 1px solid var(--el-border-color);
  }

  .editor {
    height: 250px !important;
    overflow-y: auto;
  }

  :deep(.w-e-text-container) {
    height: 250px !important;
  }

  &.is-disabled {
    .toolbar {
      pointer-events: none;
      opacity: 0.5;
    }
  }
}
</style>