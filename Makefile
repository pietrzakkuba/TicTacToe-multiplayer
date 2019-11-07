all:
	cd server; gcc server.c -pthread -Wall -o server.x
	cd src; javac *.java
	
	
