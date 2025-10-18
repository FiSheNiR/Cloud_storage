# Цель проекта

## Реализация проекта из [Java Роадмап Сергея Жукова](https://zhukovsd.github.io/java-backend-learning-course/)

## Техническое задание: [Облачное хранилище файлов](https://zhukovsd.github.io/java-backend-learning-course/projects/cloud-file-storage/)

# Инструкция по запуску

1. Установите Docker и запустите его.
2. В папке репозитория выполните:

```bash
./mvnw clean package -DskipTests
docker build -t cloud-storage-app:latest .
```

3. Перейти в папку docker-app и выполнить:

```bash
docker compose -f docker-compose-prod.yaml up -d
```
