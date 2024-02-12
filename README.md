# Rinha Backend 2ª Edição

Este projeto foi desenvolvido para participar da segunda edição da rinha backend ![aqui](https://github.com/zanfranceschi/rinha-de-backend-2024-q1)!

## Requisitos
1. Docker
2. Java 21+

## Tecnologias utilizadas

Foi utilizado ![Quarkus](https://quarkus.io/) como framework base do projeto com as seguintes extençoes

1. agroal
2. cdi
3. flyway
4. jdbc-postgresql
5. narayana-jta
6. resteasy
7. resteasy-jsonb
8. smallrye-context-propagation
9. smallrye-health, vertx].

Compilamos para nativo utilizando ![GraalVM](https://www.graalvm.org/) utilizando plugin fornecido pelo proprio framework.

## Rodando a Aplicação em modo DEV

O quarkus usa ![dev service](https://quarkus.io/guides/dev-services) em desenvolvimento com isso não é preciso ter o postgres instalado para rodar em desenvolvimento, o que não é verdade em produção. Para rodar e modo dev basta executar o comando abaixo.

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus tem uma interface de desenvolvimento muito boa q pode ser acessada atraves do link http://localhost:8080/q/dev/.

## Empacotando a aplicação

```shell script
./mvnw package
```
Este comando vai criar um arquivo  `quarkus-run.jar` no diretorio `target/quarkus-app/`.
Se atente q este não é um _über-jar_ (com as dependencias embutidas) e as dependencias se encontram no diretorio `target/quarkus-app/lib/`.

Com isto pode rodar a aplicação utilizando o comando `java -jar target/quarkus-app/quarkus-run.jar`.

Caso queira um _über-jar_:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

O _über-jar_ estara no diretorio target com sufixo `-runner.jar` para rodar a aplicação execute `java -jar target/*-runner.jar`.

## Criando um executavel nativo

Utilizamos este modo de build para gerar a imagem docker utilizada na rinha. 

Execute o comando abaixo se tiver a GraalVM devidamente configurada em seu sistema.
```shell script
./mvnw package -Dnative
```

Se não tiver a GraalVM utilize o comando abaixo ele ira utilizar um container docker para compilar (Lembre que docker instalado é um requisito)
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

o executavel nativo estara na pasta target você pode executalo com o seguinte comando `./target/rinha_e2-1.0.0-SNAPSHOT-runner`

## Rodando os testes de Integração

Temos teste de integração para validar os requisitos da rinha, a maioria dos comandos acima já executam os testes antes de buildar caso não queira executar os testes nos comandos acima use a flag `-DskipTests`

```shell script
./mvnw verify
```

## Rodando os testes de carga da rinha

No diretorio `load-test/user-files/simulations/rinhabackend/RinhaBackendCrebitosSimulation.scala`  esta o arquivo com o script de teste usado no desenvolvimento do projeto, este arquivo é fornecido pela rinha e atualizações nele não são sincronizadas.
Na raiz temos o arquivo `executar-teste-local.sh` antes de executalo para inicar os teste de carga é necessario atualizar ele com o caminho para a pasta bin do ![Gatling](https://gatling.io/docs/gatling/tutorials/installation/) com tudo configurado basta executar `./executar-teste-local.sh`.
