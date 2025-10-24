package desafio.java.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponseDTO {

    private String cep;
    private String logradouro;
    private String bairro;
    @JsonProperty("localidade")
    private String cidade;
    private String uf;
    private String complemento;

    @JsonProperty("cep")
    public String getCep() {
        if (cep != null && cep.matches("\\d{8}")) {
            return cep.substring(0, 5) + "-" + cep.substring(5);
        }
        return cep;
    }

}
