package desafio.java.controller;

import desafio.java.dto.AddressResponseDTO;
import desafio.java.service.AddressService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private AddressResponseDTO mockAddress;

    @Before
    public void setUp() {

        mockAddress = new AddressResponseDTO();
        mockAddress.setCep("12345678");
        mockAddress.setLogradouro("Rua Teste");
        mockAddress.setBairro("Bairro Teste");
        mockAddress.setCidade("Cidade Teste");
        mockAddress.setUf("SP");
        mockAddress.setComplemento("Apto 1");

        Mockito.when(addressService.fetchFromCep(anyString())).thenReturn(mockAddress);
    }

    @Test
    public void testGetAddress() {
        AddressResponseDTO response = addressController.getAddress("12345678");

        assertEquals("12345-678", response.getCep());
        assertEquals("Rua Teste", response.getLogradouro());
        assertEquals("Bairro Teste", response.getBairro());
        assertEquals("Cidade Teste", response.getCidade());
        assertEquals("SP", response.getUf());
        assertEquals("Apto 1", response.getComplemento());

        Mockito.verify(addressService).fetchFromCep("12345678");
    }
}

