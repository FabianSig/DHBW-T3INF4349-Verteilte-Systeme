FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY model ./model

ENV HUGGINGFACE_HUB_CACHE=/app/model

COPY /app/script.py .

EXPOSE 5000

CMD ["python", "script.py"]
