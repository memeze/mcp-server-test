package com.example.demo.git.tools;

import com.example.demo.git.GitCommandRunner;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitPushTool {
    private final GitCommandRunner git;

    @McpTool(name = "my_push", description = "git push origin <branch>")
    public String gitPush(@McpToolParam(description = "repo path") String repoPath) {
        var branch = git.exec(repoPath, "rev-parse", "--abbrev-ref", "HEAD").stdout().trim();
        var r = git.exec(repoPath, "push", "origin", branch);

        return r.ok() ? "OK" : ("FAIL: " + r.stderr());
    }
}