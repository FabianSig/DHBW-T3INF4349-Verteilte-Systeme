from flask import Flask, request, jsonify
from transformers import pipeline
app = Flask(__name__)

classifier = pipeline('text-classification', model='ml6team/distilbert-base-german-cased-toxic-comments', )

@app.route('/validate', methods=['POST'])
def validate_message():
    """
    Accepts a message and returns true or false.
    """
    data = request.json  # Get JSON data from the request body
    if not data or 'message' not in data:
        return jsonify({"success": False, "error": "Message is required"}), 400

    message = data['message']


    return jsonify({"success": classifier(message)})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
