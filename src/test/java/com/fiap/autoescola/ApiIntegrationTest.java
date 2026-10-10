package com.fiap.autoescola;
import com.fasterxml.jackson.databind.*;
import com.fiap.autoescola.repository.*;
import com.fiap.autoescola.domain.entity.Usuario;
import com.fiap.autoescola.domain.enums.RoleUsuario;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
@SpringBootTest @AutoConfigureMockMvc @Import(ApiIntegrationTest.TimeConfig.class)
class ApiIntegrationTest {
 @TestConfiguration static class TimeConfig {
  @Bean @Primary Clock testClock(){return Clock.fixed(LocalDateTime.of(2030,6,3,8,0).atZone(ZoneId.of("America/Sao_Paulo")).toInstant(),ZoneId.of("America/Sao_Paulo"));}
  @Bean @Primary LocalValidatorFactoryBean testValidator(Clock clock){var v=new LocalValidatorFactoryBean();v.setConfigurationInitializer(c->c.clockProvider(()->clock));return v;}
 }
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired AlunoRepository alunos; @Autowired InstrutorRepository instrutores; @Autowired InstrucaoRepository aulas; @Autowired UsuarioRepository usuarios; @Autowired PasswordEncoder encoder;
 @BeforeEach void limpar(){aulas.deleteAll();alunos.deleteAll();instrutores.deleteAll();usuarios.deleteAll();usuarios.save(Usuario.builder().username("admin").senha(encoder.encode("admin123")).role(RoleUsuario.ADMIN).ativo(true).build());}
 Map<String,Object> endereco(){return Map.of("logradouro","Rua A","numero","1","bairro","Centro","cidade","São Paulo","uf","SP","cep","01001000");}
 ResultActions admin(MockHttpServletRequestBuilder r)throws Exception{return mvc.perform(r.with(user("admin").roles("ADMIN")));}
 MockHttpServletRequestBuilder body(MockHttpServletRequestBuilder r,Object b)throws Exception{return r.contentType("application/json").content(json.writeValueAsString(b));}
 JsonNode node(ResultActions r)throws Exception{return json.readTree(r.andReturn().getResponse().getContentAsString());}
 long pessoa(String tipo,String nome)throws Exception{var b=new HashMap<String,Object>();b.put("nome",nome);b.put("email",nome+"@teste.com");b.put("telefone","11999999999");b.put("endereco",endereco());if(tipo.equals("alunos"))b.put("cpf",nome);else {b.put("cnh",nome);b.put("especialidade","CARROS");}return node(admin(body(post("/"+tipo),b)).andExpect(status().isCreated())).get("id").asLong();}
 Map<String,Object> agenda(long a,Long i,String data){var b=new HashMap<String,Object>();b.put("alunoId",a);if(i!=null)b.put("instrutorId",i);b.put("dataHora",data);return b;}
 long aula(long a,Long i,String data)throws Exception{return node(admin(body(post("/instrucoes"),agenda(a,i,data))).andExpect(status().isCreated())).get("id").asLong();}
 String login(String nome,String senha)throws Exception{return node(mvc.perform(body(post("/auth/login"),Map.of("username",nome,"senha",senha))).andExpect(status().isOk())).get("token").asText();}
 @ParameterizedTest @ValueSource(strings={"alunos","instrutores"}) void crudPreservaImutaveisEExclusaoLogica(String tipo)throws Exception {
  long id=pessoa(tipo,"Ana");admin(get("/"+tipo+"/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.ativo").value(true));
  admin(body(put("/"+tipo+"/"+id),Map.of("nome","Beatriz","telefone","11888888888","endereco",endereco(),"email","alterado@teste.com","cpf","alterado","cnh","alterado","especialidade","MOTOS"))).andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Beatriz")).andExpect(jsonPath("$.email").value("Ana@teste.com"));
  admin(delete("/"+tipo+"/"+id)).andExpect(status().isNoContent());admin(get("/"+tipo)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
  assertTrue(tipo.equals("alunos")?!alunos.findById(id).orElseThrow().isAtivo():!instrutores.findById(id).orElseThrow().isAtivo());
 }
 @ParameterizedTest @ValueSource(strings={"alunos","instrutores","usuarios"}) void cadastroInvalido(String t)throws Exception{admin(body(post("/"+t),Map.of())).andExpect(status().isBadRequest());}
 @ParameterizedTest @ValueSource(strings={"alunos","instrutores","usuarios","instrucoes"}) void idAusente(String t)throws Exception{admin(get("/"+t+"/999999")).andExpect(status().isNotFound());}
 @ParameterizedTest @ValueSource(strings={"alunos","instrutores"}) void duplicidade(String t)throws Exception{pessoa(t,"Ana");var b=new HashMap<String,Object>();b.put("nome","Ana");b.put("email","Ana@teste.com");b.put("telefone","11");b.put("endereco",endereco());b.put(t.equals("alunos")?"cpf":"cnh","Ana");if(t.equals("instrutores"))b.put("especialidade","CARROS");admin(body(post("/"+t),b)).andExpect(status().isConflict());}
 @ParameterizedTest @ValueSource(strings={"alunos","instrutores"}) void paginaOrdenada(String t)throws Exception{pessoa(t,"Zoe");pessoa(t,"Ana");admin(get("/"+t)).andExpect(status().isOk()).andExpect(jsonPath("$.size").value(10)).andExpect(jsonPath("$.content[0].nome").value("Ana"));}
 @Test void autenticacaoReal()throws Exception{mvc.perform(get("/alunos")).andExpect(status().isUnauthorized());String token=login("admin","admin123");mvc.perform(get("/alunos").header("Authorization","Bearer "+token)).andExpect(status().isOk());}
 @Test void senhaErrada()throws Exception{mvc.perform(body(post("/auth/login"),Map.of("username","admin","senha","errada"))).andExpect(status().isUnauthorized());}
 @Test void tokenInvalido()throws Exception{mvc.perform(get("/alunos").header("Authorization","Bearer nao-e-jwt")).andExpect(status().isUnauthorized());}
 @Test void usuarioCrudPermissoesSenha()throws Exception{long id=node(admin(body(post("/usuarios"),Map.of("username","comum","senha","senha123","role","USER"))).andExpect(status().isCreated())).get("id").asLong();assertTrue(encoder.matches("senha123",usuarios.findById(id).orElseThrow().getSenha()));String token=login("comum","senha123");mvc.perform(get("/usuarios").header("Authorization","Bearer "+token)).andExpect(status().isForbidden());mvc.perform(get("/alunos").header("Authorization","Bearer "+token)).andExpect(status().isOk());mvc.perform(body(patch("/usuarios/me/senha"),Map.of("senhaAtual","senha123","novaSenha","nova123" )).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());login("comum","nova123");admin(body(put("/usuarios/"+id),Map.of("role","ADMIN","ativo",true))).andExpect(status().isOk());admin(delete("/usuarios/"+id)).andExpect(status().isNoContent());mvc.perform(get("/alunos").header("Authorization","Bearer "+token)).andExpect(status().isUnauthorized());}
 @ParameterizedTest @ValueSource(strings={"2030-06-06T05:59:00","2030-06-06T20:01:00","2030-06-09T10:00:00","2030-06-03T08:29:59"}) void horariosProibidos(String data)throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo");admin(body(post("/instrucoes"),agenda(a,i,data))).andExpect(status().isUnprocessableEntity());}
 @ParameterizedTest @ValueSource(strings={"2030-06-06T06:00:00","2030-06-06T20:00:00","2030-06-03T08:30:00","2030-06-08T10:00:00"}) void limitesPermitidos(String data)throws Exception{aula(pessoa("alunos","Ana"),pessoa("instrutores","Ivo"),data);}
 @ParameterizedTest @ValueSource(strings={"alunos","instrutores"}) void inativosNaoAgendam(String t)throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo");admin(delete("/"+t+"/"+(t.equals("alunos")?a:i))).andExpect(status().isNoContent());admin(body(post("/instrucoes"),agenda(a,i,"2030-06-06T10:00:00"))).andExpect(status().isUnprocessableEntity());}
 @Test void limiteDuasAulas()throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo");aula(a,i,"2030-06-06T10:00:00");aula(a,i,"2030-06-06T11:00:00");admin(body(post("/instrucoes"),agenda(a,i,"2030-06-06T12:00:00"))).andExpect(status().isUnprocessableEntity());}
 @Test void sobreposicaoInstrutor()throws Exception{long a=pessoa("alunos","Ana"),b=pessoa("alunos","Bia"),i=pessoa("instrutores","Ivo");aula(a,i,"2030-06-06T10:00:00");admin(body(post("/instrucoes"),agenda(b,i,"2030-06-06T10:30:00"))).andExpect(status().isUnprocessableEntity());aula(b,i,"2030-06-06T11:00:00");}
 @Test void sobreposicaoAluno()throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo"),j=pessoa("instrutores","Joao");aula(a,i,"2030-06-06T10:00:00");admin(body(post("/instrucoes"),agenda(a,j,"2030-06-06T09:30:00"))).andExpect(status().isUnprocessableEntity());}
 @Test void sorteioDisponivel()throws Exception{long a=pessoa("alunos","Ana"),b=pessoa("alunos","Bia"),i=pessoa("instrutores","Ivo"),j=pessoa("instrutores","Joao");aula(a,i,"2030-06-06T10:00:00");long id=aula(b,null,"2030-06-06T10:00:00");admin(get("/instrucoes/"+id)).andExpect(jsonPath("$.instrutorId").value(j));}
 @Test void semInstrutorDisponivel()throws Exception{long a=pessoa("alunos","Ana");admin(body(post("/instrucoes"),agenda(a,null,"2030-06-06T10:00:00"))).andExpect(status().isUnprocessableEntity());}
 @Test void instrucoesCrudCancelamentoLiberaAgenda()throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo");long id=aula(a,i,"2030-06-06T10:00:00");admin(get("/instrucoes/"+id)).andExpect(status().isOk());admin(body(put("/instrucoes/"+id),agenda(a,i,"2030-06-06T11:00:00"))).andExpect(status().isOk());admin(delete("/instrucoes/"+id)).andExpect(status().isNoContent());admin(get("/instrucoes/"+id)).andExpect(jsonPath("$.status").value("CANCELADA")).andExpect(jsonPath("$.motivoCancelamento").value("OUTROS"));aula(a,i,"2030-06-06T11:00:00");admin(delete("/instrucoes/"+id)).andExpect(status().isNoContent());}
 @ParameterizedTest @CsvSource({"2030-06-04T08:00:00,200","2030-06-04T07:59:59,422"}) void cancelamento24Horas(String data,int expected)throws Exception{long id=aula(pessoa("alunos","Ana"),pessoa("instrutores","Ivo"),data);admin(body(patch("/instrucoes/"+id+"/cancelar"),Map.of("motivo","ALUNO_DESISTIU"))).andExpect(status().is(expected));}
 @Test void canceladaNaoReagenda()throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo"),id=aula(a,i,"2030-06-06T10:00:00");admin(delete("/instrucoes/"+id)).andExpect(status().isNoContent());admin(body(put("/instrucoes/"+id),agenda(a,i,"2030-06-07T10:00:00"))).andExpect(status().isUnprocessableEntity());}
 @Test void swaggerPublico()throws Exception{mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.components.securitySchemes.bearerAuth").exists()).andExpect(jsonPath("$.paths['/enderecos/{cep}']").exists());mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());}
 @Test void corsPermitido()throws Exception{mvc.perform(options("/alunos").header("Origin","http://localhost:3000").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","authorization,content-type")).andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:3000"));}
 @Test void corsRejeitado()throws Exception{mvc.perform(options("/alunos").header("Origin","https://desconhecido.test").header("Access-Control-Request-Method","GET")).andExpect(status().isForbidden());}

 @Autowired com.fiap.autoescola.service.InstrucaoService instrucaoService;
 @Test void concorrenciaNaoDuplicaAgenda()throws Exception{
  long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo");
  var pool=java.util.concurrent.Executors.newFixedThreadPool(2);var sinal=new java.util.concurrent.CountDownLatch(1);
  java.util.concurrent.Callable<Boolean> tarefa=()->{sinal.await();try{instrucaoService.agendar(new com.fiap.autoescola.dto.instrucao.AgendamentoRequest(a,i,LocalDateTime.of(2030,6,6,10,0)));return true;}catch(com.fiap.autoescola.exception.BusinessException e){return false;}};
  try{var f1=pool.submit(tarefa);var f2=pool.submit(tarefa);sinal.countDown();assertNotEquals(f1.get(10,java.util.concurrent.TimeUnit.SECONDS),f2.get(10,java.util.concurrent.TimeUnit.SECONDS));assertEquals(1,aulas.count());}finally{pool.shutdownNow();}
 }
 @Test void senhaAtualErradaNaoAltera()throws Exception{admin(body(patch("/usuarios/me/senha"),Map.of("senhaAtual","incorreta","novaSenha","nova123"))).andExpect(status().isConflict());login("admin","admin123");}
 @Test void reagendamentoNaoContorna24Horas()throws Exception{long a=pessoa("alunos","Ana"),i=pessoa("instrutores","Ivo"),id=aula(a,i,"2030-06-03T10:00:00");admin(body(put("/instrucoes/"+id),agenda(a,i,"2030-06-06T10:00:00"))).andExpect(status().isUnprocessableEntity());}
 @Test void cancelamentoExigeMotivo()throws Exception{long id=aula(pessoa("alunos","Ana"),pessoa("instrutores","Ivo"),"2030-06-06T10:00:00");admin(body(patch("/instrucoes/"+id+"/cancelar"),Map.of())).andExpect(status().isBadRequest());}
 @Test void enderecoObrigatorio()throws Exception{admin(body(post("/alunos"),Map.of("nome","Ana","email","ana@teste.com","telefone","11","cpf","123"))).andExpect(status().isBadRequest());}
 @Test void cepFormatoRetorna400()throws Exception{admin(get("/enderecos/invalido")).andExpect(status().isBadRequest());}
}
