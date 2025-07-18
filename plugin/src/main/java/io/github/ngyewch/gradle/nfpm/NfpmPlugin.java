package io.github.ngyewch.gradle.nfpm;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/** nfpm plugin. */
public class NfpmPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    project.getExtensions().create("nfpm", NfpmExtension.class);
    project
        .getTasks()
        .register("nfpm", NfpmTask.class)
        .configure(
            task -> {
              if (project.getPluginManager().hasPlugin("application")) {
                task.dependsOn("installDist");
              }
            });
  }
}
