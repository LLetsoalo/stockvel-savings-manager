package com.example.demo.web;

import jakarta.validation.constraints.NotBlank;

public class MemberForm {

    @NotBlank
    private String fullName;

    @NotBlank
    private String contactNumber;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
