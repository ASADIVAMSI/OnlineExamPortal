package com.kspiders.app.onlineexamportal.configuration;

import com.kspiders.app.onlineexamportal.dao.QuestionRepository;
import com.kspiders.app.onlineexamportal.dao.QuestionSetRepository;
import com.kspiders.app.onlineexamportal.entity.Question;
import com.kspiders.app.onlineexamportal.entity.QuestionSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class that seeds 120 Core Java questions (30 per set across 4 sets) into the database on application startup.
 */
@Configuration
public class QuestionSeedConfiguration {

    /**
     * DTO Record encapsulating raw seed data for a single assessment question.
     */
    public record QuestionData(String questionText, String optionA, String optionB, String optionC, String optionD, String questionType, String correctOption) {}

    /**
     * Executes seeding logic for all 4 Question Sets after Spring Boot initializes.
     *
     * @param setRepository      QuestionSet entity repository interface.
     * @param questionRepository Question entity repository interface.
     * @return CommandLineRunner startup bean.
     */
    @Bean
    CommandLineRunner seedQuestions(QuestionSetRepository setRepository, QuestionRepository questionRepository) {
        return args -> {
            // Seed 30 questions into Question Set 1
            seedSet(setRepository, questionRepository, "Question Set 1", getSet1Questions());
            // Seed 30 questions into Question Set 2
            seedSet(setRepository, questionRepository, "Question Set 2", getSet2Questions());
            // Seed 30 questions into Question Set 3
            seedSet(setRepository, questionRepository, "Question Set 3", getSet3Questions());
            // Seed 30 questions into Question Set 4
            seedSet(setRepository, questionRepository, "Question Set 4", getSet4Questions());
        };
    }

    private void seedSet(QuestionSetRepository setRepository, QuestionRepository questionRepository, String setPrefix, List<QuestionData> questionsData) {
        setRepository.findAll().stream()
            .filter(s -> s.getName().startsWith(setPrefix))
            .findFirst()
            .ifPresent(set -> {
                List<Question> existing = questionRepository.findByQuestionSetIdOrderById(set.getId());
                for (int i = 0; i < questionsData.size(); i++) {
                    QuestionData data = questionsData.get(i);
                    if (i < existing.size()) {
                        Question q = existing.get(i);
                        q.setQuestionText(data.questionText());
                        q.setOptionA(data.optionA());
                        q.setOptionB(data.optionB());
                        q.setOptionC(data.optionC());
                        q.setOptionD(data.optionD());
                        q.setQuestionType(data.questionType());
                        q.setCorrectOption(data.correctOption());
                        questionRepository.save(q);
                    } else {
                        Question q = new Question(set, data.questionText(), data.optionA(), data.optionB(), data.optionC(), data.optionD(), data.questionType());
                        q.setCorrectOption(data.correctOption());
                        questionRepository.save(q);
                    }
                }
            });
    }

    private List<QuestionData> getSet1Questions() {
        List<QuestionData> list = new ArrayList<>();
        // MCQs (1-15)
        list.add(new QuestionData("Which component of Java is responsible for executing compiled bytecode?", "JDK", "JVM", "JRE", "JDB", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the default value of an uninitialized instance variable of type int in Java?", "null", "undefined", "0", "1", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which of the following is a valid variable identifier name in Java?", "2total", "_totalAmount", "class", "first-name", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the memory size of a double primitive data type in Java?", "4 bytes", "8 bytes", "2 bytes", "16 bytes", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which operator is used for Bitwise AND operation in Java?", "&&", "&", "|", "^", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword is used to terminate a loop prematurely in Java?", "exit", "stop", "break", "return", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("What is the result of the integer division 17 / 4 in Java?", "4.25", "4", "4.0", "5", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword is used to declare a constant variable in Java?", "const", "static", "final", "immutable", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("What is the correct single-line comment syntax in Java?", "# comment", "<!-- comment -->", "// comment", "/* comment */", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which keyword is used for a method that does not return any value?", "empty", "null", "void", "zero", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which primitive data type stores true or false values in Java?", "bool", "boolean", "bit", "flag", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which loop in Java guarantees at least one execution of the loop body?", "for loop", "while loop", "do-while loop", "for-each loop", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("What is the evaluation result of (10 > 5 && 3 < 1) in Java?", "true", "false", "1", "compilation error", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which type conversion requires explicit narrowing (type casting) in Java?", "int to double", "double to int", "byte to int", "short to long", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What fundamental feature makes Java platform independent?", "Direct compilation to machine code", "Bytecode execution via JVM", "Pointer manipulation", "Multiple inheritance of classes", "MULTIPLE_CHOICE", "B"));

        // Theory (16-23)
        list.add(new QuestionData("Theory: What is the primary difference between JDK and JRE in Java development?", "JDK contains tools to compile Java code; JRE only provides runtime environment to execute bytecode.", "JRE contains javac compiler; JDK only contains JVM.", "JDK is platform independent; JRE is platform dependent.", "JDK and JRE are identical terms.", "THEORY", "A"));
        list.add(new QuestionData("Theory: How does Java achieve 'Write Once, Run Anywhere' (WORA) capability?", "By compiling code directly to OS-specific binary format.", "By compiling source code into platform-neutral Bytecode (.class) executed by OS-specific JVMs.", "By interpreting Java code line by line without compilation.", "By using native C++ libraries.", "THEORY", "B"));
        list.add(new QuestionData("Theory: What distinguishes primitive data types from reference data types in Java?", "Primitives store actual values on the stack; reference types store memory addresses of objects on the heap.", "Reference types store values on stack; primitives store on heap.", "Primitives have default methods; reference types do not.", "Reference types cannot be null; primitives can be null.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the functional difference between break and continue statements in loops?", "break skips current iteration; continue exits loop entirely.", "break exits the loop entirely; continue skips rest of current iteration and moves to next.", "Both break and continue exit the loop immediately.", "break pauses loop execution; continue terminates program.", "THEORY", "B"));
        list.add(new QuestionData("Theory: What is widening type casting (implicit casting) in Java?", "Converting a larger data type to a smaller data type automatically.", "Converting a smaller data type to a larger data type automatically without data loss.", "Converting a String to an integer using Integer.parseInt().", "Casting an object to a subclass.", "THEORY", "B"));
        list.add(new QuestionData("Theory: Why must local variables in Java methods be initialized before use?", "Local variables receive default values automatically from JVM.", "Local variables are allocated on stack and Java does not assign default values to local variables.", "Local variables are final by default.", "Local variables are stored in method area.", "THEORY", "B"));
        list.add(new QuestionData("Theory: What is the main benefit of using a switch statement over multiple if-else blocks?", "switch works with float and double data types.", "switch improves code readability and performance when evaluating a discrete variable against constant values.", "switch executes all cases by default without break.", "switch allows dynamic boolean expressions in case labels.", "THEORY", "B"));
        list.add(new QuestionData("Theory: Why is the main method in Java declared as public static void?", "public allows access from anywhere; static allows JVM invocation without object instantiation; void means no return value.", "public makes it hidden; static allows return values; void makes it abstract.", "public restricts JVM access; static makes it private; void is optional.", "It is just a syntax rule with no technical purpose.", "THEORY", "A"));

        // Coding (24-30)
        list.add(new QuestionData("Coding: What is the output of the Java code: int a = 5; int b = a++; System.out.println(a + \" \" + b);?", "5 5", "6 5", "6 6", "5 6", "CODING", "B"));
        list.add(new QuestionData("Coding: What will be printed by: int x = 10, y = 20; System.out.println(x > y ? \"X\" : \"Y\");?", "X", "Y", "10", "20", "CODING", "B"));
        list.add(new QuestionData("Coding: Which code snippet correctly checks if an integer num is even?", "if (num / 2 == 0)", "if (num % 2 == 0)", "if (num % 2 == 1)", "if (num & 2 == 0)", "CODING", "B"));
        list.add(new QuestionData("Coding: What is the output of: for(int i = 1; i <= 3; i++) { System.print(i * 2 + \" \"); }?", "1 2 3", "2 4 6", "2 4 6 8", "0 2 4", "CODING", "B"));
        list.add(new QuestionData("Coding: What is the result of: int sum = 0; for(int i = 1; i <= 4; i++) { sum += i; } System.out.println(sum);?", "10", "4", "15", "6", "CODING", "A"));
        list.add(new QuestionData("Coding: What is the output of: int n = 5; while(n > 3) { System.out.print(n + \" \"); n--; }?", "5 4 3", "5 4", "4 3", "5 4 3 2 1", "CODING", "B"));
        list.add(new QuestionData("Coding: Which code snippet correctly swaps the values of two variables a and b using a temporary variable temp?", "temp = a; a = b; b = temp;", "a = b; temp = a; b = temp;", "temp = b; b = a; a = temp;", "b = temp; temp = a; a = b;", "CODING", "A"));

        return list;
    }

    private List<QuestionData> getSet2Questions() {
        List<QuestionData> list = new ArrayList<>();
        // MCQs (1-15)
        list.add(new QuestionData("What is the default value of array elements in an uninitialized int[] array in Java?", "null", "-1", "0", "undefined", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("What is the index of the first element in any Java array?", "-1", "0", "1", "Depends on array length", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which property is used to obtain the total number of elements in a Java array?", "length()", "size()", "length", "capacity()", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which method of the String class returns the character at a specified index?", "getChar(int index)", "charAt(int index)", "indexOf(char c)", "substring(int index)", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What does String immutability mean in Java?", "String variables cannot change their reference.", "String objects once created in memory cannot be modified.", "String methods always modify the original string object.", "String objects are stored on the stack.", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the return type of a Java constructor?", "void", "int", "Class object type", "Constructors have no return type, not even void.", "MULTIPLE_CHOICE", "D"));
        list.add(new QuestionData("Which keyword is used to instantiate an object of a class in Java?", "create", "alloc", "new", "construct", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which keyword refers to the current invoking object instance inside a method or constructor?", "super", "this", "self", "parent", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What happens if a developer does not write any constructor in a Java class?", "Compilation error occurs.", "Java compiler automatically provides a default no-argument constructor.", "Objects of the class cannot be created.", "RuntimeException is thrown at application launch.", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which method should be used to compare the actual character contents of two String objects?", "==", "equals()", "compareTo()", "equalsIgnoreCase()", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which statement correctly initializes a 1D array of integers with size 5?", "int arr = new int[5];", "int[] arr = new int[5];", "int arr(5) = new int[];", "array int arr = new int[5];", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What does the String.length() method return for the string \"CoreJava\"?", "7", "8", "9", "0", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Where in memory are objects created using the new keyword stored in Java?", "Stack Memory", "Heap Memory", "Program Counter Register", "Native Method Stack", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the output of \"Hello\".toUpperCase() in Java?", "hello", "HELLO", "Hello", "H", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which exception is thrown when attempting to access an invalid index of an array?", "NullPointerException", "ClassCastException", "ArrayIndexOutOfBoundsException", "IllegalArgumentException", "MULTIPLE_CHOICE", "C"));

        // Theory (16-23)
        list.add(new QuestionData("Theory: What is the key difference between Heap memory and Stack memory in Java?", "Stack stores primitive values and object reference variables; Heap stores actual object instances.", "Heap stores local variables; Stack stores global objects.", "Stack memory is dynamic and shared; Heap is private to each thread.", "Heap memory is faster than Stack memory.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Difference between == operator and .equals() method when comparing two String objects in Java?", "== checks if both references point to same memory address; .equals() compares actual string character contents.", "== compares content; .equals() compares memory address.", "Both == and .equals() perform exact memory address comparison.", "== works for objects; .equals() only works for primitives.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is a Constructor in Java and how does it differ from a regular Method?", "A constructor has the same name as the class and no return type; it initializes objects during instantiation.", "A constructor must return void and can have any name.", "A constructor is static and invoked manually by the programmer.", "A constructor is used to destroy objects when garbage collected.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the primary purpose of the this keyword in Java constructors?", "To invoke superclass constructors.", "To resolve ambiguity between class instance fields and constructor parameters with the same name.", "To make constructor static.", "To prevent object creation.", "THEORY", "B"));
        list.add(new QuestionData("Theory: Explain the String Constant Pool (SCP) in Java.", "A special memory region in Heap where String literals are stored to optimize memory by reusing identical string values.", "A stack memory region for storing string characters.", "A file on disk storing java string constants.", "A collection class for holding constant strings.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What happens when you pass an array to a Java method?", "The array elements are copied by value.", "The reference to the array is passed by value, allowing method modifications to affect original array elements.", "Arrays cannot be passed to methods in Java.", "Java creates a deep copy of the array automatically.", "THEORY", "B"));
        list.add(new QuestionData("Theory: What is Constructor Overloading in Java?", "Defining multiple constructors in the same class with different parameter lists.", "Defining a constructor in a subclass with the same name as parent class.", "Creating a constructor that returns a value.", "Overriding a constructor in an interface.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Can a Java class have a private constructor? What is a common use case?", "Yes; used in Singleton design patterns to restrict direct instantiation from outside the class.", "No; Java requires all constructors to be public.", "Yes; but it causes compilation error when compiled.", "Yes; used to make the class abstract automatically.", "THEORY", "A"));

        // Coding (24-30)
        list.add(new QuestionData("Coding: What is the output of the code: int[] arr = {10, 20, 30}; System.out.println(arr[1] + arr[2]);?", "30", "50", "60", "20", "CODING", "B"));
        list.add(new QuestionData("Coding: What will be printed by: String str = \"Java Programming\"; System.out.println(str.substring(0, 4));?", "Java", "Java ", "Prog", "Java Programming", "CODING", "A"));
        list.add(new QuestionData("Coding: Which code snippet correctly finds the maximum value in an array int[] numbers?", "int max = numbers[0]; for(int i=1; i<numbers.length; i++) { if(numbers[i] > max) max = numbers[i]; }", "int max = 0; for(int i=0; i<=numbers.length; i++) { max += numbers[i]; }", "int max = numbers.length;", "int max = numbers[numbers.length];", "CODING", "A"));
        list.add(new QuestionData("Coding: What is the output of: String s1 = new String(\"Java\"); String s2 = new String(\"Java\"); System.out.println(s1 == s2);?", "true", "false", "Compilation Error", "Runtime Exception", "CODING", "B"));
        list.add(new QuestionData("Coding: What is printed by: int[] numbers = new int[3]; System.out.println(numbers[0]);?", "0", "null", "-1", "Garbage value", "CODING", "A"));
        list.add(new QuestionData("Coding: Which code snippet correctly calculates the sum of all elements in an array int[] arr?", "int sum = 0; for(int num : arr) { sum += num; }", "int sum = 0; for(int i=0; i<arr.length; i++) { sum = i; }", "int sum = arr.length * 2;", "int sum = arr[0] + arr[arr.length];", "CODING", "A"));
        list.add(new QuestionData("Coding: What is the output of: String str = \"java\"; System.out.println(str.indexOf('v'));?", "1", "2", "3", "-1", "CODING", "B"));

        return list;
    }

    private List<QuestionData> getSet3Questions() {
        List<QuestionData> list = new ArrayList<>();
        // MCQs (1-15)
        list.add(new QuestionData("Which access modifier restricts member access ONLY within the declaring class?", "public", "protected", "private", "default (package-private)", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which keyword is used by a subclass to inherit a superclass in Java?", "implements", "extends", "inherits", "super", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which OOP pillar is achieved by keeping fields private and providing public getter/setter methods?", "Abstraction", "Inheritance", "Encapsulation", "Polymorphism", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Having multiple methods in the same class with the same name but different parameter lists is called:", "Method Overriding", "Method Overloading", "Method Hiding", "Dynamic Binding", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Does Java support multiple class inheritance using extends keyword (e.g. class C extends A, B)?", "Yes", "No", "Only for abstract classes", "Only if classes are static", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword indicates that a method or variable belongs to the class itself rather than individual instances?", "final", "static", "abstract", "transient", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What type of polymorphism is Method Overloading in Java?", "Runtime Polymorphism", "Compile-time Polymorphism", "Dynamic Polymorphism", "Late Binding Polymorphism", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword is used in a subclass method to invoke an overridden method of its parent class?", "this", "super", "base", "parent", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the default access modifier of a class member when no access modifier is specified?", "public", "private", "protected", "Package-private (default)", "MULTIPLE_CHOICE", "D"));
        list.add(new QuestionData("Can a static method directly access non-static instance variables of its class?", "Yes", "No", "Only if variables are public", "Only if variables are final", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which Java keyword prevents a class from being subclassed/inherited?", "static", "final", "abstract", "private", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What happens when a child class defines a method with the exact same name, return type, and parameters as a parent class method?", "Method Overloading", "Method Overriding", "Compilation Error", "Method Redefinition Error", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which class is the ultimate superclass of all classes in Java?", "java.lang.System", "java.lang.Object", "java.lang.Class", "java.lang.Main", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the scope of a protected member in Java?", "Accessible only within the same class.", "Accessible within the same package and by subclasses in other packages.", "Accessible everywhere in the application.", "Accessible only by subclasses in the same package.", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword is used to call a superclass constructor from a subclass constructor?", "super()", "this()", "parent()", "base()", "MULTIPLE_CHOICE", "A"));

        // Theory (16-23)
        list.add(new QuestionData("Theory: Describe Encapsulation and explain why instance variables are usually declared private.", "Encapsulation wraps data and code together; private variables prevent direct unauthorized modification from outside the class.", "Encapsulation makes code execute faster by disabling security checks.", "Private variables allow child classes to access them directly without getters.", "Encapsulation allows multiple inheritance.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the main difference between Method Overloading and Method Overriding?", "Overloading occurs within the same class (same method name, different parameters); Overriding occurs between parent and child class (same signature).", "Overloading occurs in child class; Overriding occurs in same class.", "Overloading is runtime polymorphism; Overriding is compile-time polymorphism.", "Overriding requires static methods; Overloading requires final methods.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Why does Java NOT support multiple class inheritance using extends?", "To avoid the Diamond Problem ambiguity where two parent classes have methods with identical signatures.", "Because JVM memory cannot hold more than one parent object.", "Java does support multiple class inheritance using extends.", "Because C++ did not have inheritance.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the lifecycle and memory behavior of a static variable in Java?", "Static variables are created when the class is loaded into memory and shared across all instances of the class.", "Static variables are created every time an object is instantiated using new.", "Static variables are destroyed when a method finishes execution.", "Static variables are stored on the thread stack.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Explain Runtime Polymorphism (Dynamic Method Dispatch) in Java.", "Mechanism where a call to an overridden method is resolved at runtime based on the actual object type rather than reference type.", "Resolves method calls at compile time based on parameter types.", "Allows changing a class definition at runtime.", "Prevents method overriding in child classes.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What are the rules regarding access modifier visibility when overriding a method in Java?", "The overriding method in the child class cannot reduce the visibility of the parent class method (e.g. public cannot become private).", "The overriding method must always be private.", "The child method must have weaker access than the parent method.", "Access modifiers can be changed freely without rules.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Can a static method be overridden in Java? Explain why.", "No, static methods belong to the class and are resolved at compile time (known as Method Hiding, not overriding).", "Yes, static methods override normally like instance methods.", "Yes, but only if marked final.", "Static methods can only be overridden inside interfaces.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the purpose of the super keyword in Java inheritance?", "To access parent class constructors, overridden methods, and hidden fields from a child class.", "To instantiate an abstract class directly.", "To exit the child class.", "To make a child class method final.", "THEORY", "A"));

        // Coding (24-30)
        list.add(new QuestionData("Coding: What is the output of: class A { static int count = 0; A() { count++; } } after executing new A(); new A(); new A(); System.out.println(A.count);?", "1", "2", "3", "0", "CODING", "C"));
        list.add(new QuestionData("Coding: What is the output of: class Parent { void show() { System.out.print(\"P \"); } } class Child extends Parent { void show() { System.out.print(\"C \"); } } executing Parent obj = new Child(); obj.show();?", "P ", "C ", "P C", "Compilation Error", "CODING", "B"));
        list.add(new QuestionData("Coding: Which code snippet correctly implements Encapsulation getter method for private variable private int age;?", "public int getAge() { return age; }", "private void getAge() { return age; }", "public void getAge(int a) { age = a; }", "static int getAge() { return age; }", "CODING", "A"));
        list.add(new QuestionData("Coding: What is printed by: class X { int num = 10; } class Y extends X { int num = 20; void print() { System.out.println(super.num); } } when calling new Y().print();?", "10", "20", "0", "Compilation Error", "CODING", "A"));
        list.add(new QuestionData("Coding: Which method signature demonstrates Method Overloading for add(int a, int b)?", "public int add(int a, int b, int c)", "public void add(int x, int y)", "private int add(int a, int b)", "final int add(int a, int b)", "CODING", "A"));
        list.add(new QuestionData("Coding: What is the output of calling Animal a = new Animal(); if class Dog extends Animal has constructor Dog() { super(); System.out.print(\"Dog \"); } and Animal() { System.out.print(\"Animal \"); }?", "Animal ", "Animal Dog ", "Dog Animal ", "Dog ", "CODING", "A"));
        list.add(new QuestionData("Coding: Which header correctly declares a class Car inheriting from class Vehicle?", "class Car implements Vehicle", "class Car extends Vehicle", "class Car inherits Vehicle", "class Car : Vehicle", "CODING", "B"));

        return list;
    }

    private List<QuestionData> getSet4Questions() {
        List<QuestionData> list = new ArrayList<>();
        // MCQs (1-15)
        list.add(new QuestionData("Which keyword is used to declare an Abstract class in Java?", "interface", "abstract", "virtual", "implements", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword is used by a class to implement an Interface in Java?", "extends", "implements", "uses", "inherits", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Can an Abstract class be directly instantiated using the new keyword?", "Yes", "No", "Only if all methods are implemented", "Only if it contains no methods", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What are the implicit modifiers for fields declared inside a Java Interface by default?", "private final", "public static final", "protected static", "default volatile", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which block is used to enclose code that might throw an exception in Java?", "catch", "try", "finally", "throws", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which block ALWAYS executes regardless of whether an exception is thrown or caught?", "try", "catch", "finally", "throw", "MULTIPLE_CHOICE", "C"));
        list.add(new QuestionData("Which keyword is used to explicitly throw an exception object from a method?", "throws", "throw", "raise", "catch", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What is the root superclass of all Exception and Error classes in Java?", "java.lang.Exception", "java.lang.Throwable", "java.lang.Error", "java.lang.Object", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which exception occurs when an integer is divided by zero in Java?", "NullPointerException", "ArithmeticException", "NumberFormatException", "ClassCastException", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Can a single Java class implement multiple interfaces?", "No", "Yes", "Only up to 2 interfaces", "Only if interfaces have no methods", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which keyword is used in a method declaration signature to specify exceptions that the method might throw?", "throw", "throws", "try", "catch", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What category of exception is NullPointerException in Java?", "Checked Exception", "Unchecked (RuntimeException) Exception", "Compile-time Error", "JVM Fatal Error", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("Which method of Throwable prints exception details along with execution line numbers to standard error?", "getMessage()", "printStackTrace()", "toString()", "getCause()", "MULTIPLE_CHOICE", "B"));
        list.add(new QuestionData("What happens if a non-abstract class implements an interface?", "It must provide concrete implementations for all abstract methods of the interface.", "It can choose to ignore interface methods.", "It becomes abstract automatically.", "Compilation fails unless interface is empty.", "MULTIPLE_CHOICE", "A"));
        list.add(new QuestionData("Which exception occurs when attempting to parse \"abc\" as an integer using Integer.parseInt()?", "ArithmeticException", "NumberFormatException", "NullPointerException", "IllegalArgumentException", "MULTIPLE_CHOICE", "B"));

        // Theory (16-23)
        list.add(new QuestionData("Theory: What is the main difference between an Abstract Class and an Interface in Java?", "Abstract classes can have instance fields and constructors; Interfaces fields are public static final and cannot have constructors.", "Abstract classes cannot have method bodies; Interfaces can have any method body.", "A class can extend multiple abstract classes; a class can implement only one interface.", "There is no difference between abstract classes and interfaces.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Explain the difference between Checked Exceptions and Unchecked Exceptions in Java.", "Checked exceptions are checked at compile-time (must be handled or declared); Unchecked exceptions occur at runtime (subclasses of RuntimeException).", "Unchecked exceptions occur at compile-time; Checked exceptions occur at runtime.", "Checked exceptions cannot be caught in try-catch.", "Unchecked exceptions cause JVM to crash instantly without recovery.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the difference between throw and throws keywords in Java?", "throw is used inside a method body to throw an exception object; throws is used in a method signature to declare exceptions.", "throws throws an exception object; throw declares exceptions.", "Both throw and throws are used in method signatures interchangeably.", "throw is for checked exceptions; throws is for unchecked exceptions.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the purpose of the finally block in Java exception handling?", "To execute clean-up code (like closing resources/files) regardless of whether an exception occurred or was caught.", "To catch exceptions that were missed by catch blocks.", "To restart the try block execution.", "To suppress exception errors completely.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What causes a NullPointerException in Java and how can it be avoided?", "Occurs when calling a method or accessing a field on an object reference that is null; avoided by null-checks before dereferencing.", "Occurs when dividing an integer by zero; avoided by checking divisor.", "Occurs when array index is invalid.", "Occurs when heap memory is full.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Can a try block exist with ONLY a finally block and NO catch block?", "Yes, a try block can be followed by a finally block without any catch block.", "No, every try block must have at least one catch block.", "No, finally blocks require at least two catch blocks.", "Yes, but only inside abstract methods.", "THEORY", "A"));
        list.add(new QuestionData("Theory: What is the rule regarding the order of multiple catch blocks in Java?", "Subclass exception types must be caught BEFORE superclass exception types (more specific to more general).", "Superclass exception types must be caught before subclass exception types.", "Order of catch blocks does not matter.", "Only one catch block is permitted per try block.", "THEORY", "A"));
        list.add(new QuestionData("Theory: Can an Interface extend another Interface in Java?", "Yes, an interface can extend one or multiple other interfaces using the extends keyword.", "No, interfaces cannot inherit from other interfaces.", "Yes, but only using the implements keyword.", "Yes, but only if both interfaces are empty.", "THEORY", "A"));

        // Coding (24-30)
        list.add(new QuestionData("Coding: What is the output of: try { int res = 10 / 0; } catch(ArithmeticException e) { System.out.print(\"Catch \"); } finally { System.out.print(\"Finally\"); }?", "Catch Finally", "Finally", "Catch", "ArithmeticException", "CODING", "A"));
        list.add(new QuestionData("Coding: What is the output of: try { System.out.print(\"Try \"); } finally { System.out.print(\"Finally\"); }?", "Try Finally", "Try", "Finally", "Compilation Error", "CODING", "A"));
        list.add(new QuestionData("Coding: What happens when executing String s = null; System.out.println(s.length());?", "Prints 0", "Throws NullPointerException", "Prints null", "Compilation Error", "CODING", "B"));
        list.add(new QuestionData("Coding: Which code snippet correctly catches a NumberFormatException when converting string to integer?", "try { int num = Integer.parseInt(\"abc\"); } catch(NumberFormatException e) { System.out.println(\"Invalid format\"); }", "try { int num = Integer.parseInt(\"abc\"); } catch(ArithmeticException e) { System.out.println(\"Invalid format\"); }", "try { int num = Integer.parseInt(\"abc\"); } catch(NullPointerException e) { System.out.println(\"Invalid format\"); }", "catch(NumberFormatException e) { int num = Integer.parseInt(\"abc\"); }", "CODING", "A"));
        list.add(new QuestionData("Coding: Which class header correctly declares class Circle implementing interface Drawable?", "class Circle implements Drawable", "class Circle extends Drawable", "class Circle inherits Drawable", "interface Circle implements Drawable", "CODING", "A"));
        list.add(new QuestionData("Coding: What is the output of: int[] arr = {1, 2}; try { System.out.print(arr[5]); } catch(ArrayIndexOutOfBoundsException e) { System.out.print(\"Out \"); } finally { System.out.print(\"End\"); }?", "Out End", "1 End", "End", "Out", "CODING", "A"));
        list.add(new QuestionData("Coding: Which code snippet correctly checks if array index i is safe to access for array arr?", "if (i >= 0 && i < arr.length) { System.out.println(arr[i]); }", "if (i <= arr.length) { System.out.println(arr[i]); }", "if (i > 0 && i <= arr.length) { System.out.println(arr[i]); }", "if (arr[i] != null) { System.out.println(arr[i]); }", "CODING", "A"));

        return list;
    }
}
