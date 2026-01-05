import time
import logging
from flask import Flask, request, jsonify

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)

@app.route('/process', methods=['POST'])
def process_event():
    """
    Simulates complex processing.
    Expects JSON: {"payload": "string"}
    Returns JSON: {"score": int, "tags": [str], "processed_by": str}
    """
    data = request.json
    payload = data.get('payload', '')
    
    # Simulate processing delay
    time.sleep(0.1) 
    
    # Logic: Calculate score based on length and fake tagging
    score = len(payload) * 10
    tags = ["processed", "v1"]
    if "error" in payload.lower():
        tags.append("alert")
        
    logging.info(f"Processed payload: {payload[:20]}...")
    
    return jsonify({
        "complexity_score": score,
        "tags": tags,
        "processed_by": "python-worker-node-1"
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)