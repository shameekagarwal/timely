#!/bin/bash

cd frontend-web
docker build -f Dockerfile.qa . -t shameek/timely-frontend-web
docker push shameek/timely-frontend-web
cd ..

cd project-service
./mvnw spring-boot:build-image
docker push shameek/timely-project-service
cd ..

cd task-service
./mvnw spring-boot:build-image
docker push shameek/timely-task-service
cd ..

cd user-service
./mvnw spring-boot:build-image
docker push shameek/timely-user-service
cd ..

cd service-registry
./mvnw spring-boot:build-image
docker push shameek/timely-service-registry
cd ..

cd service-gateway
./mvnw spring-boot:build-image
docker push shameek/timely-service-gateway
cd ..
