// Problems JavaScript

let allProblems = [];

document.addEventListener('DOMContentLoaded', function() {
    const user = getCurrentUser();
    if (user) {
        updateUserInfo(user);
    }
    
    loadProblems();
    setupFilters();
});

function updateUserInfo(user) {
    const userInfo = document.getElementById('userInfo');
    if (userInfo) {
        userInfo.textContent = `Welcome, ${user.username}`;
    }
    
    // Role-based UI is now handled by Thymeleaf server-side
}

async function loadProblems() {
    try {
        console.log('Loading problems...');
        const response = await fetch('/api/problems');
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        allProblems = await response.json();
        console.log('Loaded problems:', allProblems.length);
        displayProblems(allProblems);
    } catch (error) {
        console.error('Error loading problems:', error);
        const grid = document.getElementById('problemsGrid');
        grid.innerHTML = '<p style="color: white; text-align: center;">Error loading problems. Please try again.</p>';
    }
}

function displayProblems(problems) {
    const grid = document.getElementById('problemsGrid');
    grid.innerHTML = '';
    
    console.log('Displaying', problems.length, 'problems');
    
    if (problems.length === 0) {
        grid.innerHTML = '<p style="color: white; text-align: center;">No problems found.</p>';
        return;
    }
    
    problems.forEach(problem => {
        const card = createProblemCard(problem);
        grid.appendChild(card);
    });
}

function createProblemCard(problem) {
    const card = document.createElement('div');
    card.className = 'problem-card';
    
    const difficultyClass = `difficulty-${problem.difficulty.toLowerCase()}`;
    const user = getCurrentUser();
    const solveButton = user && user.role !== 'ADMIN' ? 
        `<button onclick="solveProblem(${problem.id})" class="btn btn-primary">Solve Problem</button>` : '';
    
    card.innerHTML = `
        <div class="problem-difficulty ${difficultyClass}">${problem.difficulty}</div>
        <h3>${problem.title}</h3>
        <p><strong>Category:</strong> ${problem.category}</p>
        <p>${problem.description.substring(0, 100)}...</p>
        <div style="margin-top: 1rem;">
            ${solveButton}
        </div>
    `;
    
    return card;
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

function solveProblem(problemId) {
    window.location.href = `/editor?problemId=${problemId}`;
}