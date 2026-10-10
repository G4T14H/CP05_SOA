package com.fiap.autoescola.service;

import com.fiap.autoescola.dto.ViaCepResponse;
import com.fiap.autoescola.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
@Service
public class CepService {
    private final RestClient client;
    public CepService(RestClient viaCepClient){client=viaCepClient;}
    public ViaCepResponse consultar(String cep){
        if(!cep.matches("[0-9]{5}-?[0-9]{3}"))throw new IllegalArgumentException("CEP deve conter 8 dígitos, com hífen opcional");
        try{
            var endereco=client.get().uri("/{cep}/json/",cep.replace("-","")).retrieve().body(ViaCepResponse.class);
            if(endereco==null)throw new ApiExternaException("Resposta vazia do ViaCEP");
            if(Boolean.TRUE.equals(endereco.erro()))throw new ResourceNotFoundException("CEP não encontrado");
            return endereco;
        }catch(RestClientException ex){throw new ApiExternaException("ViaCEP indisponível ou resposta inválida. Tente novamente mais tarde");}
    }
}
