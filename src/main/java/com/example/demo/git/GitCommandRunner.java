package com.example.demo.git;

import com.example.demo.git.dto.GitExecResult;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class GitCommandRunner {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    public GitExecResult exec(String repoPath, String... gitArgs) {
        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        cmd.addAll(List.of(gitArgs));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(repoPath));

        try {
            Process p = pb.start();

            boolean finished = p.waitFor(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                p.destroyForcibly();
                return GitExecResult.fail(-1, "TIMEOUT");
            }

            String out = readAll(p.getInputStream());
            String err = readAll(p.getErrorStream());
            int code = p.exitValue();

            return GitExecResult.of(code == 0, code, out, err);
        } catch (Exception e) {
            return GitExecResult.fail(-2, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Allow forcing author info via -c user.name / -c user.email for commit-related commands.
    public GitExecResult execWithAuthor(String repoPath, String authorName, String authorEmail, String... gitArgs) {
        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        // Inject inline config for author/committer info.
        cmd.add("-c");
        cmd.add("user.name=" + authorName);
        cmd.add("-c");
        cmd.add("user.email=" + authorEmail);
        // These environment variables can also be used by git; we keep it simple with -c here.
        cmd.addAll(List.of(gitArgs));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(repoPath));

        try {
            Process p = pb.start();

            boolean finished = p.waitFor(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                p.destroyForcibly();
                return GitExecResult.fail(-1, "TIMEOUT");
            }

            String out = readAll(p.getInputStream());
            String err = readAll(p.getErrorStream());
            int code = p.exitValue();

            return GitExecResult.of(code == 0, code, out, err);
        } catch (Exception e) {
            return GitExecResult.fail(-2, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private String readAll(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }
}
