package com.example.demo.git.dto;

public record GitExecResult(boolean ok, int exitCode, String stdout, String stderr) {

    public static GitExecResult fail(int exitCode, String stderr){
        return new GitExecResult(false, exitCode, "", stderr);
    }

    public static GitExecResult of(boolean ok, int exitCode, String stdout, String stderr){
        return new GitExecResult(ok, exitCode, stdout, stderr);
    }
}

