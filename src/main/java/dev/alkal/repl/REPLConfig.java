package dev.alkal.repl;

public class REPLConfig {
  final String prompt;
  final String suffix;
  final String invalidCommandErrorMessage;
  final String wrongNumberOfArgumentErrorMessage;

  private REPLConfig(String prompt, String suffix, String invalidCommandErrorMessage, String wrongNumberOfArgumentErrorMessage) {
    this.prompt = prompt;
    this.suffix = suffix;
    this.invalidCommandErrorMessage = invalidCommandErrorMessage;
    this.wrongNumberOfArgumentErrorMessage = wrongNumberOfArgumentErrorMessage;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String prompt;
    private String suffix;
    private String invalidCommandErrorMessage;
    String wrongNumberOfArgumentErrorMessage;

    private Builder() {}

    public Builder setPrompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    public Builder setSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    public Builder setInvalidCommandErrorMessage(String invalidCommandErrorMessage) {
      this.invalidCommandErrorMessage = invalidCommandErrorMessage;
      return this;
    }

    public Builder setWrongNumberOfArgumentErrorMessage(String wrongNumberOfArgumentErrorMessage) {
      this.wrongNumberOfArgumentErrorMessage = wrongNumberOfArgumentErrorMessage;
      return this;
    }



    public REPLConfig build() {
      return new REPLConfig(prompt, suffix, invalidCommandErrorMessage, wrongNumberOfArgumentErrorMessage);
    }
  }
}
