// Student Problems JavaScript
document.addEventListener('DOMContentLoaded', function() {
    loadProblems();
    setupFilters();
});

let allProblems = [];

function loadProblems() {
    fetch('/api/problems')
        .then(response => response.json())
        .then(problems => {
            allProblems = problems;
            displayProblems(problems);
            populateCategoryFilter(problems);
        })
        .catch(error => {
            console.error('Error loading problems:', error);
        });
}

function displayProblems(problems) {
    const grid = document.getElementById('problemsGrid');
    grid.innerHTML = '';

    problems.forEach(problem => {
        const card = createProblemCard(problem);
        grid.appendChild(card);
    });
}

function createProblemCard(problem) {
    const card = document.createElement('div');
    card.className = 'problem-card';
    
    const difficultyClass = `difficulty-${problem.difficulty.toLowerCase()}`;
    
    card.innerHTML = `
        <div class="problem-difficulty ${difficultyClass}">${problem.difficulty}</div>
        <h3>${problem.title}</h3>
        <p>${problem.description}</p>
        <div class="problem-meta">
            <span class="category">Category: ${problem.category}</span>
            <span class="score">Max Score: ${problem.maxScore}</span>
        </div>
        <button onclick="solveProblem(${problem.id})" class="btn btn-primary">Solve Problem</button>
    `;
    
    return card;
}

function solveProblem(problemId) {
    const urlParams = new URLSearchParams(window.location.search);
    const username = urlParams.get('username') || (() => { try { return JSON.parse(localStorage.getItem('user')).username; } catch(e) { return 'student'; } })();
    const role = urlParams.get('role') || 'STUDENT';
    window.location.href = `/editor?problemId=${problemId}&role=${role}&username=${username}`;
}

function populateCategoryFilter(problems) {
    const categoryFilter = document.getElementById('categoryFilter');
    const categories = [...new Set(problems.map(p => p.category))];
    
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryFilter.appendChild(option);
    });
}

function setupFilters() {
    const difficultyFilter = document.getElementById('difficultyFilter');
    const categoryFilter = document.getElementById('categoryFilter');
    
    difficultyFilter.addEventListener('change', filterProblems);
    categoryFilter.addEventListener('change', filterProblems);
}

function filterProblems() {
    const difficultyFilter = document.getElementById('difficultyFilter').value;
    const categoryFilter = document.getElementById('categoryFilter').value;
    
    let filteredProblems = allProblems;
    
    if (difficultyFilter) {
        filteredProblems = filteredProblems.filter(p => p.difficulty === difficultyFilter);
    }
    
    if (categoryFilter) {
        filteredProblems = filteredProblems.filter(p => p.category === categoryFilter);
    }
    
    displayProblems(filteredProblems);
}