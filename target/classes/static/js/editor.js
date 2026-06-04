const templates = {
    java: 'public class Solution {\n    public static void main(String[] args) {\n        // Your code here\n    }\n}',
    python: 'def solution():\n    # Your code here\n    pass\n\nif __name__ == "__main__":\n    solution()',
    cpp: '#include <iostream>\nusing namespace std;\n\nint main() {\n    // Your code here\n    return 0;\n}',
    c: '#include <stdio.h>\n\nint main() {\n    // Your code here\n    return 0;\n}'
};

function getUrlParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}

function getUsername() {
    const fromUrl = getUrlParam('username');
    if (fromUrl) return fromUrl;
    try {
        const user = JSON.parse(localStorage.getItem('user'));
        return user ? user.username : 'student';
    } catch (e) {
        return 'student';
    }
}

function changeLanguage() {
    const lang = document.getElementById('language').value;
    const editor = document.getElementById('codeEditor');
    if (editor && !editor.dataset.modified) {
        editor.value = templates[lang] || templates.java;
    }
}

function clearEditor() {
    const editor = document.getElementById('codeEditor');
    if (editor) {
        editor.value = '';
        editor.dataset.modified = '';
    }
}

function loadSelectedProblem() {
    const select = document.getElementById('problemSelect');
    if (select && select.value) {
        const username = getUsername();
        const role = getUrlParam('role') || 'STUDENT';
        window.location.href = '/editor?problemId=' + select.value + '&role=' + role + '&username=' + username;
    }
}

function navigateTo(path) {
    const username = getUsername();
    const role = getUrlParam('role') || 'STUDENT';
    window.location.href = path + '?role=' + role + '&username=' + username;
}

function showOutput(html) {
    const output = document.getElementById('output');
    if (output) output.innerHTML = html;
}

function runCode() {
    const code = document.getElementById('codeEditor').value;
    const language = document.getElementById('language').value;

    if (!code.trim()) {
        showOutput('<span style="color:#f87171">Please write some code first!</span>');
        return;
    }

    showOutput('<span style="color:#94a3b8">Running code...</span>');

    fetch('/api/code/run', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code: code, language: language })
    })
    .then(function(r) { return r.json(); })
    .then(function(result) {
        if (result.success) {
            showOutput(
                '<strong style="color:#4ade80">&#10003; Executed Successfully</strong><br><br>' +
                '<pre style="white-space:pre-wrap;background:#0f172a;padding:10px;border-radius:6px;border-left:3px solid #4ade80">' +
                escapeHtml(result.output || '(no output)') + '</pre>'
            );
        } else {
            showOutput(formatError(result.error || 'Unknown error'));
        }
    })
    .catch(function() {
        showOutput('<span style="color:#f87171">&#10007; Could not connect to server.</span>');
    });
}

function formatError(err) {
    var isCompile = err.indexOf('Compilation Error') !== -1 || err.indexOf('[Line ') !== -1;
    var isSyntax  = err.indexOf('Syntax Error') !== -1;
    var isTLE     = err.indexOf('Time Limit') !== -1;
    var isNull    = err.indexOf('NullPointerException') !== -1;
    var isAIOB    = err.indexOf('ArrayIndexOutOfBounds') !== -1;
    var isArith   = err.indexOf('ArithmeticException') !== -1;
    var isSOF     = err.indexOf('StackOverflow') !== -1;
    var isOOM     = err.indexOf('OutOfMemory') !== -1;
    var isNSE     = err.indexOf('NoSuchElement') !== -1 || err.indexOf('InputMismatch') !== -1;
    var isRuntime = err.indexOf('RuntimeError') !== -1 || err.indexOf('Runtime Error') !== -1 ||
                    isNull || isAIOB || isArith || isSOF || isOOM || isNSE;

    var label, color, icon;
    if (isSyntax)       { label = 'Syntax Error';                        color = '#fb923c'; icon = '&#9888;'; }
    else if (isCompile) { label = 'Compilation Error';                   color = '#fb923c'; icon = '&#9888;'; }
    else if (isTLE)     { label = 'Time Limit Exceeded';                 color = '#facc15'; icon = '&#9201;'; }
    else if (isNull)    { label = 'NullPointerException';                color = '#f87171'; icon = '&#10007;'; }
    else if (isAIOB)    { label = 'ArrayIndexOutOfBoundsException';      color = '#f87171'; icon = '&#10007;'; }
    else if (isArith)   { label = 'ArithmeticException (divide by zero)';color = '#f87171'; icon = '&#10007;'; }
    else if (isSOF)     { label = 'StackOverflowError';                  color = '#f87171'; icon = '&#10007;'; }
    else if (isOOM)     { label = 'OutOfMemoryError';                    color = '#f87171'; icon = '&#10007;'; }
    else if (isNSE)     { label = 'Input Mismatch / No Such Element';    color = '#f87171'; icon = '&#10007;'; }
    else if (isRuntime) { label = 'Runtime Error';                       color = '#f87171'; icon = '&#10007;'; }
    else                { label = 'Error';                               color = '#f87171'; icon = '&#10007;'; }

    // Strip leading type prefix from message body to avoid duplication
    var body = err
        .replace(/^Compilation Error:\n?/i, '')
        .replace(/^Runtime Error:\n?/i, '')
        .replace(/^Execution Error:\n?/i, '')
        .trim();

    return '<strong style="color:' + color + ';font-size:1.05em">' + icon + ' ' + label + '</strong><br><br>' +
           '<pre style="color:#fca5a5;white-space:pre-wrap;background:#1e1e2e;padding:12px;border-radius:6px;border-left:4px solid ' + color + ';font-size:0.9em;line-height:1.6">' +
           escapeHtml(body) + '</pre>';
}

function submitCode() {
    const code = document.getElementById('codeEditor').value;
    const language = document.getElementById('language').value;
    const problemId = getUrlParam('problemId') || '1';
    const username = getUsername();

    if (!code.trim()) {
        showOutput('<span style="color:#f87171">Please write some code first!</span>');
        return;
    }

    showOutput('<span style="color:#94a3b8">Submitting your solution...</span>');

    fetch('/api/submissions/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            problemId: parseInt(problemId),
            code: code,
            language: language,
            username: username
        })
    })
    .then(function(r) { return r.json(); })
    .then(function(result) {
        var passed = result.testCasesPassed || 0;
        var total  = result.totalTestCases  || 0;
        var status = (result.status || 'FAILED');
        var isCompileError = status === 'COMPILATION_ERROR';
        var isAccepted     = result.success === true;

        // ── Compilation error: show full compiler output, no test table ──
        if (isCompileError) {
            var compileErr = (result.message || result.output || 'Unknown compilation error')
                .replace(/^Compilation Error:\n?/i, '').trim();
            showOutput(formatError(compileErr));
            return;
        }

        // ── Build per-test-case rows ──
        var lines = (result.output || '').split('\n').filter(function(l) { return l.trim(); });
        var detail = '';
        if (lines.length > 0) {
            detail = '<table style="width:100%;border-collapse:collapse;font-size:0.85em;margin-top:6px">' +
                lines.map(function(l) {
                    var isPass = l.indexOf('PASS') !== -1;
                    var isFail = l.indexOf('FAIL') !== -1;
                    var icon  = isPass ? '&#10003;' : (isFail ? '&#10007;' : '&#8226;');
                    var color = isPass ? '#4ade80'  : (isFail ? '#f87171'  : '#94a3b8');
                    // Highlight the "Got:" part in red for failures
                    var rowText = escapeHtml(l).replace(/(Got: )(.+)$/, function(_, pre, val) {
                        return pre + '<span style="color:' + (isFail ? '#f87171' : '#94a3b8') + '">' + val + '</span>';
                    });
                    return '<tr style="border-bottom:1px solid #1e293b">' +
                        '<td style="width:24px;color:' + color + ';padding:4px 6px;font-weight:bold">' + icon + '</td>' +
                        '<td style="padding:4px 6px;color:' + color + '">' + rowText + '</td>' +
                        '</tr>';
                }).join('') +
                '</table>';
        }

        // ── Runtime / Wrong answer error banner ──
        var errorBanner = '';
        if (!isAccepted && result.message) {
            errorBanner = formatError(result.message) + '<br>';
        }

        if (isAccepted) {
            showOutput(
                '<strong style="color:#4ade80;font-size:1.1em">&#10003; All Test Cases Passed!</strong><br>' +
                'Status: <strong style="color:#4ade80">ACCEPTED</strong> &nbsp;|&nbsp; ' +
                'Score: <strong>+' + (result.score || 100) + ' pts</strong> &nbsp;|&nbsp; ' +
                'Tests: <strong>' + passed + '/' + total + '</strong><br>' +
                '<hr style="border-color:#334155;margin:8px 0">' +
                detail + '<br>' +
                '<strong style="color:#fbbf24">&#9733; Points added to leaderboard!</strong>'
            );
        } else {
            var statusLabel = status.replace(/_/g, ' ');
            var statusColor = status === 'WRONG_ANSWER' ? '#f87171' :
                              status === 'TIME_LIMIT_EXCEEDED' ? '#facc15' : '#f87171';
            showOutput(
                '<strong style="color:' + statusColor + ';font-size:1.1em">&#10007; ' + statusLabel + '</strong>' +
                '  <span style="color:#94a3b8">(' + passed + '/' + total + ' passed)</span><br>' +
                '<hr style="border-color:#334155;margin:8px 0">' +
                errorBanner +
                detail
            );
        }
    })
    .catch(function() {
        showOutput('<span style="color:#f87171">&#10007; Submission failed. Please try again.</span>');
    });
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

document.addEventListener('DOMContentLoaded', function () {
    var langSelect    = document.getElementById('language');
    var problemSelect = document.getElementById('problemSelect');
    var editor        = document.getElementById('codeEditor');

    if (langSelect && editor) {
        editor.value = templates[langSelect.value] || templates.java;
        langSelect.addEventListener('change', changeLanguage);
        editor.addEventListener('input', function () {
            this.dataset.modified = 'true';
        });
    }

    var problemId = getUrlParam('problemId');
    if (problemId && problemSelect) {
        problemSelect.value = problemId;
    }

    if (problemSelect) {
        problemSelect.addEventListener('change', loadSelectedProblem);
    }
});
