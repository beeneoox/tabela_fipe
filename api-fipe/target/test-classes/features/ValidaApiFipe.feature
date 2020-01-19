Feature: Desafio_Automação_META 
	Como usuário eu gostaria de obter o valor de tabela FIPE
	do meu veículo marca GM - Chevrolet, modelo CRUZE LTZ 1.8
	16V FlexPower4pAut, ano 2016.
  
Scenario: 01_Obter_informações_tabela_FIPE 
	Given que eu sei a marca do carro 
	And sei o modelo 
	And sei o ano do veículo 
	When eu realizar uma consulta 
	Then é possível obter o seu valor atual de mercado