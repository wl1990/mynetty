package com.test.netty.chapter10.http_xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.List;

public class Customer {
    @XStreamAsAttribute
    private long customerNumber;
    private String firstName;
    private String lastName;
    private List<String> middleNames;

    public long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<String> middleNames) {
        this.middleNames = middleNames;
    }
}
