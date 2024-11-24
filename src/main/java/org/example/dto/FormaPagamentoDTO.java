package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormaPagamentoDTO {

    private String formaPagamento;

    @Override
    public String toString() {
        return formaPagamento;
    }

}
