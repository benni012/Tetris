@echo off
cd source
/Tools/jdk/bin/javac -cp ../library/* -d ../classes eu/misselwitz/Tetris/*.java
cd ..
pause
