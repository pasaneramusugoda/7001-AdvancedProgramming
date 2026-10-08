#!/bin/bash

# ==============================================================================
# IWFC Simulation Bot Launcher
# Runs the automated end-to-end demonstration covering all actors and scenarios.
#
# Usage:
#   ./run-bot.sh          (Default: realistic interactive simulation pace)
#   ./run-bot.sh --fast   (High-speed instant verification)
#   ./run-bot.sh --slow   (Presentation pace for video recording / viva walkthrough)
#   ./run-bot.sh --slow --read=5000 --typing=150   (Override: read pause / per-char typing / --step=N)
# ==============================================================================

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home

if [ ! -d "target/classes" ]; then
    echo "Compiling IWFC prototype..."
    /opt/homebrew/bin/mvn compile -q
fi

$JAVA_HOME/bin/java -cp target/classes com.iwfc.bot.DemoBot "$@"
