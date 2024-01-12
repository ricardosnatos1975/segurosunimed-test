@Service
public class CustomerService {

    private final CustomerRepository repository;

    @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> findAll() {
        return repository.findAllByOrderByNameAsc();
    }

    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }

    public List<Customer> findByNameContaining(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public List<Customer> findByEmailContaining(String email) {
        return repository.findByEmailContainingIgnoreCase(email);
    }

    public List<Customer> findByGender(String gender) {
        return repository.findByGenderIgnoreCase(gender);
    }

    public Page<Customer> getAllCustomers(int page, int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Customer createCustomer(@Valid Customer customer) {
        validateCustomerData(customer);

        if (customer.getAddress() != null && customer.getAddress().getZipCode() != null) {
            Address.getAddressByCep(customer.getAddress().getZipCode())
                    .subscribe(
                            address -> customer.setAddress(address),
                            error -> System.err.println("Failed to fetch address from ViaCEP: " + error.getMessage())
                    );
        }

        return repository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long customerId, @Valid Customer updatedCustomer) {
        validateCustomerData(updatedCustomer);
        Customer existingCustomer = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        existingCustomer.setGender(updatedCustomer.getGender());

        return existingCustomer;
    }

    @Transactional
    public Address updateAddress(Long customerId, Long addressId, @Valid Address updatedAddress) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Address addressToUpdate = customer.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (!customer.getAddresses().contains(addressToUpdate)) {
            throw new IllegalArgumentException("Address does not belong to the customer");
        }

        addressToUpdate.setStreet(updatedAddress.getStreet());
        addressToUpdate.setCity(updatedAddress.getCity());
        addressToUpdate.setState(updatedAddress.getState());
        addressToUpdate.setZipCode(updatedAddress.getZipCode());

        return addressToUpdate;
    }

    @Transactional
    public void deleteCustomer(Long customerId) {
        if (!repository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }

        repository.deleteById(customerId);
    }

    @Transactional
    public void deleteAddress(Long customerId, Long addressId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Address addressToRemove = customer.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        if (!customer.getAddresses().contains(addressToRemove)) {
            throw new IllegalArgumentException("Address does not belong to the customer");
        }

        customer.getAddresses().remove(addressToRemove);
        repository.save(customer);
    }

    private void validateCustomerData(Customer customer) {
        if (customer.getEmail() != null && repository.existsByEmailIgnoreCase(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public class Address {

        private String cep;

        public static Mono<Address> getAddressByCep(String cep) {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";

            return WebClient.create()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ViaCepResponse.class)
                    .map(viaCepResponse -> {
                        Address address = new Address();
                        address.setCep(viaCepResponse.getCep());
                        return address;
                    });
        }

        private static class ViaCepResponse {

            private String cep;

        }
    }

    public Page<Customer> findByCity(String city, int page, int size) {
        return repository.findByAddressCityIgnoreCase(city, PageRequest.of(page, size));
    }

    public Page<Customer> findByState(String state, int page, int size) {
        return repository.findByAddressStateIgnoreCase(state, PageRequest.of(page, size));
    }
}
