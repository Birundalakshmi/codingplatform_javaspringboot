package com.livecoding.arena.service;

import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    public List<Problem> getAllProblems() { return problemRepository.findAll(); }
    public Optional<Problem> getProblemById(Long id) { return problemRepository.findById(id); }
    public Problem createProblem(Problem problem) { return problemRepository.save(problem); }
    public List<Problem> getProblemsByCategory(String category) { return problemRepository.findAll(); }
    public List<Problem> getProblemsByDifficulty(Problem.Difficulty difficulty) { return problemRepository.findAll(); }

    public void initializeSampleProblems() {
        if (problemRepository.count() > 0) return;

        // ── 1. Hello World ────────────────────────────────────────────────────
        save("Hello World",
            "Print 'Hello, World!' to the console.",
            Problem.Difficulty.EASY, "Basics",
            "No input required.\nNo input required.\nNo input required.\nNo input required.\nNo input required.",
            "[\"Hello, World!\",\"Hello, World!\",\"Hello, World!\",\"Hello, World!\",\"Hello, World!\"]",
            "No input needed — just print Hello, World!",
            "[\"Hello, World!\",\"Hello, World!\",\"Hello, World!\",\"Hello, World!\",\"Hello, World!\"]");

        // ── 2. Sum of Two Numbers ─────────────────────────────────────────────
        save("Sum of Two Numbers",
            "Read two integers from input and print their sum.",
            Problem.Difficulty.EASY, "Math",
            "1 2\n3 4\n10 20\n-1 1\n100 200",
            "[\"3\",\"7\",\"30\",\"0\",\"300\"]",
            "Input: two space-separated integers\nOutput: their sum\nExample: 1 2 → 3",
            "[\"3\",\"7\",\"30\",\"0\",\"300\"]");

        // ── 3. Factorial ──────────────────────────────────────────────────────
        save("Factorial",
            "Given a non-negative integer n, print its factorial.",
            Problem.Difficulty.EASY, "Math",
            "0\n1\n5\n7\n10",
            "[\"1\",\"1\",\"120\",\"5040\",\"3628800\"]",
            "Input: single integer n (0 ≤ n ≤ 12)\nOutput: n!\nExample: 5 → 120",
            "[\"1\",\"1\",\"120\",\"5040\",\"3628800\"]");

        // ── 4. Palindrome Check ───────────────────────────────────────────────
        save("Palindrome Check",
            "Read a string and print 'true' if it is a palindrome, 'false' otherwise.",
            Problem.Difficulty.EASY, "String",
            "racecar\nhello\nmadam\njava\nlevel",
            "[\"true\",\"false\",\"true\",\"false\",\"true\"]",
            "Input: a single word (no spaces)\nOutput: true or false\nExample: racecar → true",
            "[\"true\",\"false\",\"true\",\"false\",\"true\"]");

        // ── 5. Reverse String ─────────────────────────────────────────────────
        save("Reverse String",
            "Read a string and print it reversed.",
            Problem.Difficulty.EASY, "String",
            "hello\nworld\njava\nabcde\nOpenAI",
            "[\"olleh\",\"dlrow\",\"avaj\",\"edcba\",\"IAnepO\"]",
            "Input: a single word\nOutput: the reversed word\nExample: hello → olleh",
            "[\"olleh\",\"dlrow\",\"avaj\",\"edcba\",\"IAnepO\"]");

        // ── 6. Count Vowels ───────────────────────────────────────────────────
        save("Count Vowels",
            "Read a string and print the number of vowels (a, e, i, o, u) it contains.",
            Problem.Difficulty.EASY, "String",
            "hello\nworld\naeiou\njavaprogramming\nxyz",
            "[\"2\",\"1\",\"5\",\"6\",\"0\"]",
            "Input: a single word\nOutput: count of vowels\nExample: hello → 2",
            "[\"2\",\"1\",\"5\",\"6\",\"0\"]");

        // ── 7. FizzBuzz ───────────────────────────────────────────────────────
        save("FizzBuzz",
            "Read an integer n. For each number 1 to n, print Fizz if divisible by 3, Buzz if by 5, FizzBuzz if by both, else the number. Each on a new line.",
            Problem.Difficulty.EASY, "Math",
            "3\n5\n15\n1\n7",
            "[\"1\\n2\\nFizz\",\"1\\n2\\nFizz\\n4\\nBuzz\",\"1\\n2\\nFizz\\n4\\nBuzz\\nFizz\\n7\\n8\\nFizz\\nBuzz\\n11\\nFizz\\n13\\n14\\nFizzBuzz\",\"1\",\"1\\n2\\nFizz\\n4\\nBuzz\\nFizz\\n7\"]",
            "Input: integer n\nOutput: FizzBuzz sequence from 1 to n\nExample: 3 → 1, 2, Fizz",
            "[\"1\\n2\\nFizz\",\"1\\n2\\nFizz\\n4\\nBuzz\",\"1\\n2\\nFizz\\n4\\nBuzz\\nFizz\\n7\\n8\\nFizz\\nBuzz\\n11\\nFizz\\n13\\n14\\nFizzBuzz\",\"1\",\"1\\n2\\nFizz\\n4\\nBuzz\\nFizz\\n7\"]");

        // ── 8. Max in Array ───────────────────────────────────────────────────
        save("Maximum in Array",
            "Read n integers on one line separated by spaces and print the maximum.",
            Problem.Difficulty.EASY, "Array",
            "3 1 4 1 5 9 2 6\n1\n-5 -1 -3\n100 200 150\n7 7 7 7",
            "[\"9\",\"1\",\"-1\",\"200\",\"7\"]",
            "Input: space-separated integers\nOutput: the maximum\nExample: 3 1 4 1 5 9 2 6 → 9",
            "[\"9\",\"1\",\"-1\",\"200\",\"7\"]");

        // ── 9. Sum of Array ───────────────────────────────────────────────────
        save("Sum of Array",
            "Read space-separated integers and print their sum.",
            Problem.Difficulty.EASY, "Array",
            "1 2 3 4 5\n10 20 30\n-1 -2 -3\n0\n100 200 300 400",
            "[\"15\",\"60\",\"-6\",\"0\",\"1000\"]",
            "Input: space-separated integers\nOutput: sum\nExample: 1 2 3 4 5 → 15",
            "[\"15\",\"60\",\"-6\",\"0\",\"1000\"]");

        // ── 10. Even or Odd ───────────────────────────────────────────────────
        save("Even or Odd",
            "Read an integer and print 'Even' if it is even, 'Odd' otherwise.",
            Problem.Difficulty.EASY, "Math",
            "4\n7\n0\n-3\n100",
            "[\"Even\",\"Odd\",\"Even\",\"Odd\",\"Even\"]",
            "Input: single integer\nOutput: Even or Odd\nExample: 4 → Even",
            "[\"Even\",\"Odd\",\"Even\",\"Odd\",\"Even\"]");

        // ── 11. Fibonacci ─────────────────────────────────────────────────────
        save("Fibonacci Number",
            "Given n, print the nth Fibonacci number (0-indexed: F(0)=0, F(1)=1).",
            Problem.Difficulty.MEDIUM, "Dynamic Programming",
            "0\n1\n5\n10\n15",
            "[\"0\",\"1\",\"5\",\"55\",\"610\"]",
            "Input: integer n (0 ≤ n ≤ 20)\nOutput: F(n)\nExample: 5 → 5",
            "[\"0\",\"1\",\"5\",\"55\",\"610\"]");

        // ── 12. Binary Search ─────────────────────────────────────────────────
        save("Binary Search",
            "Read a sorted array of integers on line 1 and a target on line 2. Print the index of the target or -1 if not found.",
            Problem.Difficulty.MEDIUM, "Array",
            "1 3 5 7 9\n3\n2 4 6 8 10\n5\n1 2 3 4 5\n6\n10 20 30 40 50\n40\n1\n1",
            "[\"1\",\"-1\",\"-1\",\"3\",\"0\"]",
            "Line 1: space-separated sorted integers\nLine 2: target\nOutput: index or -1\nExample: [1,3,5,7,9] target=3 → 1",
            "[\"1\",\"-1\",\"-1\",\"3\",\"0\"]");

        // ── 13. Count Words ───────────────────────────────────────────────────
        save("Count Words",
            "Read a sentence and print the number of words in it.",
            Problem.Difficulty.EASY, "String",
            "Hello World\nThe quick brown fox\nJava\nI love coding\none two three four five",
            "[\"2\",\"4\",\"1\",\"3\",\"5\"]",
            "Input: a sentence\nOutput: number of words\nExample: Hello World → 2",
            "[\"2\",\"4\",\"1\",\"3\",\"5\"]");

        // ── 14. Power of Two ──────────────────────────────────────────────────
        save("Power of Two",
            "Given an integer n, print 'true' if it is a power of two, 'false' otherwise.",
            Problem.Difficulty.EASY, "Math",
            "1\n2\n3\n16\n18",
            "[\"true\",\"true\",\"false\",\"true\",\"false\"]",
            "Input: integer n\nOutput: true or false\nExample: 16 → true",
            "[\"true\",\"true\",\"false\",\"true\",\"false\"]");

        // ── 15. Climbing Stairs ───────────────────────────────────────────────
        save("Climbing Stairs",
            "You can climb 1 or 2 steps at a time. Given n steps, print the number of distinct ways to reach the top.",
            Problem.Difficulty.MEDIUM, "Dynamic Programming",
            "1\n2\n3\n5\n10",
            "[\"1\",\"2\",\"3\",\"8\",\"89\"]",
            "Input: integer n\nOutput: number of ways\nExample: 2 → 2",
            "[\"1\",\"2\",\"3\",\"8\",\"89\"]");
    }

    private void save(String title, String description, Problem.Difficulty difficulty,
                      String category, String testCasesDisplay, String testCasesJson,
                      String testInputsDisplay, String expectedOutputJson) {
        if (problemRepository.findAll().stream().anyMatch(p -> p.getTitle().equals(title))) return;
        Problem p = new Problem();
        p.setTitle(title);
        p.setDescription(description);
        p.setDifficulty(difficulty);
        p.setCategory(category);
        p.setTestCases(testCasesDisplay);
        p.setTestInputs(testInputsDisplay);    // JSON array of 5 inputs
        p.setExpectedOutput(expectedOutputJson); // JSON array of 5 expected outputs
        p.setMaxScore(100);
        problemRepository.save(p);
    }

    public Problem createProblem(String title, String description, Problem.Difficulty difficulty,
                                 String category, String testCases, String expectedOutput) {
        if (problemRepository.findAll().stream().anyMatch(p -> p.getTitle().equals(title))) return problemRepository.findAll().stream().filter(p -> p.getTitle().equals(title)).findFirst().get();
        Problem p = new Problem();
        p.setTitle(title); p.setDescription(description); p.setDifficulty(difficulty);
        p.setCategory(category); p.setTestCases(testCases); p.setExpectedOutput(expectedOutput);
        p.setMaxScore(100);
        return problemRepository.save(p);
    }

    public Problem updateProblem(Long id, com.livecoding.arena.controller.AdminController.ProblemRequest req) {
        Problem p = problemRepository.findById(id).orElseThrow(() -> new RuntimeException("Problem not found: " + id));
        p.setTitle(req.getTitle()); p.setDescription(req.getDescription());
        p.setDifficulty(Problem.Difficulty.valueOf(req.getDifficulty()));
        p.setCategory(req.getCategory()); p.setTestCases(req.getTestCases());
        p.setExpectedOutput(req.getExpectedOutput());
        return problemRepository.save(p);
    }

    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) throw new RuntimeException("Problem not found: " + id);
        problemRepository.deleteById(id);
    }
}
