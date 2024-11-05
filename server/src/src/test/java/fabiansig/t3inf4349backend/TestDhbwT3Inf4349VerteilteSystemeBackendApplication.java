package fabiansig.t3inf4349backend;

import org.springframework.boot.SpringApplication;

public class TestDhbwT3Inf4349VerteilteSystemeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(DhbwT3Inf4349VerteilteSystemeBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
