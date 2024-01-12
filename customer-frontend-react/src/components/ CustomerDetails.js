import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

// Exemplo de Context API para gerenciamento de estado global
const CustomerContext = React.createContext();

const CustomerProvider = ({ children }) => {
  const [customer, setCustomer] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchCustomerDetails = async (customerId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/customers/${customerId}`);
      setCustomer(response.data);
    } catch (error) {
      setError(error.message || 'Erro ao buscar detalhes do cliente');
    } finally {
      setLoading(false);
    }
  };

  return (
    <CustomerContext.Provider value={{ customer, loading, error, fetchCustomerDetails }}>
      {children}
    </CustomerContext.Provider>
  );
};

const useCustomer = () => {
  const context = useContext(CustomerContext);
  if (!context) {
    throw new Error('useCustomer must be used within a CustomerProvider');
  }
  return context;
};

const CustomerDetails = () => {
  const { id } = useParams();
  const { customer, loading, error, fetchCustomerDetails } = useCustomer();

  useEffect(() => {
    fetchCustomerDetails(id);
  }, [id, fetchCustomerDetails]);

  return (
    <div>
      <h2>Detalhes do Cliente</h2>
      {loading && <p>Carregando...</p>}
      {error && <p>Erro: {error}</p>}
      {customer && (
        <div>
          <p>Nome: {customer.name}</p>
          <p>Email: {customer.email}</p>
          {/* Adicione mais detalhes conforme necessário */}
        </div>
      )}
    </div>
  );
};

export { CustomerProvider, useCustomer, CustomerDetails };
