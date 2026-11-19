#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../../.."
sudo docker cp docs/aws/rabbitmq/topology.json iot-industrial-rabbitmq-1:/tmp/topology.json
sudo docker exec iot-industrial-rabbitmq-1 sh -c 'rabbitmqadmin -u "$RABBITMQ_DEFAULT_USER" -p "$RABBITMQ_DEFAULT_PASS" import /tmp/topology.json'
echo "Topologia RabbitMQ importada correctamente"
