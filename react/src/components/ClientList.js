import { Table, Button } from 'react-bootstrap';

const ClientList = ({ clients, editable, onEdit, onDelete }) => {
  return (
    <Table striped bordered hover>
      <thead>
        <tr>
          <th>Nome</th>
          <th>CPF</th>
          <th>Telefone</th>
          <th>Email</th>
          <th>CEP</th>
          {editable && <th>Ações</th>}
        </tr>
      </thead>
      <tbody>
        {clients.map(client => (
          <tr key={client.id}>
            <td>{client.name}</td>
            <td>{client.cpf}</td>
            <td>{client.phones?.[0]?.number}</td>
            <td>{client.emails?.[0]?.email}</td>
            <td>{client.addresses?.[0]?.cep}</td>
            {editable && (
              <td>
                <Button variant="warning" size="sm" onClick={() => onEdit(client)}>Editar</Button>{' '}
                <Button variant="danger" size="sm" onClick={() => onDelete(client.id)}>Deletar</Button>
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default ClientList;
