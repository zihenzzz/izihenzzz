// 粒子系统 - 基于 full-demo.html
export class ParticleSystem {
  constructor(container) {
    this.container = container;
    this.particles = [];
    this.animationId = null;
    this.isRunning = false;
  }

  init() {
    this.clearExistingParticles();
    this.createParticles();
    this.startAnimation();
  }

  clearExistingParticles() {
    if (this.container) {
      this.container.innerHTML = '';
    }
    this.particles = [];
  }

  createParticles() {
    const particleCount = 80;
    
    for (let i = 0; i < particleCount; i++) {
      const particle = this.createParticle();
      this.particles.push(particle);
      this.container.appendChild(particle);
    }
  }

  createParticle() {
    const particle = document.createElement('div');
    particle.className = 'particle';
    
    // 随机位置
    particle.style.left = Math.random() * 100 + '%';
    particle.style.top = Math.random() * 100 + '%';
    
    // 随机动画延迟和持续时间
    particle.style.animationDelay = Math.random() * 8 + 's';
    particle.style.animationDuration = (Math.random() * 4 + 4) + 's';
    
    // 随机大小
    const size = Math.random() * 4 + 2;
    particle.style.width = size + 'px';
    particle.style.height = size + 'px';
    
    // 随机透明度
    particle.style.opacity = Math.random() * 0.5 + 0.3;
    
    return particle;
  }

  startAnimation() {
    if (this.isRunning) return;
    
    this.isRunning = true;
    this.animate();
  }

  stopAnimation() {
    this.isRunning = false;
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
      this.animationId = null;
    }
  }

  animate() {
    if (!this.isRunning) return;
    
    // 可以在这里添加自定义动画逻辑
    this.animationId = requestAnimationFrame(() => this.animate());
  }

  destroy() {
    this.stopAnimation();
    this.clearExistingParticles();
  }

  // 添加新的粒子
  addParticle() {
    const particle = this.createParticle();
    this.particles.push(particle);
    this.container.appendChild(particle);
  }

  // 移除粒子
  removeParticle() {
    const particle = this.particles.pop();
    if (particle) {
      this.container.removeChild(particle);
    }
  }

  // 调整粒子数量
  setParticleCount(count) {
    const currentCount = this.particles.length;
    
    if (count > currentCount) {
      // 添加粒子
      for (let i = 0; i < count - currentCount; i++) {
        this.addParticle();
      }
    } else if (count < currentCount) {
      // 移除粒子
      for (let i = 0; i < currentCount - count; i++) {
        this.removeParticle();
      }
    }
  }
}

// 紧急退出按钮系统
export class EmergencyExit {
  constructor() {
    this.button = null;
    this.isVisible = true;
  }

  init() {
    this.createButton();
    this.attachEventListeners();
  }

  createButton() {
    this.button = document.createElement('div');
    this.button.innerHTML = '🏠';
    this.button.className = 'emergency-exit-btn';
    this.button.title = '点击返回首页 (ESC键也可以)';
    
    document.body.appendChild(this.button);
  }

  attachEventListeners() {
    // 点击事件
    this.button.addEventListener('click', () => {
      this.handleExit();
    });

    // ESC键事件
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        this.handleExit();
      }
    });
  }

  handleExit() {
    // 动画效果
    this.button.style.animation = 'pulse 0.5s ease';
    
    setTimeout(() => {
      this.button.style.animation = '';
    }, 500);

    // 尝试通过 Vue Router 退出
    if (window.router) {
      window.router.push('/');
    } else if (window.$router) {
      // Vue 2
      window.$router.push('/');
    } else {
      // 降级方案：刷新页面
      window.location.href = '/';
    }
  }

  show() {
    if (this.button && !this.isVisible) {
      this.button.style.display = 'flex';
      this.isVisible = true;
    }
  }

  hide() {
    if (this.button && this.isVisible) {
      this.button.style.display = 'none';
      this.isVisible = false;
    }
  }

  destroy() {
    if (this.button) {
      document.body.removeChild(this.button);
      this.button = null;
    }
    this.isVisible = false;
  }
}

// 页面指示器系统
export class PageIndicator {
  constructor() {
    this.currentPage = '首页';
  }

  update(pageName) {
    this.currentPage = pageName;
    
    // 移除现有指示器
    const existingIndicator = document.querySelector('.page-indicator');
    if (existingIndicator) {
      existingIndicator.remove();
    }

    // 添加新指示器
    const activePage = document.querySelector('.page.active');
    if (activePage) {
      const pageHeader = activePage.querySelector('.page-header');
      if (pageHeader) {
        const indicator = document.createElement('div');
        indicator.className = 'page-indicator';
        indicator.textContent = pageName;
        pageHeader.appendChild(indicator);
      }
    }
  }
}

// 全局增强功能初始化
export function initEnhancedFeatures() {
  // 创建粒子容器
  const particleContainer = document.createElement('div');
  particleContainer.className = 'particles';
  particleContainer.id = 'particles';
  particleContainer.style.cssText = 'pointer-events: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; z-index: -1;';
  document.body.appendChild(particleContainer);

  // 初始化粒子系统
  const particleSystem = new ParticleSystem(particleContainer);
  particleSystem.init();

  // 初始化紧急退出按钮
  const emergencyExit = new EmergencyExit();
  emergencyExit.init();

  // 初始化页面指示器
  const pageIndicator = new PageIndicator();

  // 全局暴露（供 Vue 组件使用）
  window.particleSystem = particleSystem;
  window.emergencyExit = emergencyExit;
  window.pageIndicator = pageIndicator;

  return {
    particleSystem,
    emergencyExit,
    pageIndicator
  };
}