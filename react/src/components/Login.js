import React, { useState } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';
import { login } from '../utils/auth';
import { Form, Button, Alert, Container, Card } from 'react-bootstrap';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/auth/login', { username, password },
        {headers: {"Content-Type": "application/json"}}
      );
      console.log('Resposta do login:', res.data);
      login(res.data.token, res.data.role);
      if (res.data.role === 'ROLE_ADMIN') navigate('/admin');
      else navigate('/user');
    } catch (err) {
      setError('Usuário ou senha inválidos');
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
      <Card style={{ padding: '2rem', width: '350px' }}>
        <h3 className="mb-4 text-center">Login</h3>
        {error && <Alert variant="danger">{error}</Alert>}
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Usuário</Form.Label>
            <Form.Control value={username} onChange={e => setUsername(e.target.value)} placeholder="Digite seu usuário"/>
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Senha</Form.Label>
            <Form.Control type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Digite sua senha"/>
          </Form.Group>
          <Button type="submit" className="w-100">Entrar</Button>
        </Form>
      </Card>
    </Container>
  );
};

export default Login;
