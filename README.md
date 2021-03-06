[![Discord](https://img.shields.io/discord/745728805678874800?logo=discord)](https://discord.gg/MwBvhEz)
[![build](https://github.com/wnuke-dev/mc-http-api/workflows/Java%20CI%20with%20Gradle/badge.svg)]((https://github.com/wnuke-dev/mc-http-api/actions?query=workflow%3A%22Java%20CI%20with%20Gradle%22))
# MC HTTP API

An HTTP API for controlling Minecraft, also cancels most render/resource loading methods. Designed for use in the Docker MC project

## Setup for developers


Get access to the repo by asking wnuke (wnuke#1010 on Discord). Download the source code:

 - `git clone git@github.com:wnuke-dev/mc-http-api`

If you do not have an IDE you should download one, I recommend [IntelliJ IDEA CE](https://www.jetbrains.com/idea/).

Import the project to your IDE of choice:

 - `cd mc-http-api`
 - `./gradlew genSources`
 - `./gradlew openIdea` for IntelliJ IDEA
 - `./gradlew eclipse` for Eclipse
