package com.savvy.monasbat.Model;

public class bus_req {
    String id, date, bus_type_id, bus_type, number_of_buses, pickup_address, dropoff_address, main_phone, sub_phone, reservation_type, reservation_type_id, price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMain_phone() {
        return main_phone;
    }

    public void setMain_phone(String main_phone) {
        this.main_phone = main_phone;
    }

    public String getReservation_type() {
        return reservation_type;
    }

    public void setReservation_type(String reservation_type) {
        this.reservation_type = reservation_type;
    }

    public String getReservation_type_id() {
        return reservation_type_id;
    }

    public void setReservation_type_id(String reservation_type_id) {
        this.reservation_type_id = reservation_type_id;
    }

    public String getSub_phone() {
        return sub_phone;
    }

    public void setSub_phone(String sub_phone) {
        this.sub_phone = sub_phone;
    }

    public String getBus_type() {
        return bus_type;
    }

    public void setBus_type(String bus_type) {
        this.bus_type = bus_type;
    }

    public String getBus_type_id() {
        return bus_type_id;
    }

    public void setBus_type_id(String bus_type_id) {
        this.bus_type_id = bus_type_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber_of_buses() {
        return number_of_buses;
    }

    public void setNumber_of_buses(String number_of_buses) {
        this.number_of_buses = number_of_buses;
    }

    public String getDropoff_address() {
        return dropoff_address;
    }

    public void setDropoff_address(String dropoff_address) {
        this.dropoff_address = dropoff_address;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }
}
