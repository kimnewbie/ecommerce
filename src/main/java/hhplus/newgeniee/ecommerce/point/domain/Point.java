package hhplus.newgeniee.ecommerce.point.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Point {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;
}
