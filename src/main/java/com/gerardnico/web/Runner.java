package com.gerardnico.web;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class Runner {


  private static final String WORKING_DIRECTORY = "src/main/resources/";

  public static void runClusteredExample(Class clazz) {
    runExample(WORKING_DIRECTORY, clazz, new VertxOptions(), null, true);
  }

  public static void runExample(Class clazz) {
    runExample(WORKING_DIRECTORY, clazz, new VertxOptions(), null, false);
  }

  public static void runExample(Class clazz, DeploymentOptions options) {
    runExample(WORKING_DIRECTORY, clazz, new VertxOptions(), options, false);
  }

  public static void runExample(String workingDirectory, Class clazz, VertxOptions options, DeploymentOptions
    deploymentOptions, boolean clustered) {
    runExample(workingDirectory, clazz.getName(), options, deploymentOptions, clustered);
  }

  /**
   *
   * @param workingDirectory - to find the webroot
   * @param verticleId
   * @param options
   * @param deploymentOptions
   * @param clustered
   */
  public static void runExample(String workingDirectory, String verticleId, VertxOptions options, DeploymentOptions deploymentOptions, boolean clustered) {
    if (options == null) {
      // Default parameter
      options = new VertxOptions();
    }
    // Smart cwd detection

    // Based on the current directory (.) and the desired directory (exampleDir), we try to compute the vertx.cwd
    // directory:
    try {
      // We need to use the canonical file. Without the file name is .
      File current = new File(".").getCanonicalFile();
      if (workingDirectory.startsWith(current.getName()) && !workingDirectory.equals(current.getName())) {
        workingDirectory = workingDirectory.substring(current.getName().length() + 1);
      }
    } catch (IOException e) {
      // Ignore it.
    }


    System.setProperty("vertx.cwd", Paths.get(workingDirectory).toAbsolutePath().toString());
    Consumer<Vertx> runner = vertx -> {
      try {
        if (deploymentOptions != null) {
          vertx.deployVerticle(verticleId, deploymentOptions);
        } else {
          vertx.deployVerticle(verticleId);
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    };
    if (clustered) {
      Vertx.clusteredVertx(options, res -> {
        if (res.succeeded()) {
          Vertx vertx = res.result();
          runner.accept(vertx);
        } else {
          res.cause().printStackTrace();
        }
      });
    } else {
      Vertx vertx = Vertx.vertx(options);
      runner.accept(vertx);
    }
  }
}
