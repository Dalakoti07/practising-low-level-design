from flask import Flask
import time

app = Flask(__name__)

@app.route('/data')
def data():
    time.sleep(0.1)  # Simulate a database query or computation
    return 'Data response'

if __name__ == '__main__':
    app.run(port=3000)
