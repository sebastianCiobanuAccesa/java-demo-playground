# java-demo-playground
a java spring demo playground for Java vs Python debate session

generated using https://start.spring.io/

Features:
Caching: developed and framework 
Postgresql (Spring data + Jpa2 storage)
transaction and exchange processing service
data generation
configuration

# scripts

docker pull postgres:9.6.24-bullseye

docker run --publish 127.0.0.1:5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres -d postgres
