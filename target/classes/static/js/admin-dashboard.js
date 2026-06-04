/* ── Admin Dashboard JS ── */

var allStudents = [];
var allProblems = [];

document.addEventListener('DOMContentLoaded', function () {
    loadStats();
    loadStudents();
    loadProblems();
});

/* ── TABS ── */
function switchTab(name, btn) {
    document.querySelectorAll('.admin-panel').forEach(function (p) { p.classList.remove('active'); });
    document.querySelectorAll('.menu-item').forEach(function (b) { b.classList.remove('active'); });
    document.getElementById('tab-' + name).classList.add('active');
    if (btn) btn.classList.add('active');
}

/* ── TOAST ── */
function showToast(msg, type) {
    var t = document.getElementById('toast');
    t.innerHTML = (type === 'success' ? '✅ ' : '❌ ') + msg;
    t.className = 'toast ' + type + ' show';
    setTimeout(function () { t.className = 'toast'; }, 3500);
}

/* ── STATS ── */
function loadStats() {
    fetch('/api/admin/users')
        .then(function (r) { return r.json(); })
        .then(function (users) {
            document.getElementById('statUsers').textContent = users.length;
            var students = users.filter(function (u) { return u.role === 'STUDENT'; });
            document.getElementById('statStudents').textContent = students.length;
        })
        .catch(function () {
            document.getElementById('statUsers').textContent = '—';
            document.getElementById('statStudents').textContent = '—';
        });

    fetch('/api/problems')
        .then(function (r) { return r.json(); })
        .then(function (problems) {
            document.getElementById('statProblems').textContent = problems.length;
        })
        .catch(function () { document.getElementById('statProblems').textContent = '—'; });

    // submissions count
    fetch('/api/admin/submissions')
        .then(function (r) { return r.ok ? r.json() : []; })
        .then(function (subs) { document.getElementById('statSubmissions').textContent = subs.length; })
        .catch(function () { document.getElementById('statSubmissions').textContent = '—'; });
}

/* ── STUDENTS ── */
function loadStudents() {
    fetch('/api/admin/users')
        .then(function (r) { return r.json(); })
        .then(function (users) {
            allStudents = users.filter(function (u) { return u.role === 'STUDENT'; });
            renderStudents(allStudents);
        })
        .catch(function () {
            document.getElementById('studentsGrid').innerHTML =
                '<div class="no-data">Could not load students.</div>';
        });
}

function renderStudents(students) {
    var grid = document.getElementById('studentsGrid');
    if (!students.length) {
        grid.innerHTML = '<tr><td colspan="6" class="no-data">No students found.</td></tr>';
        return;
    }

    grid.innerHTML = students.map(function (u, i) {
        var score   = u.totalScore || 0;
        var solved  = u.problemsSolved || 0;
        var scoreClass = score === 0 ? 'score-zero' : score < 200 ? 'score-low' : score < 500 ? 'score-mid' : 'score-high';
        var initial = (u.username || 'U').charAt(0).toUpperCase();
        var colors  = ['135deg,#3b82f6,#8b5cf6', '135deg,#10b981,#3b82f6', '135deg,#f59e0b,#ef4444',
                       '135deg,#8b5cf6,#ec4899', '135deg,#06b6d4,#3b82f6'];
        var grad    = colors[u.username.charCodeAt(0) % colors.length];
        var progress = Math.min(100, Math.round((score / 1000) * 100));
        var status = score === 0 ? '<span style="color:var(--text-muted);font-size:0.75rem">● Inactive</span>' :
                     score < 200 ? '<span style="color:var(--orange);font-size:0.75rem">● Beginner</span>' :
                     score < 500 ? '<span style="color:var(--accent);font-size:0.75rem">● Active</span>' :
                     '<span style="color:var(--green);font-size:0.75rem">● Expert</span>';

        return '<tr>'
            + '<td style="color:var(--text-muted);font-family:\'JetBrains Mono\',monospace;font-size:0.8rem;font-weight:600">' + (i + 1) + '</td>'
            + '<td>'
            +   '<div class="s-user">'
            +     '<div class="s-avatar" style="background:linear-gradient(' + grad + ')">' + initial + '</div>'
            +     '<div>'
            +       '<div class="s-name">' + escHtml(u.username) + '</div>'
            +       '<div class="s-email">' + escHtml(u.email || 'No email') + '</div>'
            +     '</div>'
            +   '</div>'
            + '</td>'
            + '<td><span class="s-score ' + scoreClass + '">' + score + ' pts</span></td>'
            + '<td>'
            +   '<div class="s-bar-wrap"><div class="s-bar" style="width:' + progress + '%"></div></div>'
            + '</td>'
            + '<td style="font-weight:600;color:var(--accent)">' + solved + '</td>'
            + '<td>' + status + '</td>'
            + '</tr>';
    }).join('');
}

function filterStudents(query) {
    var q = query.toLowerCase().trim();
    var filtered = q ? allStudents.filter(function (u) {
        return u.username.toLowerCase().includes(q) || (u.email || '').toLowerCase().includes(q);
    }) : allStudents;
    renderStudents(filtered);
}

/* ── PROBLEMS TABLE ── */
function loadProblems() {
    fetch('/api/problems')
        .then(function (r) { return r.json(); })
        .then(function (problems) {
            allProblems = problems;
            renderProblemsTable(problems);
        })
        .catch(function () {
            document.getElementById('problemsBody').innerHTML =
                '<tr><td colspan="5" class="no-data">Could not load problems.</td></tr>';
        });
}

function renderProblemsTable(problems) {
    var diffColor = { EASY: 'difficulty-easy', MEDIUM: 'difficulty-medium', HARD: 'difficulty-hard' };
    document.getElementById('problemsBody').innerHTML = problems.length ? problems.map(function (p, i) {
        return '<tr>'
            + '<td style="color:var(--text-muted);font-family:\'JetBrains Mono\',monospace;font-size:0.8rem">' + (i + 1) + '</td>'
            + '<td style="font-weight:600">' + escHtml(p.title) + '</td>'
            + '<td><span class="difficulty-badge ' + (diffColor[p.difficulty] || '') + '">' + p.difficulty + '</span></td>'
            + '<td><span class="category-tag">' + escHtml(p.category || '—') + '</span></td>'
            + '<td><div style="display:flex;gap:0.4rem">'
            +   '<button class="btn btn-secondary" style="padding:0.3rem 0.7rem;font-size:0.78rem" onclick="editProblem(' + p.id + ')">✏️ Edit</button>'
            +   '<button style="padding:0.3rem 0.7rem;font-size:0.78rem;border-radius:8px;border:1px solid var(--red);background:transparent;color:var(--red);cursor:pointer;font-family:inherit" onclick="deleteProblem(' + p.id + ',\'' + escHtml(p.title).replace(/'/g, "\\'") + '\')">🗑</button>'
            + '</div></td>'
            + '</tr>';
    }).join('') : '<tr><td colspan="5" class="no-data">No problems found.</td></tr>';
}

/* ── ADD / EDIT PROBLEM ── */
function submitProblem(e) {
    e.preventDefault();
    var form   = document.getElementById('addProblemForm');
    var editId = form.dataset.editId;
    var isEdit = !!editId;
    var btn    = document.getElementById('formSubmitBtn');

    var data = {
        title:          document.getElementById('title').value,
        description:    document.getElementById('description').value,
        difficulty:     document.getElementById('difficulty').value,
        category:       document.getElementById('category').value,
        testCases:      document.getElementById('testCases').value,
        expectedOutput: document.getElementById('expectedOutput').value,
        maxScore: 100
    };

    btn.textContent = 'Saving…';
    btn.disabled    = true;

    fetch(isEdit ? '/api/admin/problems/' + editId : '/api/admin/problems', {
        method:  isEdit ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify(data)
    })
    .then(function (r) { return r.json(); })
    .then(function (result) {
        if (result.success !== false) {
            showToast(isEdit ? 'Problem updated!' : 'Problem added!', 'success');
            resetProblemForm();
            loadStats();
            loadProblems();
            setTimeout(function () {
                switchTab('problems', document.querySelector('[onclick*="problems"]'));
            }, 800);
        } else {
            showToast('Error: ' + (result.message || 'Unknown error'), 'error');
        }
    })
    .catch(function () { showToast('Network error. Try again.', 'error'); })
    .finally(function () {
        btn.textContent = isEdit ? '✔ Update Problem' : '➕ Add Problem';
        btn.disabled    = false;
    });
}

function editProblem(id) {
    fetch('/api/problems/' + id)
        .then(function (r) { return r.json(); })
        .then(function (p) {
            document.getElementById('title').value          = p.title || '';
            document.getElementById('description').value    = p.description || '';
            document.getElementById('difficulty').value     = p.difficulty || 'EASY';
            document.getElementById('category').value       = p.category || '';
            document.getElementById('testCases').value      = p.testCases || '';
            document.getElementById('expectedOutput').value = p.expectedOutput || '';

            document.getElementById('addProblemForm').dataset.editId = id;
            document.getElementById('formPanelTitle').textContent = '✏️ Edit Problem';
            document.getElementById('formSubmitBtn').textContent  = '✔ Update Problem';

            switchTab('addProblem', document.querySelector('[onclick*="addProblem"]'));
        })
        .catch(function () { showToast('Could not load problem.', 'error'); });
}

function deleteProblem(id, title) {
    if (!confirm('Delete "' + title + '"?\nThis cannot be undone.')) return;
    fetch('/api/admin/problems/' + id, { method: 'DELETE' })
        .then(function (r) {
            if (r.ok) {
                showToast('Problem deleted.', 'success');
                loadStats();
                loadProblems();
            } else {
                showToast('Could not delete problem.', 'error');
            }
        })
        .catch(function () { showToast('Network error.', 'error'); });
}

function resetProblemForm() {
    document.getElementById('addProblemForm').reset();
    delete document.getElementById('addProblemForm').dataset.editId;
    document.getElementById('formPanelTitle').textContent = '➕ Add New Problem';
    document.getElementById('formSubmitBtn').textContent  = '➕ Add Problem';
    document.getElementById('formSubmitBtn').disabled     = false;
}

/* ── UTIL ── */
function escHtml(str) {
    return String(str || '')
        .replace(/&/g,'&amp;').replace(/</g,'&lt;')
        .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
