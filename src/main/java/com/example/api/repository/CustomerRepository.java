package br.com.example.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import br.com.example.api.domain.Customer;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findAllByOrderByNameAsc();

    List<Customer> findByNameContainingIgnoreCase(String name);

    List<Customer> findByEmailContainingIgnoreCase(String email);

    List<Customer> findByGenderIgnoreCase(String gender);

    Page<Customer> findAll(Pageable pageable);

    boolean existsByEmailIgnoreCase(String email);

	Page<Customer> findByAddressCityContainingIgnoreCase(String city, Pageable pageable);

    Page<Customer> findByAddressStateContainingIgnoreCase(String state, Pageable pageable);

    Page<Customer> findByAddressCityIgnoreCase(String city, Pageable pageable);

    Page<Customer> findByAddressStateIgnoreCase(String state, Pageable pageable);

}
