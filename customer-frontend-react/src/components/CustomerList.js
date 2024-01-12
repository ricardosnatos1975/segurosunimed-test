import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const CustomerList = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/customers`, {
        params: {
          name: search,
          page: currentPage - 1,
          size: pageSize,
        },
      });
      setCustomers(response.data);
      setTotalPages(response.headers['x-total-pages']);
    } catch (error) {
      setError(error.message || 'Erro ao buscar clientes');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [search, currentPage, pageSize]);

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div>
      <h2>Lista de Clientes</h2>
      <input
        type="text"
        placeholder="Pesquisar por nome"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      {loading && <p>Carregando...</p>}
      {error && <p>Erro: {error}</p>}
      <ul>
        {customers.map((customer) => (
          <li key={customer.id}>
            <Link to={`/customer/${customer.id}`}>{customer.name}</Link>
          </li>
        ))}
      </ul>
      <Pagination
        totalPages={parseInt(totalPages)}
        currentPage={currentPage}
        onPageChange={handlePageChange}
      />
    </div>
  );
};

const Pagination = ({ totalPages, currentPage, onPageChange }) => {
  const pages = Array.from({ length: totalPages }, (_, index) => index + 1);

  return (
    <div>
      {pages.map((page) => (
        <button
          key={page}
          onClick={() => onPageChange(page)}
          style={{ fontWeight: currentPage === page ? 'bold' : 'normal' }}
        >
          {page}
        </button>
      ))}
    </div>
  );
};

export default CustomerList;
