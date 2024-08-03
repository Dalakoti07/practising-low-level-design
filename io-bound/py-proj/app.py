# app.py

from flask import Flask, jsonify
import psycopg2
from psycopg2 import sql

app = Flask(__name__)

# Database connection configuration
def get_db_connection():
    conn = psycopg2.connect(
        dbname='playground_db',    # Replace with your database name
        user='postgres',      # Replace with your database username
        password='postgres',  # Replace with your database password
        host='localhost',          # Replace with your database host (e.g., localhost or an IP address)
        port='5432'                # Default PostgreSQL port
    )
    return conn

@app.route('/data', methods=['GET'])
def get_data():
    conn = get_db_connection()
    cur = conn.cursor()
    cur.execute('SELECT * FROM books')  # Replace 'your_table_name' with your actual table name
    rows = cur.fetchall()
    cur.close()
    conn.close()

    # Convert rows to a list of dictionaries
    columns = [desc[0] for desc in cur.description]
    data = [dict(zip(columns, row)) for row in rows]
    
    return jsonify({"from":"python", "data": data})

if __name__ == '__main__':
    app.run(port=3000)
