// Se poate rula din CLI folosind:
// java -cp ".:/usr/lib/antlr-4.8-complete.jar" org.antlr.v4.gui.TestRig g r -tree

grammar g;
    r: 'Hello 'Name;
    Name: [a-zA-Z]+;
