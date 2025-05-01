# Kafka Самостоятельная работа №5
## Задание 2. Интеграция Kafka с внешними системами (Apache NiFi/Hadoop)

### 1) Создание и развертывание кластера Kafka в облаке.
### 1.1) Используется кластер созданный для задания 1
### 1.2) В кластер добавлен топик `nifi-topic`
![total](screenshots/001.png)
___
### 2) Запуск и настройка NiFi.
### 2.1) Для локального запуска NiFi в дирректории `infra` выполнить команду:
```
docker compose up -d
```
### 2.2) С помощью NiFi UI был создан и настроен Processor `GetFile`:
![total](screenshots/002.png)
___
![total](screenshots/003.png)
___
### 2.3) Был создан и настроен Processor `PublishKafkaRecord_2_0`:
![total](screenshots/004.png)
___
![total](screenshots/005.png)
___
### 2.3) В Processor `PublishKafkaRecord_2_0` был добавлен и настроен `StandardSSLContextService 1.21.0`:
![total](screenshots/006.png)
___
![total](screenshots/007.png)
___
### 3) Проверка работы и интеграция  NiFi с кластером Kafka в облаке:

