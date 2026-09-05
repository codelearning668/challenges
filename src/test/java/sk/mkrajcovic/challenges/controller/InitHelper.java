package sk.mkrajcovic.challenges.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.stereotype.Component;

import io.restassured.RestAssured;

@Component
public class InitHelper {

    private int port;

    /*
     * Spring will randomly assign port for the running test container
     * this will need to be injected into the rest assured api so that it knows
     * where to call the app
     */
    @BeforeEach
    void setup() {
        init(port);
    }

    /**
     * Initialize the helper with port.
     * Should be called in @BeforeEach setup method of tests.
     * 
     * @param port The local server port
     */
    public void init(int port) {
        this.port = port;
        RestAssured.port = port;
    }
}
