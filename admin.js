/* ===== ELITE FITNESS – ADMIN JS ===== */

// Toast
const AdminToast = {
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

// Theme
const AdminTheme = {
  apply(t) {
    document.documentElement.setAttribute('data-theme', t || 'dark');
    const icon = document.getElementById('adminThemeIcon');
    if (icon) icon.className = t === 'light' ? 'fas fa-moon' : 'fas fa-sun';
  },
  toggle() {
    const cur = document.documentElement.getAttribute('data-theme') || 'dark';
    const next = cur === 'dark' ? 'light' : 'dark';
    this.apply(next);
    localStorage.setItem('admin_theme', next);
  },
  init() { this.apply(localStorage.getItem('admin_theme') || 'dark'); }
};

// Modal
function openModal(id) { const m = document.getElementById(id); if (m) { m.classList.add('open'); document.body.style.overflow = 'hidden'; } }
function closeModal(id) { const m = document.getElementById(id); if (m) { m.classList.remove('open'); document.body.style.overflow = ''; } }
document.addEventListener('click', e => { if (e.target.classList.contains('modal-overlay')) { e.target.classList.remove('open'); document.body.style.overflow = ''; } });

// Sidebar
function initAdminSidebar() {
  const toggle = document.getElementById('adminMenuToggle');
  const sidebar = document.getElementById('adminSidebar');
  if (toggle && sidebar) {
    toggle.addEventListener('click', () => sidebar.classList.toggle('open'));
    document.addEventListener('click', e => {
      if (sidebar.classList.contains('open') && !sidebar.contains(e.target) && e.target !== toggle) {
        sidebar.classList.remove('open');
      }
    });
  }
  const path = window.location.pathname;
  document.querySelectorAll('.admin-nav-item').forEach(a => { if (a.getAttribute('href') === path) a.classList.add('active'); });
}

// Confirm delete
function confirmDelete(formId, msg) {
  if (confirm(msg || 'Are you sure you want to delete this?')) {
    document.getElementById(formId).submit();
  }
}

// Counter animation
function animateCounters() {
  document.querySelectorAll('[data-count]').forEach(el => {
    const target = parseInt(el.getAttribute('data-count'));
    let current = 0;
    const step = Math.ceil(target / 40);
    const timer = setInterval(() => {
      current = Math.min(current + step, target);
      el.textContent = current;
      if (current >= target) clearInterval(timer);
    }, 30);
  });
}

// Search table
function searchTable(inputId, tableId) {
  const q = document.getElementById(inputId).value.toLowerCase();
  document.querySelectorAll(`#${tableId} tbody tr`).forEach(row => {
    row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
  });
}

// Logout — direct, no loader
function adminLogout() {
  localStorage.removeItem('ef_user');
  window.location.href = '/login';
}

document.addEventListener('DOMContentLoaded', () => {
  AdminTheme.init();
  initAdminSidebar();
  setTimeout(animateCounters, 300);
});
