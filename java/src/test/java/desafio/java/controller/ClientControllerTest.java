package desafio.java.controller;

import desafio.java.dto.ClientRequestDTO;
import desafio.java.dto.ClientResponseDTO;
import desafio.java.dto.EmailDTO;
import desafio.java.service.ClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@RunWith(MockitoJUnitRunner.class)
public class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private ClientRequestDTO requestDTO;
    private ClientResponseDTO responseDTO;

    @Before
    public void setUp() {
        requestDTO = new ClientRequestDTO();
        requestDTO.setName("John Doe");
        EmailDTO email = new EmailDTO();
        email.setEmail("john@example.com");
        requestDTO.setEmails(Collections.singletonList(email));

        responseDTO = new ClientResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("John Doe");
        EmailDTO emailTwo = new EmailDTO();
        emailTwo.setEmail("john@example.com");
        requestDTO.setEmails(Collections.singletonList(emailTwo));
    }

    @Test
    public void testCreate() {
        Mockito.when(clientService.create(any(ClientRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.create(requestDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        assertEquals("/api/clients/1", response.getHeaders().getLocation().toString());
    }

    @Test
    public void testGetById() {
        Mockito.when(clientService.findById(anyLong())).thenReturn(responseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.getById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testGetAll() {
        List<ClientResponseDTO> clients = Arrays.asList(responseDTO);
        Mockito.when(clientService.findAll()).thenReturn(clients);

        ResponseEntity<List<ClientResponseDTO>> response = clientController.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(clients, response.getBody());
    }

    @Test
    public void testUpdate() {
        Mockito.when(clientService.update(anyLong(), any(ClientRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ClientResponseDTO> response = clientController.update(1L, requestDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    public void testDelete() {
        Mockito.doNothing().when(clientService).delete(anyLong());

        ResponseEntity<Void> response = clientController.delete(1L);

        assertEquals(204, response.getStatusCodeValue());
        Mockito.verify(clientService).delete(1L);
    }

    @Test
    public void testDadosAdmin() {
        String result = clientController.dadosAdmin();
        assertEquals("Acesso permitido apenas ao ADMIN.", result);
    }

    @Test
    public void testDadosUser() {
        String result = clientController.dadosUser();
        assertEquals("Acesso permitido ao USER e ADMIN.", result);
    }
}

