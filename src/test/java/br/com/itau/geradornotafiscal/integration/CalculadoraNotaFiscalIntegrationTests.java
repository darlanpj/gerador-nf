package br.com.itau.geradornotafiscal.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CalculadoraNotaFiscalIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String URL_TEMPLATE = "/api/pedido/gerarNotaFiscal";

    @Test
    public void contextLoads() {
    }

    @Test
    @DisplayName("Test NF gerada para PF")
    void validaNotaFiscalGeradaParaPF() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bytesFromPath(
                                new ClassPathResource("src/test/resources/payloads/teste-pf.json").getPath())))
                .andDo(print())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.valor_frete").value("10.48"));
    }

    @Test
    @DisplayName("Test NF gerada para PJ regime de tributação simples")
    void validaNotaFiscalGeradaParaPJTributacaoSimples() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bytesFromPath(
                                new ClassPathResource("src/test/resources/payloads/teste-pj-simples.json").getPath())))
                .andDo(print())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.valor_frete").value("75.456"))
                .andExpect(jsonPath("$.itens[0].valor_tributo_item").value("138.7"));
    }

    @Test
    @DisplayName("Test NF gerada para PJ regime de tributação lucro real")
    void validaNotaFiscalGeradaParaPJTributacaoLucroReal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bytesFromPath(
                                new ClassPathResource("src/test/resources/payloads/teste-pj-lucro-real.json").getPath())))
                .andDo(print())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Test NF gerada para PJ regime de tributação lucro presumido")
    void validaNotaFiscalGeradaParaPJTributacaoLucroPresumido() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bytesFromPath(
                                new ClassPathResource("src/test/resources/payloads/teste-pj-presumido.json").getPath())))
                .andDo(print())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    private byte[] bytesFromPath(final String path) throws IOException {
        System.out.println("path: " + path);
        return Files.readAllBytes(Paths.get(path));
    }
}
