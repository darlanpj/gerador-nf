package br.com.itau.calculadoratributos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = br.com.itau.geradornotafiscal.GeradorNotaFiscalApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CalculadoratributosApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	private static final String URL_TEMPLATE = "/api/pedidos/gerarNotaFiscal";


	@Test
	@DisplayName("Test NF gerada para PF")
	void validaNotaFiscalGeradaParaPF() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(URL_TEMPLATE)
						.contentType(MediaType.APPLICATION_JSON)
						.content(bytesFromPath(new ClassPathResource("payloads/test-pf.json").getPath())))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isCreated());

	}

	private byte[] bytesFromPath(final String path) throws IOException {
		System.out.println("path: " + path);
		return Files.readAllBytes(Paths.get(path));
	}
}
