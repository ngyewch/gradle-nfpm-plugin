package io.github.ngyewch.gradle.nfpm;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public abstract class NfpmExtension {
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
}
