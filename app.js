/* ===== ELITE FITNESS – GLOBAL APP JS ===== */

// ── Page Loader ───────────────────────────────────────────
const PageLoader = {
  el: null,
  create() { },
  show() { },
  hide() { },
  exit(cb) { if (cb) cb(); }
};

// ── Session ──────────────────────────────────────────────
const EF = {
  getUser() { try { return JSON.parse(localStorage.getItem('ef_user') || 'null'); } catch { return null; } },
  setUser(u) { localStorage.setItem('ef_user', JSON.stringify(u)); },
  clearUser() { localStorage.removeItem('ef_user'); },
  requireAuth(role) {
    const u = this.getUser();
    if (!u) { PageLoader.exit(() => window.location.href = '/login'); return null; }
    if (role && u.role !== role) { PageLoader.exit(() => window.location.href = '/login'); return null; }
    return u;
  },
  // Always fetch latest profile from server and sync to localStorage
  async syncProfile() {
    const u = this.getUser();
    if (!u || !u.email) return u;
    try {
      const r = await fetch('/api/user/profile?email=' + encodeURIComponent(u.email));
      const p = await r.json();
      if (p && p.email) {
        if (p.goal)            u.goal            = p.goal;
        if (p.dietPreference)  u.dietPreference  = p.dietPreference;
        if (p.healthCondition) u.healthCondition = p.healthCondition;
        if (p.activityLevel)   u.activityLevel   = p.activityLevel;
        if (p.setupAnswers)    u.setupAnswers     = p.setupAnswers;
        u.setupDone = !!(p.setupAnswers && p.setupAnswers.length > 2);
        this.setUser(u);
      }
    } catch(e) {}
    return this.getUser();
  }
};

// ── Theme ─────────────────────────────────────────────────
const Theme = {
  apply(t) {
    document.documentElement.setAttribute('data-theme', t || 'dark');
    const icon = document.getElementById('themeIcon');
    if (icon) icon.className = t === 'light' ? 'fas fa-moon' : 'fas fa-sun';
  },
  toggle() {
    const cur = document.documentElement.getAttribute('data-theme') || 'dark';
    const next = cur === 'dark' ? 'light' : 'dark';
    this.apply(next);
    const u = EF.getUser();
    if (u) {
      u.theme = next; EF.setUser(u);
      fetch('/api/user/save-theme', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email: u.email, theme: next }) }).catch(() => {});
    }
  },
  init() { const u = EF.getUser(); this.apply(u?.theme || 'dark'); }
};

// ── Toast ─────────────────────────────────────────────────
const Toast = {
  container: null,
  init() {
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.className = 'toast-container';
      document.body.appendChild(this.container);
    }
  },
  show(msg, type = 'success', duration = 3500) {
    this.init();
    const icons = { success: 'fa-check', error: 'fa-times', info: 'fa-info', warning: 'fa-exclamation' };
    const t = document.createElement('div');
    t.className = `toast ${type}`;
    t.innerHTML = `<div class="toast-icon"><i class="fas ${icons[type] || 'fa-info'}"></i></div><span>${msg}</span>`;
    this.container.appendChild(t);
    setTimeout(() => { t.classList.add('removing'); setTimeout(() => t.remove(), 300); }, duration);
  }
};

// ── Sidebar ───────────────────────────────────────────────
function initSidebar() {
  const toggle = document.getElementById('menuToggle');
  const sidebar = document.getElementById('sidebar');
  const overlay = document.getElementById('sidebarOverlay');
  if (toggle && sidebar) {
    toggle.addEventListener('click', () => {
      sidebar.classList.toggle('open');
      if (overlay) overlay.style.display = sidebar.classList.contains('open') ? 'block' : 'none';
    });
  }
  if (overlay) {
    overlay.addEventListener('click', () => { sidebar.classList.remove('open'); overlay.style.display = 'none'; });
  }
  const path = window.location.pathname;
  document.querySelectorAll('.nav-item').forEach(a => { if (a.getAttribute('href') === path) a.classList.add('active'); });
}

// ── Populate sidebar user ─────────────────────────────────
function populateSidebarUser() {
  const u = EF.getUser(); if (!u) return;
  const nameEl = document.getElementById('sidebarUserName');
  const avatarEl = document.getElementById('sidebarAvatar');
  if (nameEl) nameEl.textContent = u.name || 'User';
  if (avatarEl) avatarEl.textContent = (u.name || 'U')[0].toUpperCase();
}

// ── Logout ────────────────────────────────────────────────
function doLogout() { EF.clearUser(); PageLoader.exit(() => window.location.href = '/login'); }

// ── Modal helpers ─────────────────────────────────────────
function openModal(id) { const m = document.getElementById(id); if (m) { m.classList.add('open'); document.body.style.overflow = 'hidden'; } }
function closeModal(id) { const m = document.getElementById(id); if (m) { m.classList.remove('open'); document.body.style.overflow = ''; } }
document.addEventListener('click', e => { if (e.target.classList.contains('modal-overlay')) { e.target.classList.remove('open'); document.body.style.overflow = ''; } });

// ── Page transitions ──────────────────────────────────────
function initPageTransitions() {
  document.querySelectorAll('a[href]').forEach(a => {
    const href = a.getAttribute('href');
    if (!href || href.startsWith('#') || href.startsWith('javascript') || a.target === '_blank') return;
    if (href.startsWith('/') || href.startsWith('.')) {
      a.addEventListener('click', e => { e.preventDefault(); PageLoader.exit(() => window.location.href = href); });
    }
  });
}

// ── Scroll animations ─────────────────────────────────────
function initScrollAnimations() {
  const observer = new IntersectionObserver(entries => {
    entries.forEach(e => { if (e.isIntersecting) { e.target.style.opacity = '1'; e.target.style.transform = 'translateY(0)'; } });
  }, { threshold: 0.1 });
  document.querySelectorAll('.stat-card, .plan-card, .card').forEach(el => {
    el.style.opacity = '0'; el.style.transform = 'translateY(20px)';
    el.style.transition = 'opacity .5s ease, transform .5s ease';
    observer.observe(el);
  });
}

// ── Helpers ───────────────────────────────────────────────
function setProgressRing(id, percent, color) {
  const svg = document.querySelector(`#${id} svg .ring-fill`); if (!svg) return;
  const r = parseFloat(svg.getAttribute('r')), circ = 2 * Math.PI * r;
  svg.style.strokeDasharray = circ; svg.style.strokeDashoffset = circ - (percent / 100) * circ;
  svg.style.stroke = color || 'var(--primary)';
  const val = document.querySelector(`#${id} .ring-value`); if (val) val.textContent = Math.round(percent) + '%';
}

function animateCounter(el, target, duration = 1500) {
  const step = (ts) => {
    if (!el._start) el._start = ts;
    const p = Math.min((ts - el._start) / duration, 1);
    el.textContent = Math.floor(p * target);
    if (p < 1) requestAnimationFrame(step); else el.textContent = target;
  };
  requestAnimationFrame(step);
}

// ── Init ──────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  Theme.init();
  initSidebar();
  populateSidebarUser();
  initPageTransitions();
  PageLoader.hide();
  setTimeout(initScrollAnimations, 100);
});
