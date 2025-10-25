import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import ClientList from '../components/ClientList';
import { Container } from 'react-bootstrap';
import api from '../api/api';

const UserDashboard = () => {
  const [clients, setClients] = useState([]);

  const fetchClients = async () => {
    try {
      const res = await api.get('/api/clients');
      setClients(res.data);
    } catch (err) {
      console.error('Erro ao buscar clientes:', err);
    }
  };

  useEffect(() => {
    fetchClients();
  }, []);

  return (
    <>
      <Navbar />
      <Container className="mt-4">
        <h2>Dashboard Usuário</h2>
        <ClientList clients={clients} editable={false} />
      </Container>
    </>
  );
};

export default UserDashboard;
