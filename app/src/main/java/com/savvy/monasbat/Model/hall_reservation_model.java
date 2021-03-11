package com.savvy.monasbat.Model;

import java.io.Serializable;

public class hall_reservation_model implements Serializable {
    String req_id, hall_price, package_price, meal_price, payment_type_id, extra_hours, name1, name2, phone1, phone2, date, num_of_people_id, include_meal, reservation_type_id, meal_id, num_of_meals, package_id, package_name, meal_name, hall_id, price, hall_name, city_id, city_name, address, num_of_ppl, policy;
    String[] package_details, meal_details;

    public hall_reservation_model() {
        package_details = new String[0];
        meal_details = new String[0];
        hall_price = "0";
        package_price = "0";
        meal_price = "0";
        package_id = "0";
        meal_id = "0";
        num_of_meals = "0";
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getNum_of_ppl() {
        return num_of_ppl;
    }

    public void setNum_of_ppl(String num_of_ppl) {
        this.num_of_ppl = num_of_ppl;
    }

    public String getReq_id() {
        return req_id;
    }

    public void setReq_id(String req_id) {
        this.req_id = req_id;
    }

    public String getHall_price() {
        return hall_price;
    }

    public void setHall_price(String hall_price) {
        this.hall_price = hall_price;
    }

    public String getPackage_price() {
        return package_price;
    }

    public void setPackage_price(String package_price) {
        this.package_price = package_price;
    }

    public String getMeal_price() {
        return meal_price;
    }

    public void setMeal_price(String meal_price) {
        this.meal_price = meal_price;
    }

    public String getNum_of_meals() {
        return num_of_meals;
    }

    public void setNum_of_meals(String num_of_meals) {
        this.num_of_meals = num_of_meals;
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

    public String getMeal_name() {
        return meal_name;
    }

    public void setMeal_name(String meal_name) {
        this.meal_name = meal_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
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

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
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

    public String getNum_of_people_id() {
        return num_of_people_id;
    }

    public void setNum_of_people_id(String num_of_people_id) {
        this.num_of_people_id = num_of_people_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReservation_type_id() {
        return reservation_type_id;
    }

    public void setReservation_type_id(String reservation_type_id) {
        this.reservation_type_id = reservation_type_id;
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
