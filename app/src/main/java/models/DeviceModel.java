package models;

public class DeviceModel {
    private int deviceModelId;
    private String brand;
    private String model;

    public int getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(int deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
