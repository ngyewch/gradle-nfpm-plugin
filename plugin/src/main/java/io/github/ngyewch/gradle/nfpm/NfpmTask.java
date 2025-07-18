package io.github.ngyewch.gradle.nfpm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskAction;

/** nfpm task. */
public abstract class NfpmTask extends DefaultTask {
  /**
   * Get the package name.
   *
   * @return package name.
   */
  @Input
  @Optional
  public abstract Property<String> getPackageName();

  /**
   * Get the list of packagers.
   *
   * @return list of packagers.
   */
  @Input
  @Optional
  public abstract ListProperty<String> getPackagers();

  /**
   * Task action.
   *
   * @throws IOException if an I/O exception occurred.
   */
  @TaskAction
  public void action() throws IOException {
    final Sync installDistTask = (Sync) getProject().getTasks().getByName("installDist");
    final NfpmExtension extension = getProject().getExtensions().getByType(NfpmExtension.class);
    final String packageName =
        getPackageName().orElse(extension.getPackageName()).orElse(getProject().getName()).get();
    List<String> packagers = getPackagers().get();
    if (packagers.isEmpty()) {
      packagers = extension.getPackagers().get();
    }
    if (packagers.isEmpty()) {
      packagers = List.of("deb", "rpm");
    }
    final Executor executor =
        DefaultExecutor.builder().setWorkingDirectory(getProject().getProjectDir()).get();
    final File outputDir =
        getProject().getLayout().getBuildDirectory().dir("nfpm").get().getAsFile();
    outputDir.mkdirs();
    final String arch = "all";
    final Map<String, String> env = new HashMap<>(System.getenv());
    env.put("NAME", packageName);
    env.put("VERSION", getProject().getVersion().toString());
    env.put("ARCH", arch);
    env.put("INSTALL_DIR", installDistTask.getDestinationDir().getPath());
    for (final String packager : packagers) {
      final File target =
          new File(
              outputDir,
              String.format("%s_%s_%s.%s", packageName, getProject().getVersion(), arch, packager));
      final CommandLine commandLine =
          new CommandLine("nfpm")
              .addArgument("package")
              .addArgument("--packager")
              .addArgument(packager)
              .addArgument("--target")
              .addArgument(target.getPath());
      executor.execute(commandLine, env);
    }
  }
}
