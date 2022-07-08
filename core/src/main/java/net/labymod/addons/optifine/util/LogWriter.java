package net.labymod.addons.optifine.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LogWriter {

  public static final LogWriter INSTANCE = new LogWriter();
  private final StringBuilder builder;

  private LogWriter() {
    this.builder = new StringBuilder();
  }

  public StringBuilder builder() {
    return this.builder;
  }

  public void write(Path path) throws IOException {
    Files.write(path, this.builder.toString().getBytes(StandardCharsets.UTF_8));
    this.builder.setLength(0);
  }

}
