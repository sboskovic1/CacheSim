JFLAGS = -g
JC = javac
.SUFFIXES: .java .class

build: Main.java
	$(JC) $(JFLAGS) *.java

clean:
	$(RM) *.class