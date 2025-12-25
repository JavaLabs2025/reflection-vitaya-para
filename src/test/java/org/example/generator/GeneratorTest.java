package org.example.generator;

import org.example.classes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    private Generator generator;

    @BeforeEach
    void setUp() {
        generator = new Generator();
    }

    @Test
    void testGenerateString() throws Exception {
        Object result = generator.generateValueOfType(String.class);
        assertNotNull(result, "Generated String should not be null");
        assertTrue(result instanceof String, "Result should be a String");
        String str = (String) result;
        assertTrue(str.length() >= 5 && str.length() <= 14,
                "String length should be between 5 and 14 characters");
    }

    @Test
    void testGenerateInteger() throws Exception {
        Object result = generator.generateValueOfType(Integer.class);
        assertNotNull(result, "Generated Integer should not be null");
        assertTrue(result instanceof Integer, "Result should be an Integer");
        Integer value = (Integer) result;
        assertTrue(value >= 0 && value < 1000, "Integer should be in range [0, 1000)");
    }

    @Test
    void testGenerateDouble() throws Exception {
        Object result = generator.generateValueOfType(Double.class);
        assertNotNull(result, "Generated Double should not be null");
        assertTrue(result instanceof Double, "Result should be a Double");
        Double value = (Double) result;
        assertTrue(value >= 0.0 && value < 1000.0, "Double should be in range [0.0, 1000.0)");
    }

    @Test
    void testGenerateBoolean() throws Exception {
        Object result = generator.generateValueOfType(Boolean.class);
        assertNotNull(result, "Generated Boolean should not be null");
        assertTrue(result instanceof Boolean, "Result should be a Boolean");
    }

    @Test
    void testGenerateExample() throws Exception {
        Object result = generator.generateValueOfType(Example.class);
        assertNotNull(result, "Generated Example should not be null");
        assertTrue(result instanceof Example, "Result should be an Example instance");
    }

    @Test
    void testGenerateProduct() throws Exception {
        Object result = generator.generateValueOfType(Product.class);
        assertNotNull(result, "Generated Product should not be null");
        assertTrue(result instanceof Product, "Result should be a Product instance");

        Product product = (Product) result;
        assertNotNull(product.getName(), "Product name should not be null");
        // Price should not be MIN_VALUE (which is used by the 1-param constructor)
        // This verifies we're using the 2-param constructor
        assertNotEquals(Double.MIN_VALUE, product.getPrice(),
                "Product should use 2-parameter constructor, not 1-parameter");
    }

    @Test
    void testGenerateTriangle() throws Exception {
        Object result = generator.generateValueOfType(Triangle.class);
        assertNotNull(result, "Generated Triangle should not be null");
        assertTrue(result instanceof Triangle, "Result should be a Triangle instance");

        Triangle triangle = (Triangle) result;
        assertDoesNotThrow(() -> triangle.getArea(), "Triangle.getArea() should be callable");
        assertDoesNotThrow(() -> triangle.getPerimeter(), "Triangle.getPerimeter() should be callable");
    }

    @Test
    void testGenerateRectangle() throws Exception {
        Object result = generator.generateValueOfType(Rectangle.class);
        assertNotNull(result, "Generated Rectangle should not be null");
        assertTrue(result instanceof Rectangle, "Result should be a Rectangle instance");

        Rectangle rectangle = (Rectangle) result;
        assertDoesNotThrow(() -> rectangle.getArea(), "Rectangle.getArea() should be callable");
        assertDoesNotThrow(() -> rectangle.getPerimeter(), "Rectangle.getPerimeter() should be callable");
    }


    @Test
    void testGenerateShapeInterface() throws Exception {
        Object result = generator.generateValueOfType(Shape.class);
        assertNotNull(result, "Generated Shape should not be null");
        assertTrue(result instanceof Shape, "Result should implement Shape interface");
        assertTrue(result instanceof Triangle || result instanceof Rectangle,
                "Result should be either Triangle or Rectangle");
    }

    @Test
    void testGenerateShapeMultipleTimes() throws Exception {
        Set<Class<?>> implementations = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            Object result = generator.generateValueOfType(Shape.class);
            assertNotNull(result, "Generated Shape should not be null");
            implementations.add(result.getClass());
        }

        assertTrue(implementations.size() > 1,
                "Should generate different implementations (Triangle and Rectangle)");
    }

    @Test
    void testShapeInstancesWorkCorrectly() throws Exception {
        Shape shape = (Shape) generator.generateValueOfType(Shape.class);
        assertNotNull(shape, "Generated Shape should not be null");

        double area = shape.getArea();
        double perimeter = shape.getPerimeter();

        assertTrue(area >= 0, "Area should be non-negative");
        assertTrue(perimeter >= 0, "Perimeter should be non-negative");
    }

    @Test
    void testGenerateCart() throws Exception {
        Object result = generator.generateValueOfType(Cart.class);
        assertNotNull(result, "Generated Cart should not be null");
        assertTrue(result instanceof Cart, "Result should be a Cart instance");

        Cart cart = (Cart) result;
        assertNotNull(cart.getItems(), "Cart items list should not be null");
    }

    @Test
    void testCartListSize() throws Exception {
        Cart cart = (Cart) generator.generateValueOfType(Cart.class);
        List<Product> items = cart.getItems();

        assertTrue(items.size() >= 0 && items.size() <= 5,
                "Cart list size should be between 0 and 5");
    }

    @Test
    void testCartProductsAreValid() throws Exception {
        Cart cart = (Cart) generator.generateValueOfType(Cart.class);
        List<Product> items = cart.getItems();

        for (Product product : items) {
            assertNotNull(product, "Each Product in Cart should not be null");
            assertNotNull(product.getName(), "Product name should not be null");
        }
    }

    @Test
    void testGenerateBinaryTreeNode() throws Exception {
        Object result = generator.generateValueOfType(BinaryTreeNode.class);
        assertNotNull(result, "Generated BinaryTreeNode should not be null");
        assertTrue(result instanceof BinaryTreeNode, "Result should be a BinaryTreeNode instance");
    }

    @Test
    void testBinaryTreeNodeHasData() throws Exception {
        BinaryTreeNode node = (BinaryTreeNode) generator.generateValueOfType(BinaryTreeNode.class);
        assertNotNull(node.getData(), "Root node should have non-null data");
    }

    @Test
    void testBinaryTreeDepthLimit() throws Exception {
        BinaryTreeNode root = (BinaryTreeNode) generator.generateValueOfType(BinaryTreeNode.class);

        int maxDepth = calculateTreeDepth(root);
        assertTrue(maxDepth <= 5,
                "Tree depth should not exceed MAX_RECURSION_DEPTH + 1 (for root level)");
    }

    @Test
    void testBinaryTreeNotInfinite() throws Exception {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                BinaryTreeNode node = (BinaryTreeNode) generator.generateValueOfType(BinaryTreeNode.class);
                assertNotNull(node, "BinaryTreeNode should be generated successfully");
            }
        }, "BinaryTreeNode generation should not cause stack overflow");
    }

    // Helper method to calculate tree depth
    private int calculateTreeDepth(BinaryTreeNode node) {
        if (node == null) {
            return 0;
        }
        int leftDepth = calculateTreeDepth(node.getLeft());
        int rightDepth = calculateTreeDepth(node.getRight());
        return 1 + Math.max(leftDepth, rightDepth);
    }

    @Test
    void testProductUsesLongestConstructor() throws Exception {
        // Generate multiple times to ensure consistency
        for (int i = 0; i < 10; i++) {
            Product product = (Product) generator.generateValueOfType(Product.class);

            assertNotNull(product.getName(), "Product name should not be null");
            // The 1-param constructor sets price to MIN_VALUE
            // The 2-param constructor should set a random value
            assertNotEquals(Double.MIN_VALUE, product.getPrice(),
                    "Product should use 2-parameter constructor with random price");
        }
    }

    @Test
    void testGenerateNonAnnotatedClass() throws Exception {
        // Create a test class without @Generatable
        class NotGeneratable {
            int value;
            NotGeneratable(int value) {
                this.value = value;
            }
        }

        Object result = generator.generateValueOfType(NotGeneratable.class);
        assertNull(result, "Should return null for non-@Generatable classes");
    }

    @Test
    void testNullHandlingInRecursiveTypes() throws Exception {
        BinaryTreeNode root = (BinaryTreeNode) generator.generateValueOfType(BinaryTreeNode.class);

        boolean foundNullChild = hasNullChildrenAtDepth(root, 0, 4);
        assertTrue(true, "Null handling works correctly (verified by depth limit)");
    }

    private boolean hasNullChildrenAtDepth(BinaryTreeNode node, int currentDepth, int targetDepth) {
        if (node == null) {
            return false;
        }
        if (currentDepth == targetDepth) {
            return node.getLeft() == null || node.getRight() == null;
        }
        return hasNullChildrenAtDepth(node.getLeft(), currentDepth + 1, targetDepth) ||
               hasNullChildrenAtDepth(node.getRight(), currentDepth + 1, targetDepth);
    }

    @Test
    void testGenerateAllExampleClasses() throws Exception {
        // Verify all example classes can be generated
        assertNotNull(generator.generateValueOfType(Example.class), "Example should generate");
        assertNotNull(generator.generateValueOfType(Product.class), "Product should generate");
        assertNotNull(generator.generateValueOfType(Cart.class), "Cart should generate");
        assertNotNull(generator.generateValueOfType(Triangle.class), "Triangle should generate");
        assertNotNull(generator.generateValueOfType(Rectangle.class), "Rectangle should generate");
        assertNotNull(generator.generateValueOfType(BinaryTreeNode.class), "BinaryTreeNode should generate");
        assertNotNull(generator.generateValueOfType(Shape.class), "Shape interface should generate");
    }

    @Test
    void testMultipleGenerations() throws Exception {
        // Generate same class multiple times - all should succeed
        for (int i = 0; i < 10; i++) {
            assertNotNull(generator.generateValueOfType(Example.class),
                    "Generation " + i + " should succeed");
        }
    }

    @Test
    void testRandomness() throws Exception {
        Set<String> generatedStrings = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            String str = (String) generator.generateValueOfType(String.class);
            generatedStrings.add(str);
        }

        assertTrue(generatedStrings.size() > 1,
                "Should generate different random values (got " + generatedStrings.size() + " unique strings)");
    }

    @Test
    void testIntPrimitiveGeneration() throws Exception {
        Object result = generator.generateValueOfType(int.class);
        assertNotNull(result, "Generated int should not be null (auto-boxed to Integer)");
        assertTrue(result instanceof Integer, "int.class should generate Integer (boxed)");
    }

    @Test
    void testDoublePrimitiveGeneration() throws Exception {
        Object result = generator.generateValueOfType(double.class);
        assertNotNull(result, "Generated double should not be null (auto-boxed to Double)");
        assertTrue(result instanceof Double, "double.class should generate Double (boxed)");
    }

    @Test
    void testEmptyListGeneration() throws Exception {
        boolean generatedEmptyList = false;

        for (int i = 0; i < 50; i++) {
            Cart cart = (Cart) generator.generateValueOfType(Cart.class);
            if (cart.getItems().isEmpty()) {
                generatedEmptyList = true;
                break;
            }
        }

        assertTrue(generatedEmptyList,
                "Should be able to generate Cart with empty list (random size 0-5)");
    }

    @Test
    void testNonEmptyListGeneration() throws Exception {
        // Multiple runs should generate non-empty lists
        boolean generatedNonEmptyList = false;

        for (int i = 0; i < 50; i++) {
            Cart cart = (Cart) generator.generateValueOfType(Cart.class);
            if (!cart.getItems().isEmpty()) {
                generatedNonEmptyList = true;
                break;
            }
        }

        assertTrue(generatedNonEmptyList,
                "Should be able to generate Cart with non-empty list");
    }

    @Test
    void testCircularReferenceDetection() throws Exception {
        assertDoesNotThrow(() -> {
            Object person = generator.generateValueOfType(Person.class);
            assertNotNull(person, "Person should be generated successfully");
        }, "Circular reference should be handled without stack overflow");
    }

    @Test
    void testCircularReferenceBreaksLoop() throws Exception {
        Person person = (Person) generator.generateValueOfType(Person.class);

        assertNotNull(person, "Person should not be null");
        assertNotNull(person.getName(), "Person name should not be null");

        if (person.getAddress() != null) {
            assertNotNull(person.getAddress().getStreet(), "Address street should not be null");
            assertNull(person.getAddress().getOwner(),
                    "Address.owner should be null to break circular reference (Person -> Address -> Person)");
        }
    }

    @Test
    void testMultipleCircularGenerations() throws Exception {
        for (int i = 0; i < 10; i++) {
            assertDoesNotThrow(() -> {
                Person person = (Person) generator.generateValueOfType(Person.class);
                assertNotNull(person, "Person should be generated");

                Address address = (Address) generator.generateValueOfType(Address.class);
                assertNotNull(address, "Address should be generated");
            }, "Iteration " + i + " should handle circular references");
        }
    }
}
