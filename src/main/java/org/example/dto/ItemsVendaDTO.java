package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsVendaDTO {

    private Integer idProduto;
    private String nomeProduto;
    private Integer codigoDeBarras;
    private Integer quantidade;
    private BigDecimal precoUnitario;

}
