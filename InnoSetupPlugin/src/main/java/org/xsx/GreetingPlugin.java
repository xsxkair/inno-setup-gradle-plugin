package org.xsx;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GreetingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // 注册一个扩展，允许在build.gradle中配置插件
        project.getExtensions().create("myPluginConfig", MyPluginExtension.class);

        // 创建一个自定义任务
        project.getTasks().register("helloFromPlugin", HelloTask.class, task -> {
            // 可以在这里配置任务的默认值
            task.setMessage("Hello from my plugin!");
        });

        project.getTasks().register("innoSetup", InnoSetupTask.class, task -> {
        });
    }
}
