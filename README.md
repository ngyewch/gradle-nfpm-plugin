# gradle-nfpm-plugin

Gradle plugin for [nfpm](https://nfpm.goreleaser.com/).

NOTE: `nfpm` should be available on the `PATH`.

## Installation

See https://plugins.gradle.org/plugin/io.github.ngyewch.nfpm

## Example usage

See https://github.com/ngyewch/gradle-nfpm-plugin/tree/main/test

## Tasks

### `nfpm` - `io.github.ngyewch.gradle.nfpm.NfpmTask`

* Performs `nfpm` packaging.
* Depends on: `installDist` (if `application` plugin is available).

| Name          | Type           | Required | Default           | Description   |
|---------------|----------------|----------|-------------------|---------------|
| `packageName` | `String`       | N        | `${project.name}` | Package name. |
| `packagers`   | `List<String>` | N        | `["deb", "rpm"]`  | Packagers.    | 

#### Environment variables passed to `nfpm`

| Name          | Description                                         |
|---------------|-----------------------------------------------------|
| `NAME`        | Package name.                                       |
| `VERSION`     | Package version.                                    |
| `ARCH`        | Architecture.                                       |
| `INSTALL_DIR` | Install dir (if `application` plugin is available). |

## Extension

### `nfpm`

| Name          | Type           | Required | Default           | Description   |
|---------------|----------------|----------|-------------------|---------------|
| `packageName` | `String`       | N        | `${project.name}` | Package name. |
| `packagers`   | `List<String>` | N        | `["deb", "rpm"]`  | Packagers.    | 

