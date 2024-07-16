package com.liushukov.TheRavenTechTask.services;

import com.liushukov.TheRavenTechTask.dtos.CustomerDTO;
import com.liushukov.TheRavenTechTask.models.Customer;
import com.liushukov.TheRavenTechTask.repositories.CustomerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerById(Long id){
        return customerRepository.findById(id).orElse(null);
    }

    public Optional<Customer> getCustomerByEmail(String email){
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getAllCustomers(String sortBy, String order, int pageNumber, int pageSize){
        Pageable pageable;
        switch (order) {
            case "desc" -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        }
        return customerRepository.findAll(pageable).getContent();
    }

    public Customer saveCustomer(CustomerDTO customerDTO){
        var customer = new Customer(
                customerDTO.fullName(),
                customerDTO.email(),
                customerDTO.phone()
        );
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer, CustomerDTO customerDTO){
        customer.setFullName(customerDTO.fullName());
        customer.setPhone(customerDTO.phone());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Customer customer){
        customer.setActive(false);
        customerRepository.save(customer);
    }
}
