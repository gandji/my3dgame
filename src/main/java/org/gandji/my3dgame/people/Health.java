package org.gandji.my3dgame.people;

public class Health {
    Long healthMax;
    Long currentHealth;

    Person character;

    public Health(Long health, Long healthMax) {
        this.currentHealth = health;
        this.healthMax = healthMax;
    }
}
