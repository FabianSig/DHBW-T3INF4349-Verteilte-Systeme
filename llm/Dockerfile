FROM nikomitk/python-transformers-toxic:slim-cpu

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY /app/script.py .

EXPOSE 5000

CMD ["python", "script.py"]
