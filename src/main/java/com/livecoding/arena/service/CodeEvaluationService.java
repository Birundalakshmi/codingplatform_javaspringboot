package com.livecoding.arena.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livecoding.arena.entity.Problem;
import com.livecoding.arena.entity.Submission;
import com.livecoding.arena.entity.User;
import com.livecoding.arena.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

@Service
public class CodeEvaluationService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserService userService;

    private static final int TIMEOUT_SECONDS = 10;
    private static final ObjectMapper JSON = new ObjectMapper();

    // ── Public API ────────────────────────────────────────────────────────────

    public Map<String, Object> executeCode(String code, String language) {
        if (!"java".equalsIgnoreCase(language)) {
            return error("Only Java execution is supported.", false);
        }
        return runJava(code, null);
    }

    public Submission evaluateCode(User user, Problem problem, String code, String language) {
        Submission sub = new Submission(user, problem, code);
        sub.setLanguage(language);

        List<String> inputs  = parseJson(problem.getTestInputs());
        List<String> outputs = parseJson(problem.getExpectedOutput());

        // If this is an old problem with no structured test cases, just compile+run
        if (inputs.isEmpty() || outputs.isEmpty()) {
            Map<String, Object> runResult = runJava(code, null);
            boolean ok = Boolean.TRUE.equals(runResult.get("success"));
            sub.setStatus(ok ? Submission.Status.ACCEPTED : (Boolean.TRUE.equals(runResult.get("compilationError")) ? Submission.Status.COMPILATION_ERROR : Submission.Status.RUNTIME_ERROR));
            sub.setScore(ok ? 100 : 0);
            sub.setOutput(ok ? (String) runResult.getOrDefault("output", "") : (String) runResult.getOrDefault("error", ""));
            sub.setTestCasesPassed(ok ? 1 : 0);
            sub.setTotalTestCases(1);
            sub = submissionRepository.save(sub);
            if (ok) userService.updateUserScore(user, 100);
            return sub;
        }

        int total = Math.max(inputs.size(), outputs.size());
        sub.setTotalTestCases(total);

        // Compile once
        CompileResult compiled = compileJava(code);
        if (!compiled.success) {
            sub.setStatus(Submission.Status.COMPILATION_ERROR);
            sub.setScore(0);
            sub.setErrorMessage(compiled.error);
            sub.setOutput(compiled.error);
            sub.setTestCasesPassed(0);
            return submissionRepository.save(sub);
        }

        // Run against each test case
        int passed = 0;
        StringBuilder fullOutput = new StringBuilder();
        String firstError = null;
        Submission.Status status = Submission.Status.ACCEPTED;

        for (int i = 0; i < total; i++) {
            String input    = i < inputs.size()  ? inputs.get(i)  : "";
            String expected = i < outputs.size() ? outputs.get(i).trim() : "";

            Map<String, Object> run = runJava(code, input, compiled.tmpDir);
            boolean ok = Boolean.TRUE.equals(run.get("success"));
            String actual = ok ? ((String) run.getOrDefault("output", "")).trim() : "";
            String errMsg  = ok ? "" : (String) run.getOrDefault("error", "");

            boolean testPassed = ok && normalise(actual).equals(normalise(expected));
            if (testPassed) passed++;

            fullOutput.append("Test ").append(i + 1).append(": ")
                      .append(testPassed ? "PASS" : "FAIL")
                      .append(" | Input: ").append(input.replace("\n", "\\n"))
                      .append(" | Expected: ").append(expected)
                      .append(" | Got: ").append(ok ? actual : errMsg)
                      .append("\n");

            if (!ok && firstError == null) {
                firstError = errMsg;
                boolean tle = errMsg.contains("Time Limit");
                status = tle ? Submission.Status.TIME_LIMIT_EXCEEDED : Submission.Status.RUNTIME_ERROR;
            } else if (ok && !testPassed && status == Submission.Status.ACCEPTED) {
                status = Submission.Status.WRONG_ANSWER;
            }
        }

        // Clean up temp dir
        if (compiled.tmpDir != null) deleteTempDir(compiled.tmpDir);

        if (passed == total) status = Submission.Status.ACCEPTED;

        sub.setStatus(status);
        sub.setScore(passed == total ? 100 : 0);
        sub.setTestCasesPassed(passed);
        sub.setOutput(fullOutput.toString().trim());
        if (firstError != null) sub.setErrorMessage(firstError);

        sub = submissionRepository.save(sub);
        if (status == Submission.Status.ACCEPTED) userService.updateUserScore(user, 100);
        return sub;
    }

    public Map<String, Object> compileCode(String code, String language) {
        if (!"java".equalsIgnoreCase(language)) {
            return error("Only Java compilation is supported.", true);
        }
        CompileResult r = compileJava(code);
        if (r.success) {
            if (r.tmpDir != null) deleteTempDir(r.tmpDir);
            Map<String, Object> ok = new HashMap<>();
            ok.put("success", true);
            ok.put("message", "Compilation successful");
            return ok;
        }
        return error(r.error, true);
    }

    // ── Compile ───────────────────────────────────────────────────────────────

    private static class CompileResult {
        boolean success;
        String error;
        Path tmpDir;
    }

    private CompileResult compileJava(String code) {
        CompileResult r = new CompileResult();
        try {
            r.tmpDir = Files.createTempDirectory("livecoding_");
            String className = extractClassName(code);
            Path javaFile = r.tmpDir.resolve(className + ".java");
            Files.writeString(javaFile, code);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                r.success = false;
                r.error = "Compilation Error: Java compiler not available (ensure JDK is used, not JRE).";
                return r;
            }

            DiagnosticCollector<JavaFileObject> diag = new DiagnosticCollector<>();
            try (StandardJavaFileManager fm = compiler.getStandardFileManager(diag, null, null)) {
                Iterable<? extends JavaFileObject> units = fm.getJavaFileObjects(javaFile.toFile());
                boolean ok = compiler.getTask(null, fm, diag, null, null, units).call();
                if (!ok) {
                    String[] codeLines = code.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (Diagnostic<? extends JavaFileObject> d : diag.getDiagnostics()) {
                        if (d.getKind() == Diagnostic.Kind.ERROR) {
                            String msg = d.getMessage(null);
                            long line = d.getLineNumber();
                            long col  = d.getColumnNumber();
                            // Categorise the error type
                            String type;
                            if (msg.contains("';' expected") || msg.contains("illegal start") ||
                                msg.contains("reached end of file") || msg.contains("not a statement") ||
                                msg.contains("'}' expected") || msg.contains("'{' expected") ||
                                msg.contains("'(' expected") || msg.contains("')' expected") ||
                                msg.contains("illegal character") || msg.contains(".class expected")) {
                                type = "Syntax Error";
                            } else if (msg.contains("cannot find symbol") || msg.contains("package ") ||
                                       msg.contains("cannot be resolved")) {
                                type = "Undefined Symbol";
                            } else if (msg.contains("incompatible types") || msg.contains("bad operand") ||
                                       msg.contains("possible lossy conversion")) {
                                type = "Type Error";
                            } else if (msg.contains("method") && msg.contains("not found")) {
                                type = "Method Not Found";
                            } else if (msg.contains("is already defined") || msg.contains("duplicate")) {
                                type = "Duplicate Declaration";
                            } else if (msg.contains("missing return") || msg.contains("return outside")) {
                                type = "Missing Return";
                            } else if (msg.contains("unreachable statement")) {
                                type = "Unreachable Code";
                            } else if (msg.contains("variable") && msg.contains("not initialized")) {
                                type = "Uninitialized Variable";
                            } else {
                                type = "Compile Error";
                            }
                            sb.append("[Line ").append(line).append("] ").append(type).append(": ").append(msg).append("\n");
                            // Append the source line + caret pointer
                            if (line > 0 && line <= codeLines.length) {
                                String srcLine = codeLines[(int) line - 1];
                                sb.append("    ").append(srcLine).append("\n");
                                if (col > 0) {
                                    sb.append("    ");
                                    for (int c = 1; c < col; c++) sb.append(' ');
                                    sb.append("^\n");
                                }
                            }
                            sb.append("\n");
                        }
                    }
                    r.success = false;
                    r.error = sb.toString().trim();
                    deleteTempDir(r.tmpDir);
                    r.tmpDir = null;
                    return r;
                }
            }
            r.success = true;
        } catch (Exception e) {
            r.success = false;
            r.error = "Compilation Error: " + e.getMessage();
        }
        return r;
    }

    // ── Run ───────────────────────────────────────────────────────────────────

    /** Run without stdin — for the "Run Code" button */
    private Map<String, Object> runJava(String code, String stdin) {
        CompileResult compiled = compileJava(code);
        if (!compiled.success) {
            return error(compiled.error, true);
        }
        Map<String, Object> result = runJava(code, stdin, compiled.tmpDir);
        deleteTempDir(compiled.tmpDir);
        return result;
    }

    /** Run using an already-compiled tmpDir */
    private Map<String, Object> runJava(String code, String stdin, Path tmpDir) {
        Map<String, Object> result = new HashMap<>();
        try {
            String className = extractClassName(code);
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", tmpDir.toString(), className);
            pb.directory(tmpDir.toFile());
            pb.redirectErrorStream(false);
            Process process = pb.start();

            // Feed stdin
            if (stdin != null && !stdin.isEmpty()) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write((stdin + "\n").getBytes());
                    os.flush();
                }
            } else {
                process.getOutputStream().close();
            }

            Future<String> stdoutF = capture(process.getInputStream());
            Future<String> stderrF = capture(process.getErrorStream());

            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return error("Time Limit Exceeded: program ran longer than " + TIMEOUT_SECONDS + "s.", false);
            }

            String stdout = stdoutF.get(2, TimeUnit.SECONDS);
            String stderr  = stderrF.get(2, TimeUnit.SECONDS);

            if (process.exitValue() != 0 || !stderr.isBlank()) {
                String errMsg = (stderr.isBlank() ? stdout : stderr).trim();
                // Categorise runtime errors
                String errType = "Runtime Error";
                if (errMsg.contains("NullPointerException")) errType = "NullPointerException";
                else if (errMsg.contains("ArrayIndexOutOfBoundsException")) errType = "ArrayIndexOutOfBoundsException";
                else if (errMsg.contains("NumberFormatException")) errType = "NumberFormatException";
                else if (errMsg.contains("ArithmeticException")) errType = "ArithmeticException (divide by zero)";
                else if (errMsg.contains("ClassCastException")) errType = "ClassCastException";
                else if (errMsg.contains("StackOverflowError")) errType = "StackOverflowError (infinite recursion?)";
                else if (errMsg.contains("OutOfMemoryError")) errType = "OutOfMemoryError";
                else if (errMsg.contains("NoSuchElementException")) errType = "NoSuchElementException (input mismatch)";
                else if (errMsg.contains("InputMismatchException")) errType = "InputMismatchException";
                return error(errType + ":\n" + errMsg, false);
            }

            result.put("success", true);
            result.put("output", stdout.trim());
            result.put("executionTime", 0);

        } catch (Exception e) {
            return error("Execution Error: " + e.getMessage(), false);
        }
        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> error(String msg, boolean compilationError) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", false);
        r.put("error", msg);
        r.put("compilationError", compilationError);
        return r;
    }

    private String extractClassName(String code) {
        Matcher m = Pattern.compile("public\\s+class\\s+(\\w+)").matcher(code);
        return m.find() ? m.group(1) : "Solution";
    }

    @SuppressWarnings("unchecked")
    private List<String> parseJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return JSON.readValue(json, List.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Normalise output: trim, collapse whitespace for comparison */
    private String normalise(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\r\\n", "\n").replaceAll(" +", " ");
    }

    private Future<String> capture(InputStream is) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<String> f = exec.submit(() -> {
            try (BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line).append("\n");
                return sb.toString();
            }
        });
        exec.shutdown();
        return f;
    }

    private void deleteTempDir(Path dir) {
        if (dir == null) return;
        try {
            Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException ignored) {}
    }
}
