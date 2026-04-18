<template>
  <PageContainer>
    <div class="content-card">
      <iframe :src="druidUrl" style="width: 100%; height: 800px"></iframe>
    </div>
  </PageContainer>
</template>

<script setup>
const druidUrl = '/druid/index.html'
</script>
