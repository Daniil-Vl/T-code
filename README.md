# T-Code

## Описание проекта

T-Code - приложение для решения алгоритмических задач в рамках контестов (соревнований).
Оно позволяет создавать задачи и контесты, а также принимать участие в контестах других пользователей.

## Технологии

### Основные

1. Java 21
2. Spring boot 3
3. Postgres 16
4. Liquibase 4.25
5. RabbitMQ 4.0

### Интеграции

1. [Yandex Cloud Object Storage (S3)](https://yandex.cloud/ru/services/storage)
2. [Judge0](https://github.com/judge0/judge0)

### Метрики

1. Prometheus
2. Grafana

### Логи

1. ElasticSearch
2. Logstash
3. Kibana

### Деплой

1. Docker

### CI\CD

1. GitHub Actions

## Архитектура проекта

![Архитектура проекта](assets/project_schema.png)

## Метрики

В проект добавление Prometheus для сбора метрик и Grafana для их визуализации.

Добавлен дашборд, на котором можно мониторить:

1. Использование процессора
2. Использование памяти процессом
3. Количество созданных и потребленных сообщений через RabbitMQ
4. Доля запросов с http кодами 4xx и 5xx.
5. Среднее время обработки запроса

Конфигурация дашборда (json model) хранится в директории grafana.
С помощью данной конфигурации
можно [импортировать](https://grafana.com/docs/grafana/latest/dashboards/build-dashboards/import-dashboards/) данный
дашборд.
В качестве адреса prometheus data source нужно будет указать `http://prometheus:9090`

#### Дашборд

![Grafana dashboard first](assets/grafana-dashboard-1.png)
![Grafana dashboard second](assets/grafana-dashboard-2.png)

## Логи

Для сбора логов добавлена конфигурация для поднятия elk стэка в docker compose.
Каждый сервис пишет логи в отдельный index (contest-service или submission-service),
что позволяет просматривать логи каждого сервиса по отдельности.

#### Kibana dashboard

![kibana dashboard](assets/kibana.png)

## Тестирование

Код покрыт юнит тестами. Помимо этого добавлены интеграционные тесты с использованием TestContainers (для поднятия
postgresql, rabbitmq и s3 object storage).

### Покрытие тестами:

#### Contest service

![jacoco-reports](assets/contest-service-jacoco-report.png)

#### Submission service

![img.png](assets/submission-service-jacoco-report.png)

## Запуск проекта

Указать здесь файл env и перечислить, какие переменные окружения нужно внести
