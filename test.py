#!/usr/bin/env python3
import subprocess
import minecraft_launcher_lib

options = {
    "username": "testuser",
    "uuid": "3e3d82b4-8491-3129-b43f-ffbc66844904",
    "token": "0",
    "launcherName": "mclauncher-cmd",
    "launcherVersion": "1.0",
    "demo": "false",
    "gameDirectory": "/srv/instance",
    "jvmArguments": ["-Xms300M", "-Xmx450M", "-XX:+UnlockExperimentalVMOptions", "-XX:+AlwaysPreTouch", "-XX"
                     ":+UseAdaptiveGCBoundary", "-XX:+UseGCOverheadLimit", "-XX:MaxHeapFreeRatio=80",
                     "-XX:MinHeapFreeRatio=40", "-XX:-UseG1GC", "-XX:+UseZGC", "-XX:+DisableExplicitGC",
                     "-XX:-UseParallelGC", "-XX:-UseParallelOldGC"],
    "resolutionWidth": "20",
    "resolutionHeight": "20",
}

command = minecraft_launcher_lib.command.get_minecraft_command("fabric-1.16", "/srv/minecraft", options)

subprocess.call(command)
