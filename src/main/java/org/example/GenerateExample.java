package org.example;


import org.example.classes.Address;
import org.example.classes.Example;
import org.example.classes.Person;
import org.example.classes.Shape;
import org.example.generator.Generator;

public class GenerateExample {
    public static void main(String[] args) {
        case1();
        System.out.println("\n");
        case2();
        System.out.println("\n");
        case3();
    }

    private static void case1() {
        var gen = new Generator();
        try {
            Object generated = gen.generateValueOfType(Example.class);
            System.out.println(generated);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void case2() {
        var gen = new Generator();
        try {
            System.out.println("Testing Shape.class generation:");
            for (int i = 0; i < 5; i++) {
                Object generated = gen.generateValueOfType(Shape.class);
                if (generated instanceof Shape) {
                    Shape shape = (Shape) generated;
                    System.out.println("Generated: " + shape.getClass().getSimpleName() +
                            " - Area: " + shape.getArea() +
                            ", Perimeter: " + shape.getPerimeter());
                } else {
                    System.out.println("Failed to generate Shape");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void case3() {
        Generator generator = new Generator();

        try {
            System.out.println("Demonstrating Circularity Detection");
            System.out.println("====================================\n");

            System.out.println("Generating Person (which references Address, which references Person)...");
            Person person = (Person) generator.generateValueOfType(Person.class);

            if (person != null) {
                System.out.println("Person generated successfully");
                System.out.println("  - Name: " + person.getName());

                if (person.getAddress() != null) {
                    System.out.println("  - Address: " + person.getAddress().getStreet());
                    System.out.println("  - Address.owner: " +
                            (person.getAddress().getOwner() == null ? "null (cycle broken)" : "NOT NULL (BUG!)"));
                } else {
                    System.out.println("  - Address: null");
                }
            }

            System.out.println("\nGenerating Address (which references Person, which references Address)...");
            Address address = (Address) generator.generateValueOfType(Address.class);

            if (address != null) {
                System.out.println("Address generated successfully");
                System.out.println("  - Street: " + address.getStreet());

                if (address.getOwner() != null) {
                    System.out.println("  - Owner: " + address.getOwner().getName());
                    System.out.println("  - Owner.address: " +
                            (address.getOwner().getAddress() == null ? "null (cycle broken)" : "NOT NULL (BUG!)"));
                } else {
                    System.out.println("  - Owner: null");
                }
            }

            System.out.println("\nCircularity detection successfully prevented infinite loops!");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}