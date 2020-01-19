package pages;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import model.Veiculo;

public class ApiFipeActions {

	int codigoMarca, codigoModelo;
	String codigoAno, valor;
	Veiculo veiculo;

	final String URL_DEFAULT = "https://parallelum.com.br/fipe/api/v1/carros/marcas";
	HttpResponse<String> response;

	// Requisição para pegar o ID da Chevrolet
	public boolean consultaMarcaVeiculo(String marca) throws InterruptedException {

		try {
			// Request
			Unirest.setTimeouts(0, 0);
			this.response = Unirest.get(URL_DEFAULT).asString();

			// Valido status requisição
			if (response.getStatus() == 200) {

				// Appendei o início pra ficar mais fácil transformar em array (não é o correto)
				StringBuilder apiResponse = new StringBuilder("{ \"marcas\" :").append(response.getBody().toString())
						.append("}");

				// Converto response em Json
				JSONObject jObject = new JSONObject(apiResponse.toString());
				// Converto Json em Array
				JSONArray jArrayMarcas = jObject.getJSONArray("marcas");

				// Varro array pra buscar ID da Chevrolet e faço as atribuições
				for (int i = 0; i < jArrayMarcas.length(); i++) {
					JSONObject jMarca = jArrayMarcas.getJSONObject(i);

					if (jMarca.getString("nome").toUpperCase().contains(marca.toUpperCase())) {
						this.codigoMarca = jMarca.getInt("codigo");
						this.veiculo = new Veiculo();
						this.veiculo.setMarca(jMarca.getString("nome"));
						return true;
					}
				}
			} else {
				throw new Exception("--> Erro na requisição consultaMarcaVeiculo()");
			}

		} catch (Exception ex) {
			System.out.println("##### Erro ao consultar marca do veículo! ####\n\n" + ex.getMessage());
			return false;
		}
		return false;
	}

	// Requisição para pegar o ID do Cruze
	public boolean consultaModeloVeiculo(String modelo) throws InterruptedException {

		// Monto URL com os parâmetros necessários
		StringBuilder URL_MODELOS = new StringBuilder(URL_DEFAULT).append("/").append(this.codigoMarca)
				.append("/modelos");

		try {
			// Request
			Unirest.setTimeouts(0, 0);
			this.response = Unirest.get(URL_MODELOS.toString()).asString();

			if (response.getStatus() == 200) {
				JSONObject jObject = new JSONObject(response.getBody().toString());
				JSONArray jArrayModelos = jObject.getJSONArray("modelos");

				for (int i = 0; i < jArrayModelos.length(); i++) {

					JSONObject jModelo = jArrayModelos.getJSONObject(i);
					if (jModelo.getString("nome").toUpperCase().contains(modelo.toUpperCase())) {
						this.codigoModelo = jModelo.getInt("codigo");
						this.veiculo.setModelo(jModelo.getString("nome"));
						return true;
					}
				}
			} else {
				throw new Exception("--> Erro na requisição consultaModeloVeiculo()");
			}

		} catch (Exception ex) {
			System.out.println("##### Erro ao consultar modelo do veículo! ####\n\n" + ex.getMessage());
			return false;
		}

		return false;
	}

	// Requisição para pegar o ID do Ano
	public boolean consultaAnoVeiculo(int ano) throws InterruptedException {

		// Monto URL com parâmetros necessários
		StringBuilder URL_ANOS = new StringBuilder(URL_DEFAULT).append("/").append(this.codigoMarca).append("/modelos/")
				.append(this.codigoModelo).append("/anos");

		try {
			// Request
			Unirest.setTimeouts(0, 0);
			this.response = Unirest.get(URL_ANOS.toString()).asString();

			// Valido status requisiçao
			if (response.getStatus() == 200) {
				StringBuilder apiResponse = new StringBuilder("{ \"anos\" :").append(response.getBody().toString())
						.append("}");

				// Transforo request em Json / JsonArray respectivamente
				JSONObject jObject = new JSONObject(apiResponse.toString());
				JSONArray jArrayAnos = jObject.getJSONArray("anos");

				// Varro array para buscar o ID do Ano
				for (int i = 0; i < jArrayAnos.length(); i++) {
					JSONObject jAno = jArrayAnos.getJSONObject(i);
					if (jAno.getString("nome").contains(String.valueOf(ano))) {
						this.codigoAno = jAno.getString("codigo");
						this.veiculo.setAno(Integer.valueOf(this.codigoAno.substring(0, 4)));
						return true;
					}
				}
			} else {
				throw new Exception("--> Erro na requisição consultaAnoVeiculo()");
			}

		} catch (Exception ex) {
			System.out.println("##### Erro ao consultar ano do veículo! ####\n\n" + ex.getMessage());
			return false;
		}

		return false;
	}

	// Consulto tabela FIPE para obter as informações finais
	public boolean realizaConsultaTabelaFipe() throws InterruptedException {
		// Monto a URL com os parâmetros
		StringBuilder URL_CONSULTA = new StringBuilder(URL_DEFAULT).append("/").append(this.codigoMarca)
				.append("/modelos/").append(this.codigoModelo).append("/anos/").append(this.codigoAno);

		try {
			// Request
			Unirest.setTimeouts(0, 0);
			this.response = Unirest.get(URL_CONSULTA.toString()).asString();

			// Valido response e pego os valores necessários no Json
			if (response.getStatus() == 200) {
				JSONObject jsonObj = new JSONObject(response.getBody().toString());
				this.valor = jsonObj.getString("Valor");
				this.veiculo.setValor(Double.valueOf(jsonObj.getString("Valor").substring(2, valor.length() - 3)));
				this.veiculo.setCodigoFipe(jsonObj.getString("CodigoFipe"));
				return true;
			} else {
				throw new Exception("--> Erro na requisição consultaTabelaFipe()");
			}

		} catch (Exception ex) {
			System.out.println("##### Erro ao consultar Tabela FIPE! ####\n\n" + ex.getMessage());
		}
		return this.response.getStatus() == 200;
	}

	// Apenas valida o que consta no objeto Veiculo (informações obtidas nas
	// requisições) e compara com o esperado (String com os valores informados
	// previamente).
	public boolean validaInformacoesTabelaFipe() throws Exception {
		final String CODIGO_FIPE = "004381-8", MARCA = "GM - Chevrolet", MODELO = "CRUZE LTZ 1.8 16V FlexPower 4p Aut.",
				ANO = "2016", VALOR = "R$ 60.468,00";

		// String com as informações esperadas, apenas para comparar com o atual
		StringBuilder expected = new StringBuilder("Código FIPE: ").append(CODIGO_FIPE).append("\nMarca: ")
				.append(MARCA).append("\nModelo: ").append(MODELO).append("\nAno: ").append(ANO).append("\nValor: ")
				.append(VALOR);
		if (this.veiculo != null) {
			// Crio String com informações dos responses para comparar com o esperado
			StringBuilder actual = new StringBuilder("Código FIPE: ").append(this.veiculo.getCodigoFipe())
					.append("\nMarca: ").append(this.veiculo.getMarca()).append("\nModelo: ")
					.append(this.veiculo.getModelo()).append("\nAno: ").append(this.veiculo.getAno())
					.append("\nValor: R$ ").append(this.veiculo.getValor()).append(",00");

			// Apenas exibe as informações no console.
			System.out.println("\n\n###### RESULTADO ESPERADO #####");
			System.out.println(expected.toString());

			System.out.println("\n\n####### RESULTADO ATUAL #######");
			System.out.println(actual.toString());
			return expected.toString().equals(actual.toString());
		} else {
			throw new Exception("Não foi possível validar os valores. Objeto Veiculo == NULL.");
		}

	}
}
