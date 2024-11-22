#!/bin/bash

# Wait for Ollama to start
while ! nc -z localhost 11434; do
  sleep 1
done

echo "Ollama is ready, pulling the model..."
ollama pull smollm:135m
