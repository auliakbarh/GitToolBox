package zielu.junit5.intellij.extension.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

class TextResourceImpl implements TextResource {
  private final ResourcePath resourcePath;

  TextResourceImpl(ResourcePath resourcePath) {
    this.resourcePath = resourcePath;
  }

  @Override
  public List<String> getLines() {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream(resourcePath.value()), StandardCharsets.UTF_8))) {
      return reader.lines().collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to load " + resourcePath.value(), e);
    }
  }
}
