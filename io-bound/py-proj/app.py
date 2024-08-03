from flask import Flask, jsonify
import time

app = Flask(__name__)

def calculate_primes(limit):
    is_prime = [True] * (limit + 1)
    p = 2
    while (p * p <= limit):
        if (is_prime[p] == True):
            for i in range(p * p, limit + 1, p):
                is_prime[i] = False
        p += 1
    return [p for p in range(2, limit) if is_prime[p]]

@app.route('/primes')
def get_primes():
    start_time = time.time()
    primes = calculate_primes(1000000)
    duration = time.time() - start_time
    return jsonify({
        "numbers": primes,
        "from": "python"
    })

if __name__ == '__main__':
    app.run(port=3001)
