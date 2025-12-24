<template>
  <el-config-provider :locale="locale">
    <div id="app" class="page-container">
      <router-view />
    </div>
  </el-config-provider>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { useEnhanced, injectGlobalStyles } from '@/composables/useEnhanced'

const locale = ref(zhCn)

onMounted(() => {
  // 注入全局增强样式
  injectGlobalStyles()
  
  // 使用增强功能
  const { addButtonEffects } = useEnhanced()
  
  // 延迟添加按钮效果
  setTimeout(() => {
    addButtonEffects()
  }, 200)
})
</script>

<style lang="scss">
#app {
  width: 100%;
  min-height: 100vh;
  position: relative;
  z-index: 0;
}

// 确保所有页面都在正确的层级
.page-container {
  position: relative;
  z-index: 1;
}

// 粒子背景
.particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite;
  z-index: 0;
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg); 
    opacity: 0.3; 
  }
  50% { 
    transform: translateY(-30px) rotate(180deg); 
    opacity: 0.8; 
  }
}
</style>
