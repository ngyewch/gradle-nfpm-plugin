package io.github.ngyewch.gradle.nfpm;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

/** nfpm task. */
public abstract class NfpmTask extends DefaultTask {
  /**
   * Get the archive base name.
   *
   * @return archive base name.
   */
  @Input
  @Optional
  public abstract Property<String> getArchiveBaseName();

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
    final NfpmExtension extension = getProject().getExtensions().getByType(NfpmExtension.class);
    final String archiveBaseName =
        getArchiveBaseName()
            .orElse(extension.getArchiveBaseName())
            .orElse(getProject().getName())
            .get();
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
    for (final String packager : packagers) {
      final File target =
          new File(
              outputDir,
              String.format("%s_%s_all.%s", archiveBaseName, getProject().getVersion(), packager));
      final CommandLine commandLine =
          new CommandLine("nfpm")
              .addArgument("package")
              .addArgument("--packager")
              .addArgument(packager)
              .addArgument("--target")
              .addArgument(target.getPath());
      executor.execute(commandLine);
    }
  }
}
