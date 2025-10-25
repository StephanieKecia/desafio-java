import React, { useState, useEffect } from 'react';
import { Container, Button, Modal } from 'react-bootstrap';
import Navbar from '../components/Navbar';
import ClientList from '../components/ClientList';
import ClientForm from '../components/ClientForm';
import api from '../api/api';

const AdminDashboard = () => {
  const [showModal, setShowModal] = useState(false);
  const [editingClient, setEditingClient] = useState(null);
  const [clients, setClients] = useState([]);

  // Buscar clientes
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

  // Abrir modal para criar
  const handleCreate = () => {
    setEditingClient(null);
    setShowModal(true);
  };

  // Abrir modal para editar
  const handleEdit = (client) => {
    setEditingClient(client);
    setShowModal(true);
  };

  // Fechar modal e atualizar lista
  const handleClose = () => {
    setShowModal(false);
    fetchClients();
  };

  // Deletar cliente
  const handleDelete = async (clientId) => {
    if (window.confirm('Tem certeza que deseja deletar este cliente?')) {
      try {
        await api.delete(`/api/clients/${clientId}`);
        fetchClients();
      } catch (err) {
        console.error('Erro ao deletar cliente:', err);
      }
    }
  };

  return (
    <>
      <Navbar />
      <Container className="mt-4">
        <h2>Dashboard Admin</h2>
        <Button className="mb-3" onClick={handleCreate}>Criar Cliente</Button>
        <ClientList 
          clients={clients} 
          editable={true} 
          onEdit={handleEdit} 
          onDelete={handleDelete} 
        />

        <Modal show={showModal} onHide={handleClose}>
          <Modal.Header closeButton>
            <Modal.Title>{editingClient ? 'Editar Cliente' : 'Criar Cliente'}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <ClientForm client={editingClient} onClose={handleClose} />
          </Modal.Body>
        </Modal>
      </Container>
    </>
  );
};

export default AdminDashboard;
