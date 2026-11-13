package com.polizas;

import com.polizas.dto.CoreEvento;
import com.polizas.integration.CoreGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PolizasApplicationTest {

	private static final String KEY = "x-api-key";
	private static final String VALID_KEY = "123456";

	@Autowired
	MockMvc mvc;

	@MockitoBean
	CoreGateway coreGateway;

	@Test
	void sinApiKeyRetorna401() throws Exception {
		mvc.perform(get("/polizas"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void listaPorTipoYEstado() throws Exception {
		mvc.perform(get("/polizas?tipo=COLECTIVA&estado=VIGENTE").header(KEY, VALID_KEY))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].tipo").value("COLECTIVA"))
				.andExpect(jsonPath("$[0].estado").value("VIGENTE"));
	}

	@Test
	void renovarIncrementaCanonYNotificaAlCore() throws Exception {
		mvc.perform(post("/polizas/1/renovar").header(KEY, VALID_KEY))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("RENOVADA"))
				.andExpect(jsonPath("$.canonMensual").value(1051500.0));

		verify(coreGateway).enviarEvento(any(CoreEvento.class));
	}

	@Test
	void noSeRenuevaPolizaCancelada() throws Exception {
		mvc.perform(post("/polizas/3/renovar").header(KEY, VALID_KEY))
				.andExpect(status().isConflict());
	}

	@Test
	void noSeAgreganRiesgosAPolizaIndividual() throws Exception {
		mvc.perform(post("/polizas/1/riesgos").header(KEY, VALID_KEY)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"direccionInmueble\":\"Calle 1\",\"arrendatario\":\"X\"}"))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void agregaRiesgoAPolizaColectiva() throws Exception {
		mvc.perform(post("/polizas/2/riesgos").header(KEY, VALID_KEY)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"direccionInmueble\":\"Calle 50 #10-20\",\"arrendatario\":\"Pedro Díaz\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.estado").value("ACTIVO"));
	}

	@Test
	void cancelarPolizaCancelaSusRiesgos() throws Exception {
		mvc.perform(post("/polizas/4/cancelar").header(KEY, VALID_KEY))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("CANCELADA"));

		mvc.perform(get("/polizas/4/riesgos").header(KEY, VALID_KEY))
				.andExpect(jsonPath("$[0].estado").value("CANCELADO"))
				.andExpect(jsonPath("$[1].estado").value("CANCELADO"));
	}
}