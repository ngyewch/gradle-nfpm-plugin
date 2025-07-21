package io.github.ngyewch.gradle.nfpm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.file.Directory;
import org.gradle.api.internal.provider.Providers;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOutput;
import org.gradle.process.ExecResult;

/** nfpm task. */
public abstract class NfpmTask extends DefaultTask {
  private final Provider<String> projectName = Providers.of(getProject().getName());

  private final Provider<String> projectVersion =
      Providers.of(getProject().getVersion().toString());

  private final Provider<File> projectDir = Providers.of(getProject().getProjectDir());

  private final Provider<TaskContainer> taskContainer = Providers.of(getProject().getTasks());

  private final ProviderFactory providerFactory = getProject().getProviders();

  private final NfpmExtension extension =
      getProject().getExtensions().getByType(NfpmExtension.class);

  private final Provider<Directory> outputDir =
      getProject().getLayout().getBuildDirectory().dir("nfpm");

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
    final String packageName =
        getPackageName().orElse(extension.getPackageName()).orElse(projectName).get();
    final String packageVersion = projectVersion.get();
    List<String> packagers = getPackagers().get();
    if (packagers.isEmpty()) {
      packagers = extension.getPackagers().get();
    }
    if (packagers.isEmpty()) {
      packagers = List.of("deb", "rpm");
    }
    outputDir.get().getAsFile().mkdirs();
    final String arch = "all";
    final Map<String, String> env = new HashMap<>(System.getenv());
    env.put("NAME", packageName);
    env.put("VERSION", packageVersion);
    env.put("ARCH", arch);
    final Sync installDistTask = getInstallDistTask();
    if (installDistTask != null) {
      env.put("INSTALL_DIR", installDistTask.getDestinationDir().getPath());
    }
    for (final String packager : packagers) {
      final File target =
          outputDir
              .get()
              .file(String.format("%s_%s_%s.%s", packageName, packageVersion, arch, packager))
              .getAsFile();
      final ExecOutput execOutput =
          providerFactory.exec(
              execSpec ->
                  execSpec
                      .commandLine(
                          "nfpm", "package", "--packager", packager, "--target", target.getPath())
                      .setIgnoreExitValue(true)
                      .environment(env)
                      .workingDir(projectDir.get()));
      final ExecResult execResult = execOutput.getResult().get();
      System.out.println(execOutput.getStandardOutput().getAsText().get());
      System.out.println(execOutput.getStandardError().getAsText().get());
      if (execResult.getExitValue() != 0) {
        throw new GradleException("nfpm exit code: " + execResult.getExitValue());
      }
    }
  }

  private Sync getInstallDistTask() {
    try {
      return (Sync) taskContainer.get().getByName("installDist");
    } catch (UnknownTaskException e) {
      return null;
    }
  }
}
