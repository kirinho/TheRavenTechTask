package com.liushukov.TheRavenTechTask.controllers;

import com.liushukov.TheRavenTechTask.dtos.CustomerDTO;
import com.liushukov.TheRavenTechTask.services.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<Object> viewCustomers(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ){
        try {
            var response = customerService.getAllCustomers(sortBy, order, pageNumber, pageSize);
            logger.info("get list of customers");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException exception){
            logger.error("invalid request data: " + exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        } catch (Exception exception) {
            logger.error("unexpected error: " + exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Object> viewCustomer(@PathVariable Long customerId){
        try {
            var customer = customerService.getCustomerById(customerId);
            logger.info("checking existence of customer");
            if (customer != null){
                logger.info("return customer");
                return ResponseEntity.status(HttpStatus.OK).body(customer);
            }
            logger.info("customer doesn't exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer doesn't exist");
        } catch (IllegalArgumentException exception){
            logger.error("invalid request data: " + exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        } catch (Exception exception) {
            logger.error("unexpected error: " + exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping
    public ResponseEntity<Object> addCustomer(@Valid @RequestBody CustomerDTO customerDTO){
        try {
            logger.info("checking if user exist with that email");
            var existedEmail = customerService.getCustomerByEmail(customerDTO.email());
            if (existedEmail.isEmpty()) {
                logger.info("created customer and return response");
                var customer = customerService.saveCustomer(customerDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(customer);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Customer with that email already exists");
        } catch (IllegalArgumentException exception){
            logger.error("invalid request data: " + exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        } catch (Exception exception) {
            logger.error("unexpected error: " + exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Object> updateCustomer(@PathVariable Long customerId,
                                                 @Valid @RequestBody CustomerDTO customerDTO){
        try {
            logger.info("checking existence of customer");
            var existedCustomer = customerService.getCustomerById(customerId);
            if (existedCustomer != null){
                if (existedCustomer.isActive()) {
                    logger.info("updated customer and return response");
                    var response = customerService.updateCustomer(existedCustomer, customerDTO);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                }
                logger.info("customer doesn't exist");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is not active");
            } return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer doesn't exist");
        } catch (IllegalArgumentException exception){
            logger.error("invalid request data: " + exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        } catch (Exception exception) {
            logger.error("unexpected error: " + exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable Long customerId){
        try {
            logger.info("checking existence of customer");
            var existedCustomer = customerService.getCustomerById(customerId);
            if (existedCustomer != null){
                if (existedCustomer.isActive()) {
                    logger.info("set false for user's activity and return response");
                    customerService.deleteCustomer(existedCustomer);
                    return ResponseEntity.status(HttpStatus.OK).body("Customer's status is modified on inactive");
                }
                logger.info("customer doesn't exist");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is already not active");
            } return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer doesn't exist");
        } catch (IllegalArgumentException exception){
            logger.error("invalid request data: " + exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data");
        } catch (Exception exception) {
            logger.error("unexpected error: " + exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
