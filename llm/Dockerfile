FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /app

# Copy the requirements file if you have one (recommended for dependency management)
COPY requirements.txt .

# Install the dependencies
RUN pip install --no-cache-dir -r requirements.txt

COPY model ./model

ENV HUGGINGFACE_HUB_CACHE=/app/model

# Copy your Python script into the container
COPY /app/script.py .

EXPOSE 5000

# Specify the command to run the script
CMD ["python", "script.py"]
