package dev.alkal.repl;

import java.lang.reflect.Method;

final class CommandInfo {
  Method method;
  String command;
  int paramCount;

  CommandInfo(Method method, String command, int paramCount) {
    this.method = method;
    this.command = command;
    this.paramCount = paramCount;
  }
}

