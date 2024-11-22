#!/bin/bash

# Start Ollama in the background
apt-get update && apt-get install -y netcat

/app/run/run_model.sh &

echo "Starting Ollama server..."
ollama serve