// Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    if (!requireAuth()) return;
    
    const user = getCurrentUser();
    updateUserInfo(user);
    loadDashboardData(user);
});

function updateUserInfo(user) {
    const userInfo = document.getElementById('userInfo');
    if (userInfo) {
        userInfo.textContent = `Welcome, ${user.username}`;
    }
    
    const totalScore = document.getElementById('totalScore');
    if (totalScore) {
        totalScore.textContent = user.totalScore || 0;
    }
    
    // Role-based UI is now handled by Thymeleaf server-side
}

async function loadDashboardData(user) {
    try {
        // Load leaderboard to get current rank
        const leaderboardResponse = await fetch('/api/leaderboard');
        const leaderboard = await leaderboardResponse.json();
        
        const userRank = leaderboard.findIndex(entry => entry.username === user.username) + 1;
        const currentRank = document.getElementById('currentRank');
        if (currentRank) {
            currentRank.textContent = userRank > 0 ? userRank : '-';
        }
        
        // Set problems solved from user data
        const problemsSolved = document.getElementById('problemsSolved');
        if (problemsSolved) {
            problemsSolved.textContent = user.problemsSolved || 0;
        }
        
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}