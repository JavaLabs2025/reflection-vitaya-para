package org.example.classes;

import org.example.generator.Generatable;

@Generatable
public class Address {
    private final String street;
    private final Person owner;

    public Address(String street, Person owner) {
        this.street = street;
        this.owner = owner;
    }

    public String getStreet() {
        return street;
    }

    public Person getOwner() {
        return owner;
    }
}
