package com.example.androidpv_016.dto.category;

public class CreateCategoryDTO {
    private String name;
    private String description;
    private String imageBase64;
    private int priority;

    public CreateCategoryDTO(String name, String description, String imageBase64, int priority) {
        this.name = name;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public int getPriority() {
        return priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
