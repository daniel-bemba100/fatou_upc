package com.hotelmanager.model;

/**
 * Service entity representing additional hotel services
 */
public class HotelService extends BaseEntity {
    private String serviceName;
    private String description;
    private double price;
    private boolean isActive;

    public HotelService() {
        super();
        this.isActive = true;
    }

    public HotelService(int id) {
        super(id);
        this.isActive = true;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return serviceName + " ($" + price + ")";
    }
}
