// Leaderboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const user = getCurrentUser();
    if (user) {
        updateUserInfo(user);
    }
    
    loadLeaderboard();
});

function updateUserInfo(user) {
    const userInfo = document.getElementById('userInfo');
    if (userInfo) {
        userInfo.textContent = `Welcome, ${user.username}`;
    }
    
    // Role-based UI is now handled by Thymeleaf server-side
}

async function loadLeaderboard() {
    try {
        const response = await fetch('/api/leaderboard');
        const leaderboard = await response.json();
        
        displayLeaderboard(leaderboard);
    } catch (error) {
        console.error('Error loading leaderboard:', error);
    }
}

function displayLeaderboard(leaderboard) {
    const tbody = document.getElementById('leaderboardBody');
    tbody.innerHTML = '';
    
    leaderboard.forEach((entry, index) => {
        const row = document.createElement('tr');
        
        // Highlight current user
        const currentUser = getCurrentUser();
        if (currentUser && entry.username === currentUser.username) {
            row.style.backgroundColor = '#e3f2fd';
            row.style.fontWeight = 'bold';
        }
        
        row.innerHTML = `
            <td>${entry.rank}</td>
            <td>${entry.username}</td>
            <td>${entry.totalScore}</td>
            <td>${entry.role}</td>
        `;
        
        tbody.appendChild(row);
    });
}