<template>
  <div class="login-container">
    <LoginBanner />
    <LoginForm />
  </div>
</template>

<script setup>
import LoginBanner from '@/views/login/components/LoginBanner.vue'
import LoginForm from '@/views/login/components/LoginForm.vue'
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
}
</style>