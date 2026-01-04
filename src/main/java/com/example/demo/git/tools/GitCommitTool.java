package com.example.demo.git.tools;

import com.example.demo.git.GitCommandRunner;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitCommitTool {
    private final GitCommandRunner git;

    @McpTool(name = "my_commit", description = "git add -A then git commit -m <message>")
    public String gitCommit(@McpToolParam(description = "repo path") String repoPath,
                            @McpToolParam(description = "commit message") String message) {
        git.exec(repoPath, "add", "-A");
        var r = git.exec(repoPath, "commit", "-m", message);
        
        return r.ok() ? "OK" : ("FAIL: " + r.stderr());
    }
}
