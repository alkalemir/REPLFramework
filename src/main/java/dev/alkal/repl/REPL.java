package dev.alkal.repl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

public final class REPL {
  static List<CommandInfo> commandInfos;
  static REPLConfig config;

  public static void start(Class<?> clazz, REPLConfig config) {
    REPL.config = config;
    try {
      commandInfos = findCommands(clazz);
      run();
    } catch (ClassNotFoundException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void run() {
    for (;;) {
      printPrompt();
      var args = parseLine();
      var cmdInfo = findCommandByName(args[0]);
      if (cmdInfo == null) {
        printInvalidCommandErrorMessage();
        continue;
      }
      var methodArgs = findMethodArgs(args);
      if (methodArgs.length != cmdInfo.paramCount) {
        printWrongNumberOfArgumentErrorMessage();
        continue;
      }
      if (methodArgs.length == 0) {
        invokeMethod(cmdInfo.method);
        continue;
      }
      invokeMethodWithParams(cmdInfo.method, methodArgs);
    }
  }

  private static String[] findMethodArgs(String[] args) {
    String[] methodArgs = new String[args.length - 1];
    System.arraycopy(args, 1, methodArgs, 0, args.length - 1);

    return methodArgs;
  }

  private static void invokeMethodWithParams(Method method, String[] args) {
    try {
      method.setAccessible(true);
      method.invoke(null, (Object[])args);
      method.setAccessible(false);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static void invokeMethod(Method method) {
    try {
      method.setAccessible(true);
      method.invoke(null);
      method.setAccessible(false);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static void printWrongNumberOfArgumentErrorMessage() {
    System.err.println(config.wrongNumberOfArgumentErrorMessage);
  }

  private static void printInvalidCommandErrorMessage() {
    System.err.println(config.invalidCommandErrorMessage);
  }

  private static void printPrompt() {
    System.out.print(config.prompt + config.suffix);
  }

  private static String[] parseLine() {
    Scanner kb = new Scanner(System.in);
    return kb.nextLine().split("[ \t]");
  }

  private static CommandInfo findCommandByName(String name) {
    for (var command: commandInfos) {
      if (command.command.equals(name)) {
        return command;
      }
    }
    return null;
  }

  private static List<CommandInfo> findCommands(Class<?> clazz) throws IOException, ClassNotFoundException {
    return CommandScanner.findCommandMethods(clazz.getPackageName());
  }
}
