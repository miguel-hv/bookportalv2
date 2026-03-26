# Monitoring Implementation Roadmap

## Current State
- Spring Boot 3.3.4 with Logstash JSON encoder
- JSON logs in `logs/bookportal.json` with rotation

---

## Architecture

```
[App] → JSON Logs → [Filebeat] → [Elasticsearch] ← [Kibana]
```

---

## Step 1: Add Dependencies to Docker Compose

```yaml
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
    ports:
      - "9200:9200"
    volumes:
      - es-data:/usr/share/elasticsearch/data
    networks:
      - app-network

  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch
    networks:
      - app-network
```

---

## Step 2: Configure Filebeat

Create `filebeat/filebeat.yml`:

```yaml
filebeat.inputs:
  - type: log
    paths:
      - /usr/share/logs/*.json
    json.keys_under_root: true
    json.message_key: message

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
  index: "bookportal-logs-%{+YYYY.MM.dd}"
```

Add filebeat service to docker-compose:

```yaml
  filebeat:
    image: docker.elastic.co/beats/filebeat:8.11.0
    volumes:
      - ./filebeat/filebeat.yml:/usr/share/filebeat/filebeat.yml
      - ./backend/logs:/usr/share/logs
    depends_on:
      - elasticsearch
    networks:
      - app-network
```

---

## Step 3: Verify

- Start: `docker-compose up -d`
- Kibana: http://localhost:5601
- Create index pattern: `bookportal-logs-*`

---

## Notes

- Minimal setup: Filebeat sends directly to ES (no Logstash)
- ~512MB RAM for ES is enough for learning
- Alternative: Loki + Grafana (lighter)