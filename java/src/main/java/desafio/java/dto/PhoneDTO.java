package desafio.java.dto;

import desafio.java.enums.PhoneType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {

    @NotNull(message = "O tipo de telefone é obrigatório")
    private PhoneType type;

    @NotBlank(message = "O número do telefone é obrigatório")
    @Pattern(regexp = "^[0-9()\\-\\s]+$", message = "Número inválido")
    private String number;

    public void setNumber(String number) {
        if (number != null) {
            this.number = number.replaceAll("\\D", "");
        } else {
            this.number = null;
        }
    }


    public String getNumberMasked() {
        if (number == null) return null;

        switch (type) {
            case CELULAR:
                if (number.length() == 11) {
                    return number.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
                }
                break;
            case RESIDENCIAL:
            case COMERCIAL:
                if (number.length() == 10) {
                    return number.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
                }
                break;
        }
        return number;
    }
}
