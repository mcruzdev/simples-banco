# Exercício: Evitando Race Conditions em Transferências Bancárias

Você foi contratado para desenvolver o módulo de **transferências bancárias** de um sistema financeiro. O sistema deve permitir que um cliente transfira valores entre contas bancárias cadastradas no banco de dados.  

Atualmente, existe um problema grave: quando várias transferências ocorrem ao mesmo tempo (concorrência), é possível que a conta de origem fique com **saldo negativo**, mesmo existindo uma regra de negócio que impede isso. Isso acontece porque duas transações podem **ler o mesmo saldo antigo** antes de debitar, resultando em inconsistências (**race condition** / **lost update**).

## **Seu desafio**

1. Implementar a operação de transferência corrigindo esse problema de concorrência.  
2. Resolver o problema de **pelo menos 3 formas diferentes**.  
3. Garantir que **nenhuma conta possa ficar com saldo negativo**, independentemente da carga de concorrência.
4. Você tem que fazer todos os testes da aplicação passarem com sucesso.

## **Requisitos funcionais**

- A operação deve debitar o valor da conta de origem e creditar na conta de destino.  
- A transação deve ser **atômica**: se ocorrer qualquer falha (saldo insuficiente, conta inexistente, falha técnica), nenhuma alteração deve ser gravada.  
- O saldo final de cada conta deve estar correto mesmo quando várias transferências ocorrem ao mesmo tempo.  

## **Sugestões (para inspirar suas soluções)**

- Usar **lock pessimista** (`PESSIMISTIC_WRITE`) em JPA.  
- Usar **lock otimista** com `@Version` e retry em caso de conflito.  
- Usar um **UPDATE condicional** (CAS - compare and swap) com `WHERE saldo >= valor`.  
- Usar **constraint no banco** (`CHECK (saldo >= 0)`) em conjunto com tratamento da exceção.  
- Criar uma abordagem arquitetural (fila de mensagens, serialização de comandos por conta).

## **Não é necessário entregar, isso aqui é pra vocês praticarem**

- Implemente **três soluções diferentes**, documentando em comentários a abordagem adotada em cada uma.  
- Explique (em texto ou README) **vantagens e desvantagens** de cada solução.  
