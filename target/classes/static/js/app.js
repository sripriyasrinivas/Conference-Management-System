/* =============================================
   Conference Management System — app.js
   ============================================= */

document.addEventListener('DOMContentLoaded', () => {
  initAlertDismiss();
  initFileUpload();
  initStarRatings();
  initConfirmDialogs();
  initTabFilter();
  highlightActiveNav();
});

/* ── Auto-dismiss alerts after 5 seconds ──── */
function initAlertDismiss() {
  document.querySelectorAll('.alert').forEach(alert => {
    setTimeout(() => {
      alert.style.transition = 'opacity 0.5s';
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 500);
    }, 5000);
  });
}

/* ── File upload preview ─────────────────── */
function initFileUpload() {
  const fileInput = document.getElementById('fileInput');
  if (!fileInput) return;

  const label = fileInput.nextElementSibling;
  if (!label) return;

  fileInput.addEventListener('change', e => {
    const file = e.target.files[0];
    if (file) {
      label.innerHTML = `
        <i class="fas fa-file-pdf" style="color:#ef4444"></i>
        <span>${file.name}</span>
        <small>${(file.size / 1024 / 1024).toFixed(2)} MB</small>
      `;
    }
  });

  // Drag & drop support
  const area = fileInput.closest('.file-upload-area');
  if (area) {
    area.addEventListener('dragover', e => {
      e.preventDefault();
      area.style.borderColor = 'var(--primary)';
      area.style.background = '#eef2ff';
    });
    area.addEventListener('dragleave', () => {
      area.style.borderColor = '';
      area.style.background = '';
    });
    area.addEventListener('drop', e => {
      e.preventDefault();
      area.style.borderColor = '';
      area.style.background = '';
      if (e.dataTransfer.files.length) {
        fileInput.files = e.dataTransfer.files;
        fileInput.dispatchEvent(new Event('change'));
      }
    });
  }
}

/* ── Star rating sliders ─────────────────── */
function initStarRatings() {
  document.querySelectorAll('.range-input').forEach(input => {
    updateStars(input);
    input.addEventListener('input', () => updateStars(input));
  });
}

function updateStars(input) {
  const val = parseInt(input.value, 10);
  const display = input.nextElementSibling;
  if (!display) return;
  const stars = '⭐'.repeat(val) + '☆'.repeat(5 - val);
  display.textContent = `${stars} (${val}/5)`;
}

/* ── Confirm dialogs on dangerous forms ──── */
function initConfirmDialogs() {
  document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
      if (!confirm(form.dataset.confirm)) e.preventDefault();
    });
  });
}

/* ── Tab filter by query param ───────────── */
function initTabFilter() {
  const params = new URLSearchParams(window.location.search);
  const status = params.get('status');
  if (!status) return;

  document.querySelectorAll('.tab').forEach(tab => {
    const tabHref = new URL(tab.href, window.location.origin);
    const tabStatus = tabHref.searchParams.get('status') || '';
    if (tabStatus === status) {
      tab.classList.add('active');
    } else {
      tab.classList.remove('active');
    }
  });
}

/* ── Highlight active nav link ───────────── */
function highlightActiveNav() {
  const path = window.location.pathname;
  document.querySelectorAll('.navbar-links a').forEach(a => {
    if (a.getAttribute('href') && path.startsWith(a.getAttribute('href')) &&
        a.getAttribute('href') !== '/') {
      a.style.color = 'var(--primary)';
      a.style.fontWeight = '600';
    }
  });
}

/* ── Score display in review form ─────────── */
const scoreInput = document.querySelector('input[name="score"]');
if (scoreInput) {
  scoreInput.addEventListener('input', () => {
    const val = parseInt(scoreInput.value, 10);
    let color = '#ef4444';
    if (val >= 7) color = '#10b981';
    else if (val >= 5) color = '#f59e0b';
    scoreInput.style.borderColor = color;
  });
}

/* ── Utility: format date ─────────────────── */
function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
}
