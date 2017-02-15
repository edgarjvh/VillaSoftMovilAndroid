package models;

public class Device {
    private int deviceId;
    private String imei;
    private String phoneNumber;
    private String ip;
    private int port;
    private long registrationDate;
    private long expirationDate;
    private int statusDealer;
    private int statusSell;
    private int freeSuspension;
    private int batteryEvent;
    private int geofenceEvent;
    private int speedEvent;
    private int panicEvent;
    private int ignitionNotification;
    private int batteryNotification;
    private int geofenceNotification;
    private int speedNotification;
    private Vehicle vehicle;
    private DeviceModel deviceModel;
    private Dealer dealer;
    private DeviceTrace deviceTrace;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getStatusDealer() {
        return statusDealer;
    }

    public void setStatusDealer(int statusDealer) {
        this.statusDealer = statusDealer;
    }

    public int getStatusSell() {
        return statusSell;
    }

    public void setStatusSell(int statusSell) {
        this.statusSell = statusSell;
    }

    public int getFreeSuspension() {
        return freeSuspension;
    }

    public void setFreeSuspension(int freeSuspension) {
        this.freeSuspension = freeSuspension;
    }

    public int getBatteryEvent() {
        return batteryEvent;
    }

    public void setBatteryEvent(int batteryEvent) {
        this.batteryEvent = batteryEvent;
    }

    public int getGeofenceEvent() {
        return geofenceEvent;
    }

    public void setGeofenceEvent(int geofenceEvent) {
        this.geofenceEvent = geofenceEvent;
    }

    public int getSpeedEvent() {
        return speedEvent;
    }

    public void setSpeedEvent(int speedEvent) {
        this.speedEvent = speedEvent;
    }

    public int getPanicEvent() {
        return panicEvent;
    }

    public void setPanicEvent(int panicEvent) {
        this.panicEvent = panicEvent;
    }

    public int getIgnitionNotification() {
        return ignitionNotification;
    }

    public void setIgnitionNotification(int ignitionNotification) {
        this.ignitionNotification = ignitionNotification;
    }

    public int getBatteryNotification() {
        return batteryNotification;
    }

    public void setBatteryNotification(int batteryNotification) {
        this.batteryNotification = batteryNotification;
    }

    public int getGeofenceNotification() {
        return geofenceNotification;
    }

    public void setGeofenceNotification(int geofenceNotification) {
        this.geofenceNotification = geofenceNotification;
    }

    public int getSpeedNotification() {
        return speedNotification;
    }

    public void setSpeedNotification(int speedNotification) {
        this.speedNotification = speedNotification;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public DeviceTrace getDeviceTrace() {
        return deviceTrace;
    }

    public void setDeviceTrace(DeviceTrace deviceTrace) {
        this.deviceTrace = deviceTrace;
    }

    public Device(){
        this.vehicle = new Vehicle();
        this.deviceModel = new DeviceModel();
        this.dealer = new Dealer();
        this.deviceTrace = new DeviceTrace();
    }
}
