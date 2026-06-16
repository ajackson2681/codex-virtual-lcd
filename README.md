# JavaFX virtual LCD for Codex Development

This is purely a development tool so that I could test gap buffer movements and whatnot without having to constantly flash the actual hardware. It is basically just a 1:1 port of the gap buffer implementation from the [Codex Project](https://github.com/ajackson2681/codex).

There are a few differences, like how it listens to udpates and draws to the LCD. In the actual Codex, it checks for a stale flag in the main loop. For this, it looks at a BooleanProperty and looks for when it changes. Same basic idea, but slightly different implementation based on how JavaFX works vs an embedded project.

## To Run

You'll need maven, and a JDK. This project was created against JDK 21. To run it, just execute:

`mvn javafx:run`.

To build it as a runnable jar do:

`mvn package`