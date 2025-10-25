import api from '../api/api';

export const login = (token, role) => {
  localStorage.setItem('token', token);
  localStorage.setItem('role', role);
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('role');
  delete api.defaults.headers.common['Authorization'];
};

export const isAuthenticated = () => !!localStorage.getItem('token');

export const getRole = () => localStorage.getItem('role');
