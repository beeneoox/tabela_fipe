package steps;

import static org.junit.Assert.assertTrue;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import pages.ApiFipeActions;

public class ApiFipeSteps extends ApiFipeActions {

	@Given("^que eu sei a marca do carro$")
	public void queEuSeiMarcaDoCarro() throws Throwable {
		String marca = "Chevrolet";
		assertTrue(consultaMarcaVeiculo(marca));
	}

	@And("^sei o modelo$")
	public void seiModeloVeiculo() throws Throwable {
		String modelo = "CRUZE LTZ 1.8 16V FlexPower 4p Aut";
		assertTrue(consultaModeloVeiculo(modelo));
	}

	@And("^sei o ano do veículo$")
	public void seiAnoVeiculo() throws Throwable {
		int ano = 2016;
		assertTrue(consultaAnoVeiculo(ano));
	}

	@When("^eu realizar uma consulta$")
	public void euRealizarUmaConsulta() throws Throwable {
		assertTrue(realizaConsultaTabelaFipe());
	}

	@Then("^é possível obter o seu valor atual de mercado$")
	public void ePossivelObterSeuValorAtualMercado() throws Throwable {
		assertTrue(validaInformacoesTabelaFipe());
	}
}
