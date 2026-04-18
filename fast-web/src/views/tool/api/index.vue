<template>
  <PageContainer>
    <div class="content-card">
      <iframe :src="docUrl" frameborder="0" style="width: 100%; height: 800px"></iframe>
    </div>
  </PageContainer>
</template>

<script setup>
const docUrl = '/api/doc.html'
</script>
