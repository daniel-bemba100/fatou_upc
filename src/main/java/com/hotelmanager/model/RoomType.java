package com.hotelmanager.model;

/**
 * RoomType entity representing hotel room categories
 */
public class RoomType extends BaseEntity {
    private String typeName;
    private String description;
    private double basePrice;
    private int maxOccupancy;
    private String amenities;

    public RoomType() {
        super();
        this.maxOccupancy = 2;
        this.basePrice = 0.0;
    }

    public RoomType(int id) {
        super(id);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    @Override
    public String toString() {
        return typeName + " ($" + basePrice + ")";
    }
}
