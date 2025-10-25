import React, { useState, useEffect } from 'react';
import { Form, Button } from 'react-bootstrap';
import api from '../api/api';

const ClientForm = ({ client, onClose }) => {
  const [name, setName] = useState('');
  const [cpf, setCpf] = useState('');
  const [phones, setPhones] = useState([{ number: '', type: 'CELULAR' }]);
  const [emails, setEmails] = useState(['']);
  const [cep, setCep] = useState('');
  const [logradouro, setLogradouro] = useState('');
  const [bairro, setBairro] = useState('');
  const [localidade, setLocalidade] = useState('');
  const [uf, setUf] = useState('');
  const [complemento, setComplemento] = useState('');

  useEffect(() => {
    if (client) {
      setName(client.name);
      setCpf(client.cpf);
      setPhones(client.phones?.length ? client.phones : [{ number: '', type: 'CELULAR' }]);
      setEmails(client.emails?.length ? client.emails.map(e => e.email) : ['']);
      setCep(client.addresses?.length ? client.addresses[0].cep : '');
      setLogradouro(client.addresses?.length ? client.addresses[0].logradouro : '');
      setBairro(client.addresses?.length ? client.addresses[0].bairro : '');
      setLocalidade(client.addresses?.length ? client.addresses[0].localidade : '');
      setUf(client.addresses?.length ? client.addresses[0].uf : '');
      setComplemento(client.addresses?.length ? client.addresses[0].complemento : '');
    }
  }, [client]);

  useEffect(() => {
    const timer = setTimeout(async () => {
      const cepOnlyNumbers = cep.replace(/\D/g, ''); 

      if (cepOnlyNumbers.length === 8) {
        try {
          const res = await api.get(`/api/cep/${cepOnlyNumbers}`);
          const data = res.data;
          setLogradouro(data.logradouro || '');
          setBairro(data.bairro || '');
          setLocalidade(data.localidade || '');
          setUf(data.uf || '');
          setComplemento(data.complemento || '');
        } catch (err) {
          console.error('Erro ao buscar CEP:', err);
        }
      } else if (cepOnlyNumbers.length === 0) {
        setLogradouro('');
        setBairro('');
        setLocalidade('');
        setUf('');
        setComplemento('');
      }
    }, 500);

    return () => clearTimeout(timer);
  }, [cep]);

  
  const handlePhoneChange = (index, field, value) => {
    const updated = [...phones];
    updated[index][field] = value;
    setPhones(updated);
  };
  const addPhone = () => setPhones([...phones, { number: '', type: 'CELULAR' }]);
  const removePhone = (index) => setPhones(phones.filter((_, i) => i !== index));

 
  const handleEmailChange = (index, value) => {
    const updated = [...emails];
    updated[index] = value;
    setEmails(updated);
  };
  const addEmail = () => setEmails([...emails, '']);
  const removeEmail = (index) => setEmails(emails.filter((_, i) => i !== index));

  
  const handleSubmit = async (e) => {
    e.preventDefault();

    const data = {
      name,
      cpf,
      phones,
      emails: emails.map(email => ({ email })),
      addresses: [{
        cep,
        logradouro,
        bairro,
        localidade,
        uf,
        complemento
      }]
    };

    try {
      if (client) {
        await api.put(`/api/clients/${client.id}`, data);
      } else {
        await api.post('/api/clients', data);
      }
      onClose();
    } catch (err) {
      console.error('Erro ao salvar cliente:', err);
      alert('Erro ao salvar cliente. Verifique os dados.');
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Form.Group className="mb-3">
        <Form.Label>Nome</Form.Label>
        <Form.Control value={name} onChange={e => setName(e.target.value)} required />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>CPF</Form.Label>
        <Form.Control value={cpf} onChange={e => setCpf(e.target.value)} required />
      </Form.Group>

      <Form.Label>Telefones</Form.Label>
      {phones.map((p, i) => (
        <div key={i} className="d-flex mb-2 gap-2">
          <Form.Control
            value={p.number}
            onChange={e => handlePhoneChange(i, 'number', e.target.value)}
            placeholder="Número"
            required
          />
          <Form.Select
            value={p.type}
            onChange={e => handlePhoneChange(i, 'type', e.target.value)}
          >
            <option value="RESIDENCIAL">RESIDENCIAL</option>
            <option value="COMERCIAL">COMERCIAL</option>
            <option value="CELULAR">CELULAR</option>
          </Form.Select>
          {phones.length > 1 && <Button variant="danger" onClick={() => removePhone(i)}>Remover</Button>}
        </div>
      ))}
      <Button variant="secondary" onClick={addPhone} className="mb-3">Adicionar Telefone</Button>

      <Form.Group className='mb-2'>Emails</Form.Group>
      {emails.map((e, i) => (
        <div key={i} className="d-flex mb-2 gap-2">
          <Form.Control
            type="email"
            value={e}
            onChange={ev => handleEmailChange(i, ev.target.value)}
            placeholder="Email"
            required
          />
          {emails.length > 1 && <Button variant="danger" onClick={() => removeEmail(i)}>Remover</Button>}
        </div>
      ))}
      <Button variant="secondary" onClick={addEmail} className="mb-3">Adicionar Email</Button>

      <Form.Group className="mb-3">
        <Form.Label>CEP</Form.Label>
        <Form.Control
          value={cep}
          onChange={e => {
            const onlyNumbers = e.target.value.replace(/\D/g, '');
            let formatted = onlyNumbers;
            if (onlyNumbers.length > 5) formatted = onlyNumbers.slice(0,5) + '-' + onlyNumbers.slice(5,8);
            setCep(formatted);
          }}
          placeholder="Digite o CEP"
          required
        />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>Logradouro</Form.Label>
        <Form.Control value={logradouro} onChange={e => setLogradouro(e.target.value)} required />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>Bairro</Form.Label>
        <Form.Control value={bairro} onChange={e => setBairro(e.target.value)} required />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>Cidade</Form.Label>
        <Form.Control value={localidade} onChange={e => setLocalidade(e.target.value)} required />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>UF</Form.Label>
        <Form.Control value={uf} onChange={e => setUf(e.target.value)} required />
      </Form.Group>

      <Form.Group className="mb-3">
        <Form.Label>Complemento</Form.Label>
        <Form.Control value={complemento} onChange={e => setComplemento(e.target.value)} placeholder="Opcional" />
      </Form.Group>

      <Button type="submit" className="w-100">Salvar</Button>
    </Form>
  );
};

export default ClientForm;
