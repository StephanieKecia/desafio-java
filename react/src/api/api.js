import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Intercepta requisições e adiciona o token JWT
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Função auxiliar para tentar renovar o token
async function refreshJwtToken() {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return null;

    const response = await axios.post('http://localhost:8080/auth/refresh', {
      refreshToken,
    });

    const newToken = response.data.token;
    localStorage.setItem('token', newToken);
    return newToken;
  } catch (error) {
    console.error('Erro ao tentar atualizar o token:', error);
    return null;
  }
}

// Intercepta respostas com erro (ex: token expirado)
api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;

    // Se o token expirou (401) e ainda não tentamos atualizar
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const newToken = await refreshJwtToken();
      if (newToken) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
        return api(originalRequest); // Reenvia a requisição com o novo token
      } else {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login'; // Redireciona o usuário para login
      }
    }

    return Promise.reject(error);
  }
);

export default api;
