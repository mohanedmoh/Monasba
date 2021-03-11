package com.savvy.monasbat.Model;

public class req_info {
    int req_type;//1=halls,2=grace,3=wedding procession,4=photographer,5=barber,6=hairdresser,7=hotel,8=honeymoon,9=buses,10=meeting halls,11=organizing group,12=kitchens,13=outdoor halls,14=party designer
    String req_status;
    //seera Req attr
    String id, date, bus_type_id, bus_type, number_of_buses, pickup_address, dropoff_address, phone1, phone2, reservation_type, reservation_type_id, price, paid, approved, payment_method;
    //hall reservation attr
    String name1, name2, num_of_people, include_meal, meal_id, package_id, package_name, package_price, meal_name, meal_price, num_of_meals, hall_id, hall_name, city_id, city_name, address, extra_hours, payment_type_id, payment_type;
    //limo attr
    String pickup_time, dropoff_time, car_name, car_model, car_details;
    String[] package_details, meal_details;

    //
    public req_info() {
        package_details = new String[0];
        meal_details = new String[0];
        price = "0";
        package_price = "0";
        meal_price = "0";
        package_id = "0";
        meal_id = "0";
        include_meal = "0";
        num_of_meals = "0";
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getDropoff_time() {
        return dropoff_time;
    }

    public void setDropoff_time(String dropoff_time) {
        this.dropoff_time = dropoff_time;
    }

    public String getCar_details() {
        return car_details;
    }

    public void setCar_details(String car_details) {
        this.car_details = car_details;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getNum_of_meals() {
        return num_of_meals;
    }

    public void setNum_of_meals(String num_of_meals) {
        this.num_of_meals = num_of_meals;
    }

    public String getMeal_price() {
        return meal_price;
    }

    public void setMeal_price(String meal_price) {
        this.meal_price = meal_price;
    }

    public String getPackage_price() {
        return package_price;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPayment_type_id() {
        return payment_type_id;
    }

    public void setPayment_type_id(String payment_type_id) {
        this.payment_type_id = payment_type_id;
    }

    public String getExtra_hours() {
        return extra_hours;
    }

    public void setExtra_hours(String extra_hours) {
        this.extra_hours = extra_hours;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getReq_status() {
        return req_status;
    }

    public void setReq_status(String req_status) {
        this.req_status = req_status;
    }

    public int getReq_type() {
        return req_type;
    }

    public void setReq_type(int req_type) {
        this.req_type = req_type;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBus_type_id() {
        return bus_type_id;
    }

    public void setBus_type_id(String bus_type_id) {
        this.bus_type_id = bus_type_id;
    }

    public String getNumber_of_buses() {
        return number_of_buses;
    }

    public void setNumber_of_buses(String number_of_buses) {
        this.number_of_buses = number_of_buses;
    }

    public String getBus_type() {
        return bus_type;
    }

    public void setBus_type(String bus_type) {
        this.bus_type = bus_type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getReservation_type_id() {
        return reservation_type_id;
    }

    public void setReservation_type_id(String reservation_type_id) {
        this.reservation_type_id = reservation_type_id;
    }

    public String getReservation_type() {
        return reservation_type;
    }

    public void setReservation_type(String reservation_type) {
        this.reservation_type = reservation_type;
    }

    public String getDropoff_address() {
        return dropoff_address;
    }

    public void setDropoff_address(String dropoff_address) {
        this.dropoff_address = dropoff_address;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getHall_id() {
        return hall_id;
    }

    public void setHall_id(String hall_id) {
        this.hall_id = hall_id;
    }

    public String getHall_name() {
        return hall_name;
    }

    public void setHall_name(String hall_name) {
        this.hall_name = hall_name;
    }

    public String getInclude_meal() {
        return include_meal;
    }

    public void setInclude_meal(String include_meal) {
        this.include_meal = include_meal;
    }

    public String getMeal_id() {
        return meal_id;
    }

    public void setMeal_id(String meal_id) {
        this.meal_id = meal_id;
    }

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public void setPackage_price(String package_price) {
        this.package_price = package_price;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getNum_of_people() {
        return num_of_people;
    }

    public void setNum_of_people(String num_of_people) {
        this.num_of_people = num_of_people;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String[] getMeal_details() {
        return meal_details;
    }

    public void setMeal_details(String[] meal_details) {
        this.meal_details = meal_details;
    }

    public String[] getPackage_details() {
        return package_details;
    }

    public void setPackage_details(String[] package_details) {
        this.package_details = package_details;
    }

}
