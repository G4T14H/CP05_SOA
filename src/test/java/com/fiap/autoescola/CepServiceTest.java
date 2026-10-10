package com.fiap.autoescola;
import com.fiap.autoescola.service.CepService;
import com.fiap.autoescola.exception.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
class CepServiceTest {
 MockRestServiceServer server;CepService service;
 @BeforeEach void setup(){var builder=RestClient.builder().baseUrl("https://viacep.com.br/ws");server=MockRestServiceServer.bindTo(builder).build();service=new CepService(builder.build());}
 @ParameterizedTest @ValueSource(strings={"01001000","01001-000"}) void consultaRealHttpSimulada(String cep){server.expect(requestTo("https://viacep.com.br/ws/01001000/json/")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess("{\"cep\":\"01001-000\",\"logradouro\":\"Praça da Sé\",\"localidade\":\"São Paulo\",\"uf\":\"SP\"}",MediaType.APPLICATION_JSON));assertEquals("SP",service.consultar(cep).uf());server.verify();}
 @ParameterizedTest @ValueSource(strings={"abc","123","123456789","../alunos"}) void formatoInvalido(String cep){assertThrows(IllegalArgumentException.class,()->service.consultar(cep));server.verify();}
 @Test void inexistente(){server.expect(anything()).andRespond(withSuccess("{\"erro\":true}",MediaType.APPLICATION_JSON));assertThrows(ResourceNotFoundException.class,()->service.consultar("00000000"));server.verify();}
 @Test void indisponivel(){server.expect(anything()).andRespond(withServerError());assertThrows(ApiExternaException.class,()->service.consultar("01001000"));server.verify();}
 @Test void jsonInvalido(){server.expect(anything()).andRespond(withSuccess("nao-json",MediaType.APPLICATION_JSON));assertThrows(ApiExternaException.class,()->service.consultar("01001000"));server.verify();}
 @Test void vazio(){server.expect(anything()).andRespond(withSuccess("",MediaType.APPLICATION_JSON));assertThrows(ApiExternaException.class,()->service.consultar("01001000"));server.verify();}
}
