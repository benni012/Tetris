@echo off
cd source
javac -cp ../library/* -d ../classes eu/misselwitz/Tetris/*.java
cd ..
pause