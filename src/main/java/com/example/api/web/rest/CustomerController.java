package com.example.api.web.rest;

import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (name != null) {
            return new ResponseEntity<>(service.findByNameContaining(name), HttpStatus.OK);
        } else if (email != null) {
            return new ResponseEntity<>(service.findByEmailContaining(email), HttpStatus.OK);
        } else if (gender != null) {
            return new ResponseEntity<>(service.findByGender(gender), HttpStatus.OK);
        }

        Page<Customer> customers = service.getAllCustomers(PageRequest.of(page, size));
        return new ResponseEntity<>(customers.getContent(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer createdCustomer = service.createCustomer(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody Customer customer) {
        Customer updatedCustomer = service.updateCustomer(customerId, customer);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        service.deleteCustomer(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<Address>> getAddressesByCustomerId(@PathVariable Long customerId) {
        List<Address> addresses = service.getAddressesByCustomerId(customerId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<Address> createAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody Address address) {
        Address createdAddress = service.createAddress(customerId, address);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

	@PutMapping("/{customerId}/addresses/{addressId}")
	public ResponseEntity<Address> updateAddress(
			@PathVariable Long customerId,
			@PathVariable Long addressId,
			@Valid @RequestBody Address address) {
		Address updatedAddress = service.updateAddress(customerId, addressId, address);
		return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
	}

	@DeleteMapping("/{customerId}/addresses/{addressId}")
	public ResponseEntity<Void> deleteAddress(
			@PathVariable Long customerId,
			@PathVariable Long addressId) {
		service.deleteAddress(customerId, addressId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Customer>> searchCustomers(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String gender,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String state,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		if (city != null) {
			return new ResponseEntity<>(service.findByCity(city, page, size).getContent(), HttpStatus.OK);
		} else if (state != null) {
			return new ResponseEntity<>(service.findByState(state, page, size).getContent(), HttpStatus.OK);
		}
		
		Page<Customer> customers = service.getAllCustomers(PageRequest.of(page, size));
		return new ResponseEntity<>(customers.getContent(), HttpStatus.OK);
	}
}
