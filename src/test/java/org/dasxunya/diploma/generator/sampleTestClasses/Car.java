package org.dasxunya.diploma.generator.sampleTestClasses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Car {
    // Сеттеры для установки значений полей
    // Поля класса
    private String brand;
    private String model;
    private int year;
    private double price;

    // Конструктор класса
    public Car(String brand, String model, int year, double price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
    }

    // Метод для получения информации о машине
    public String getCarInfo() {
        return "Brand: " + brand + ", Model: " + model + ", Year: " + year + ", Price: $" + price;
    }

    // Пример другого метода
    public void drive() {
        System.out.println("The car is being driven.");
    }
}
