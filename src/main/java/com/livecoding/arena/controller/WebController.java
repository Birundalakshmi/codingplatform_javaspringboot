package com.livecoding.arena.controller;

import com.livecoding.arena.entity.User;
import com.livecoding.arena.service.ProblemService;
import com.livecoding.arena.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    
    @Autowired
    private ProblemService problemService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String role,
                            @RequestParam(required = false) String username,
                            Model model) {
        // Guard: redirect non-students to their correct page
        if ("MENTOR".equals(role)) {
            return "redirect:/mentor?role=MENTOR&username=" + (username != null ? username : "");
        }
        if ("ADMIN".equals(role)) {
            return "redirect:/admin-dashboard";
        }
        model.addAttribute("userRole", role);
        model.addAttribute("username", username);
        
        // Get or create the actual user
        String actualUsername = username != null ? username : "student";
        User user = userService.findByUsername(actualUsername).orElse(null);
        if (user == null) {
            user = userService.registerUser(actualUsername, actualUsername + "@test.com", "password", User.Role.STUDENT);
        }
        
        model.addAttribute("totalScore", user.getTotalScore());
        model.addAttribute("problemsSolved", user.getProblemsSolved());
        model.addAttribute("currentRank", user.getProblemsSolved() > 0 ? "1" : "-");
        
        // Get actual solved problems for this user
        java.util.List<String> solvedProblems = userService.getSolvedProblems(actualUsername);
        System.out.println("CONTROLLER DEBUG: Solved problems for " + actualUsername + ": " + solvedProblems);
        model.addAttribute("solvedProblems", solvedProblems);
        
        return "dashboard";
    }
    
    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
    
    @GetMapping("/problems")
    public String problems(@RequestParam(required = false) String role,
                           @RequestParam(required = false) String username,
                           Model model) {
        try {
            model.addAttribute("userRole", role);
            model.addAttribute("username", username);
            var problems = problemService.getAllProblems();
            model.addAttribute("problems", problems != null ? problems : java.util.Collections.emptyList());
            return "problems";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading problems: " + e.getMessage());
            model.addAttribute("problems", java.util.Collections.emptyList());
            return "problems";
        }
    }
    
    @GetMapping("/editor")
    public String editor(@RequestParam(required = false) Long problemId, @RequestParam(required = false) String role, Model model) {
        // Redirect non-students away from editor
        if (role != null && !"STUDENT".equals(role)) {
            if ("MENTOR".equals(role)) {
                return "redirect:/mentor?role=" + role;
            } else if ("ADMIN".equals(role)) {
                return "redirect:/admin?role=" + role;
            }
        }
        
        model.addAttribute("problems", problemService.getAllProblems());
        if (problemId != null) {
            problemService.getProblemById(problemId).ifPresent(problem -> 
                model.addAttribute("selectedProblem", problem));
        }
        return "editor";
    }
    
    @GetMapping("/admin")
    public String admin(@RequestParam(required = false) String role, Model model) {
        model.addAttribute("userRole", role);
        model.addAttribute("problems", problemService.getAllProblems());
        return "admin";
    }
    
    @GetMapping("/leaderboard")
    public String leaderboard(@RequestParam(required = false) String role,
                               @RequestParam(required = false) String username,
                               Model model) {
        model.addAttribute("userRole", role);
        model.addAttribute("username", username);
        java.util.List<User> users = userService.getAllUsers();
        users.sort((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()));
        model.addAttribute("users", users);
        return "leaderboard";
    }
    
    @GetMapping("/student-problems")
    public String studentProblems() {
        return "student-problems";
    }
    
    @GetMapping("/mentor")
    public String mentor(@RequestParam(required = false) String role,
                         @RequestParam(required = false) String username,
                         Model model) {
        model.addAttribute("userRole", role != null ? role : "MENTOR");
        model.addAttribute("username", username);
        model.addAttribute("problems", problemService.getAllProblems());

        long studentCount = userService.getStudentCount();
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("submissionCount", 0);
        model.addAttribute("topStudents", userService.getTopStudents(3));
        model.addAttribute("strugglingStudents", userService.getStrugglingStudents(3));

        return "mentor";
    }
}