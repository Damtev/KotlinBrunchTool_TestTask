#!/usr/bin/env bash

mkdir -p out
javac -d out -cp .:lib/* $(find . -name "*.java")
java -cp out:lib/* org.junit.runner.JUnitCore Tester