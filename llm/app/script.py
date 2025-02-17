from flask import Flask, request, jsonify
from transformers import pipeline

app = Flask(__name__)


# Load text classification models at startup
# German toxic comment classifier model
german_classifier = pipeline('text-classification', model='ml6team/distilbert-base-german-cased-toxic-comments')
# Multilingual toxic comment classifier model
multilingual_classifier = pipeline('text-classification', model='unitary/multilingual-toxic-xlm-roberta')

@app.route('/validate', methods=['POST'])
def validate_message():
    app.logger.info("Validating message: %s", request.json)
    data = request.json  # Get JSON data from the request body
    if not data or 'message' not in data:
        return jsonify({"success": False, "error": "Message is required"}), 400

    message = data['message']
    german_result = german_classifier(message)
    multilingual_result = multilingual_classifier(message)

    app.logger.info("Validation result: german: %s \nmultilingual: %s", german_result, multilingual_result)

    # Choose the best result based on the multilingual classifier's confidence score
    best_result = multilingual_result if multilingual_result[0]['score'] > 0.9 else german_result

    app.logger.info("Validation returned best result: %s", best_result)

    return jsonify({"success": best_result})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
