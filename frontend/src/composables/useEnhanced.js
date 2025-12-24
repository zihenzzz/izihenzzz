// Vue 组合式 API - 增强功能
import { ref, onMounted, onUnmounted } from 'vue'

export function useEnhanced() {
  const rippleRef = ref(null)

  // 创建涟漪效果
  const createRipple = (event) => {
    const button = event.currentTarget
    const rect = button.getBoundingClientRect()
    const size = Math.max(rect.width, rect.height)
    const x = event.clientX - rect.left - size / 2
    const y = event.clientY - rect.top - size / 2
    
    const ripple = document.createElement('span')
    ripple.style.cssText = `
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.6);
      transform: scale(0);
      animation: ripple-animation 0.8s linear;
      pointer-events: none;
      z-index: 100;
      width: ${size}px;
      height: ${size}px;
      left: ${x}px;
      top: ${y}px;
    `
    
    button.style.position = 'relative'
    button.style.overflow = 'hidden'
    button.appendChild(ripple)
    
    setTimeout(() => {
      ripple.remove()
    }, 800)
  }

  // 添加按钮效果
  const addButtonEffects = () => {
    const buttons = document.querySelectorAll('.el-button')
    buttons.forEach(button => {
      if (!button.hasAttribute('data-enhanced')) {
        button.setAttribute('data-enhanced', 'true')
        button.addEventListener('click', createRipple)
      }
    })
  }

  // 页面切换增强
  const enhancePageTransition = () => {
    // 监听路由变化
    const observer = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
          const target = mutation.target
          if (target.classList.contains('page')) {
            if (target.classList.contains('active')) {
              target.style.animation = 'pageSlideIn 0.8s cubic-bezier(0.4, 0, 0.2, 1)'
              
              // 更新页面指示器
              setTimeout(() => {
                if (window.pageIndicator) {
                  const pageName = getPageNameFromElement(target)
                  window.pageIndicator.update(pageName)
                }
              }, 100)
            }
          }
        }
      })
    })

    // 观察页面元素
    const pages = document.querySelectorAll('.page')
    pages.forEach(page => {
      observer.observe(page, { attributes: true })
    })

    return observer
  }

  // 从元素获取页面名称
  const getPageNameFromElement = (element) => {
    const headers = element.querySelectorAll('h1, h2')
    if (headers.length > 0) {
      const title = headers[0].textContent.trim()
      const pageNames = {
        '欢迎使用火车票预订系统': '首页',
        '车次查询结果': '车次查询',
        '订单确认': '订单确认',
        '我的订单': '我的订单',
        '个人中心': '个人中心',
        '管理后台': '管理后台'
      }
      return pageNames[title] || title
    }
    return '未知页面'
  }

  // 键盘快捷键
  const setupKeyboardShortcuts = () => {
    const handleKeydown = (event) => {
      if (event.key === 'Escape') {
        if (window.emergencyExit) {
          window.emergencyExit.handleExit()
        }
      }
    }

    document.addEventListener('keydown', handleKeydown)

    return () => {
      document.removeEventListener('keydown', handleKeydown)
    }
  }

  // 增强动画观察器
  const setupAnimationObserver = () => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.style.animation = 'fadeInUp 0.8s ease-out forwards'
        }
      })
    }, {
      threshold: 0.1,
      rootMargin: '0px 0px -50px 0px'
    })

    // 观察需要动画的元素
    const animatedElements = document.querySelectorAll('.train-item, .order-item, .stat-card')
    animatedElements.forEach(element => {
      observer.observe(element)
    })

    return observer
  }

  onMounted(() => {
    // 延迟初始化，确保 DOM 渲染完成
    setTimeout(() => {
      addButtonEffects()
      const pageObserver = enhancePageTransition()
      const keyboardCleanup = setupKeyboardShortcuts()
      const animationObserver = setupAnimationObserver()

      // 清理函数
      onUnmounted(() => {
        pageObserver.disconnect()
        keyboardCleanup()
        animationObserver.disconnect()
      })
    }, 100)
  })

  return {
    rippleRef,
    createRipple,
    addButtonEffects
  }
}

// 添加 CSS 动画到全局样式
export function injectGlobalStyles() {
  if (!document.querySelector('#enhanced-animations')) {
    const style = document.createElement('style')
    style.id = 'enhanced-animations'
    style.textContent = `
      @keyframes ripple-animation {
        to {
          transform: scale(6);
          opacity: 0;
        }
      }

      @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.2); }
        100% { transform: scale(1); }
      }

      @keyframes fadeInUp {
        from { 
          opacity: 0;
          transform: translateY(30px);
        }
        to { 
          opacity: 1;
          transform: translateY(0);
        }
      }
    `
    document.head.appendChild(style)
  }
}