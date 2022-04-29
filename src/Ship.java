package com.company;

public class Ship {
    int length;
    int initialX, initialY, initialD;
    int[][] coordinates;

    public Ship(int initialX, int initialY, int initialD, int length) {
        this.initialX = initialX;
        this.initialY = initialY;
        this.initialD = initialD;
        System.out.println("SHIP CONSTTUCTOR");
        this.length = length;
    }

    public void addCoordinates(int[][] coordinates) {
        this.coordinates = coordinates;
    }
}


