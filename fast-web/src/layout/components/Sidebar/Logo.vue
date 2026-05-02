<template>
  <div
    class="logo-container"
    :class="{ collapsed: appStore.sidebarCollapsed }"
  >
    <router-link
      to="/"
      class="logo-link"
    >
      <transition
        name="logo-fade"
        mode="out-in"
      >
        <div
          v-if="!appStore.sidebarCollapsed"
          key="full"
          class="logo-full"
        >
          <img
            src="@/assets/logo.svg"
            alt="logo"
            class="logo-img"
          >
          <h1 class="logo-title">
            Fast Frame
          </h1>
        </div>
        <div
          v-else
          key="mini"
          class="logo-mini"
        >
          <img
            src="@/assets/logo.svg"
            alt="logo"
            class="logo-img-mini"
          >
        </div>
      </transition>
    </router-link>
  </div>
</template>

<script setup>
import { useAppStore } from '@/store/app'

const appStore = useAppStore()
</script>

<style scoped lang="scss">
.logo-container {
  height: 60px;
  background: var(--color-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  text-decoration: none;
}

.logo-full {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.logo-img {
  width: 32px;
  height: 32px;
}

.logo-img-mini {
  width: 32px;
  height: 32px;
}

.logo-title {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: var(--color-foreground);
  white-space: nowrap;
  letter-spacing: -0.02em;
}

.logo-mini {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
}

// Logo 过渡动画
.logo-fade-enter-active,
.logo-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.logo-fade-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.logo-fade-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>