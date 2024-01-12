import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    public void testCreateCustomer() {
        Customer customerToCreate = new Customer();

        when(customerRepository.save(any(Customer.class))).thenReturn(customerToCreate);

        ResponseEntity<Customer> responseEntity = customerService.createCustomer(customerToCreate);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(customerToCreate, responseEntity.getBody());
    }

    @Test
    public void testCreateCustomerWithDuplicateEmail() {

        Customer customerToCreate = new Customer();
        customerToCreate.setEmail("existing@example.com");

        when(customerRepository.save(any(Customer.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(customerToCreate));
    }

    @Test
    public void testUpdateCustomer() {

        Long customerId = 1L;
        Customer existingCustomer = new Customer();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        Customer updatedCustomerData = new Customer();

        ResponseEntity<Customer> responseEntity = customerService.updateCustomer(customerId, updatedCustomerData);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals
