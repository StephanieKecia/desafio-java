import React from 'react';
import { Navbar as BSNavbar, Container, Nav, Button } from 'react-bootstrap';
import { logout, getRole } from '../utils/auth';
import { useNavigate } from 'react-router-dom';

const Navbar = () => {
  const navigate = useNavigate();
  const role = getRole();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <BSNavbar bg="dark" variant="dark" expand="lg">
      <Container>
        <BSNavbar.Brand href="#">Sistema Clientes</BSNavbar.Brand>
        <Nav className="ms-auto">
          {role && <Button variant="outline-light" onClick={handleLogout}>Logout</Button>}
        </Nav>
      </Container>
    </BSNavbar>
  );
};

export default Navbar;
