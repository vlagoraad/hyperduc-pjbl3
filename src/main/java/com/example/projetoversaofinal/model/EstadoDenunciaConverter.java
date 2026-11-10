package com.example.projetoversaofinal.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Converte o objeto de estado (padrão State) em texto para a coluna "status" e vice-versa.
// Assim o State continua sendo um objeto com comportamento em Java,
// mas no banco aparece só como VARCHAR (ex.: "Aberta").
@Converter
public class EstadoDenunciaConverter implements AttributeConverter<EstadoDenuncia, String> {

    @Override
    public String convertToDatabaseColumn(EstadoDenuncia estado) {
        return estado == null ? null : estado.getClass().getSimpleName();
    }

    @Override
    public EstadoDenuncia convertToEntityAttribute(String valor) {
        if (valor == null) {
            return null;
        }
        return switch (valor) {
            case "Aberta" -> new EstadoDenuncia.Aberta();
            case "EmAnalise" -> new EstadoDenuncia.EmAnalise();
            case "Procedente" -> new EstadoDenuncia.Procedente();
            case "Arquivada" -> new EstadoDenuncia.Arquivada();
            default -> throw new IllegalArgumentException("Status desconhecido no banco: " + valor);
        };
    }
}
