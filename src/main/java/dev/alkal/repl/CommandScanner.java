package dev.alkal.repl;

import dev.alkal.repl.annotation.Command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

class CommandScanner {
  public static List<CommandInfo> findCommandMethods(String basePackage) throws IOException, ClassNotFoundException {
    Set<Class<?>> classes = getClasses(basePackage);

    List<CommandInfo> commands = new ArrayList<>();
    for (Class<?> clazz : classes) {
      for (Method method : clazz.getDeclaredMethods()) {
        Command[] annotations = method.getAnnotationsByType(Command.class);
        for (Command replCommand : annotations) {
          int paramCount = method.getParameterCount();
          commands.add(new CommandInfo(
              method,
              replCommand.value().isEmpty() ? method.getName() : replCommand.value(),
              method.getParameterCount()));
        }
      }
    }
    return commands;
  }

  private static Set<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<>();
    String path = packageName.replace('.', '/');
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Enumeration<URL> resources = classLoader.getResources(path);

    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      if (resource.getProtocol().equals("file")) {
        classes.addAll(findClassesInDirectory(new File(resource.getFile()), packageName));
      } else if (resource.getProtocol().equals("jar")) {
        classes.addAll(findClassesInJar(resource, path));
      }
    }
    return classes;
  }

  private static Set<Class<?>> findClassesInDirectory(File directory, String packageName) throws ClassNotFoundException {
    Set<Class<?>> classes = new HashSet<>();
    if (!directory.exists()) return classes;

    for (File file : Objects.requireNonNull(directory.listFiles())) {
      if (file.isDirectory()) {
        classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
        classes.add(Class.forName(className));
      }
    }
    return classes;
  }

  private static Set<Class<?>> findClassesInJar(URL resource, String path) throws IOException {
    Set<Class<?>> classes = new HashSet<>();
    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
    try (JarFile jar = new JarFile(jarPath)) {
      jar.stream()
          .filter(e -> e.getName().startsWith(path) && e.getName().endsWith(".class"))
          .forEach(e -> {
            String className = e.getName().replace('/', '.').replace(".class", "");
            try {
              classes.add(Class.forName(className));
            } catch (Throwable ignored) {
            }
          });
    }
    return classes;
  }
}

