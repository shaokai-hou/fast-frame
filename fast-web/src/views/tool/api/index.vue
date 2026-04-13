<template>
  <div class="page-container">
    <div class="content-card">
      <iframe :src="docUrl" frameborder="0" style="width: 100%; height: 800px"></iframe>
    </div>
  </div>
</template>

<script setup>
const docUrl = '/api/doc.html'
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100%;
}

.content-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
}
</style>
