package org.xsx;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class InnoSetupTask extends DefaultTask {

    private String appName;
    private String appVersion;
    private String appId;
    private String appIcon;
    private String srcExeFiltPath;
    private String srcLibPath;
    private String srcJrePath;
    private String replaceSetup;
    private String appPublisher;
    private String appPublisherUrl;


    private String isccPath = "iscc.exe"; // 可默认使用环境变量中的 iscc

    @Input
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Input
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Input
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Input
    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    @Input
    public String getReplaceSetup() {
        return replaceSetup;
    }

    public void setReplaceSetup(String replaceSetup) {
        this.replaceSetup = replaceSetup;
    }

    @Input
    public String getAppPublisher() {
        return appPublisher;
    }

    public void setAppPublisher(String appPublisher) {
        this.appPublisher = appPublisher;
    }

    @Input
    public String getAppPublisherUrl() {
        return appPublisherUrl;
    }

    public void setAppPublisherUrl(String appPublisherUrl) {
        this.appPublisherUrl = appPublisherUrl;
    }

    @Input
    public String getSrcExeFiltPath() {
        return srcExeFiltPath;
    }

    public void setSrcExeFiltPath(String srcExeFiltPath) {
        this.srcExeFiltPath = srcExeFiltPath;
    }

    @Input
    public String getSrcLibPath() {
        return srcLibPath;
    }

    public void setSrcLibPath(String srcLibPath) {
        this.srcLibPath = srcLibPath;
    }

    @Input
    public String getSrcJrePath() {
        return srcJrePath;
    }

    public void setSrcJrePath(String srcJrePath) {
        this.srcJrePath = srcJrePath;
    }

    @Input
    public String getIsccPath() {
        return isccPath;
    }

    public void setIsccPath(String isccPath) {
        this.isccPath = isccPath;
    }


    @TaskAction
    public void buildInstaller() throws IOException, InterruptedException {
        Path tempDir = getProject().getBuildDir().toPath().resolve("inno-setup");
        Files.createDirectories(tempDir);

        Path issFile = tempDir.resolve(appName + ".iss");
        Path outputExe = tempDir.resolve(appName + "_setup.exe");

        // 1. 写入 .iss 脚本内容
        String iss_content = generate_iss_content();
        Files.write(issFile, iss_content.getBytes(Charset.forName("gbk")));

        getLogger().lifecycle("生成 ISS 脚本: " + issFile.toAbsolutePath());

        // 2. 调用 ISCC 编译器
        ProcessBuilder pb = new ProcessBuilder(isccPath, issFile.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                getLogger().lifecycle(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            getLogger().lifecycle("安装包创建成功: " + outputExe.toAbsolutePath());
        } else {
            throw new RuntimeException("Inno Setup 编译失败，退出码: " + exitCode);
        }
    }

    public String generate_iss_content() throws IOException {
        try {
            InputStream inputStream = InnoSetupTask.class.getResourceAsStream("/setup_template.iss");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("gbk")));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

            String issContent = sb.toString()
                    .replace("{{appName}}", String.format("\"%s\"", appName))
                    .replace("{{appVersion}}", String.format("\"%s\"", appVersion))
                    .replace("{{appId}}", String.format("\"%s\"", appId))
                    .replace("{{appIcon}}", String.format("\"%s\"", appIcon))
                    .replace("{{srcExeFiltPath}}", String.format("\"%s\"", srcExeFiltPath))
                    .replace("{{srcLibPath}}", String.format("\"%s\"", srcLibPath))
                    .replace("{{srcJrePath}}", String.format("\"%s\"", srcJrePath))
                    .replace("{{replaceSetup}}", String.format("\"%s\"", replaceSetup))
                    .replace("{{appPublisher}}", String.format("\"%s\"", appPublisher))
                    .replace("{{appPublisherUrl}}", String.format("\"%s\"", appPublisherUrl));


            getLogger().lifecycle("appName: " + appName);
            getLogger().lifecycle("appVersion: " + appVersion);
            getLogger().lifecycle("appId: " + appId);
            getLogger().lifecycle("appIcon: " + appIcon);
            getLogger().lifecycle("srcExeFiltPath: " + srcExeFiltPath);
            getLogger().lifecycle("srcLibPath: " + srcLibPath);
            getLogger().lifecycle("srcJrePath: " + srcJrePath);
            getLogger().lifecycle("replaceSetup: " + replaceSetup);
            getLogger().lifecycle("appPublisher: " + appPublisher);
            getLogger().lifecycle("appPublisherUrl: " + appPublisherUrl);
            getLogger().lifecycle("isccPath: " + isccPath);

            return issContent;

        } catch (Exception e) {
            throw new IOException("读取模板文件失败", e);
        }

    }

}
