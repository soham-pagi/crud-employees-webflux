package com.learning.springboot;

import com.learning.springboot.dto.EmployeeDto;
import com.learning.springboot.repository.EmployeeRepository;
import com.learning.springboot.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIntegrationTests {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void before() {
        System.out.println("Before Each Test");
        employeeRepository.deleteAll().subscribe();
    }

    @Test
    public void testSaveEmployee() {
        var employeeDto = new EmployeeDto(null, "Shreyas", "Naik", "naiksp@gmail.com");

        webTestClient.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDto), EmployeeDto.class)
                .exchange()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void testGetSingleEmployee() {
        var employeeDto = new EmployeeDto(null, "John", "Cena", "john@gmail.com");

        var savedEmployee = employeeService.saveEmployee(employeeDto).block();

       webTestClient.get().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
               .exchange()
               .expectStatus().isOk()
               .expectBody()
               .consumeWith(System.out::println)
               .jsonPath("$.id").isEqualTo(savedEmployee.getId())
               .jsonPath("$.firstName").isEqualTo(savedEmployee.getFirstName())
               .jsonPath("$.lastName").isEqualTo(savedEmployee.getLastName())
               .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());
    }

    @Test
    public void getAllEmployees() {
        var employeeDto1 = new EmployeeDto(null, "John", "Cena", "john@gmail.com");
        var employeeDto2 = new EmployeeDto(null, "Shreyas", "Naik", "naiksp@gmail.com");

        employeeService.saveEmployee(employeeDto1).block();
        employeeService.saveEmployee(employeeDto2).block();

        webTestClient.get().uri("/api/employees")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void testUpdateEmployee() {
        var employeeDto = new EmployeeDto(null, "Soham", "Pagui", "soham@gmail.com");
        var savedEmployee = employeeService.saveEmployee(employeeDto).block();

        var updatedEmployee = new EmployeeDto(null, "Soham", "Pagi", "pagi@gmail.com");

        webTestClient.put().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedEmployee), EmployeeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(updatedEmployee.getFirstName())
                .jsonPath("$.lastName").isEqualTo(updatedEmployee.getLastName())
                .jsonPath("$.email").isEqualTo(updatedEmployee.getEmail());
    }

    @Test
    public void testDeleteEmployee() {
        var employeeDto = new EmployeeDto(null, "Soham", "Pagi", "soham@gmail.com");
        var savedEmployee = employeeService.saveEmployee(employeeDto).block();

        webTestClient.delete().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);
    }

}
