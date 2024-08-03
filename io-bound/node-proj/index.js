const { Client } = require('pg');
const express = require('express');
const app = express();
const port = 3001;

// Database connection configuration
const client = new Client({
    user: 'postgres',       // Replace with your database username
    host: 'localhost',           // Replace with your database host (e.g., localhost or an IP address)
    database: 'playground_db',   // Replace with your database name
    password: 'postgres',   // Replace with your database password
    port: 5432,                  // Default PostgreSQL port
});

// Connect to the database
client.connect()
  .then(() => {
    console.log('Connected to the database');
  })
  .catch((err) => {
    console.error('Error connecting to the database', err.stack);
  });

  app.get('/data', async (req, res) => {
    try {
      const result = await client.query('SELECT * FROM books');  // Replace 'your_table_name' with your actual table name
      res.json({
        "from":"node",
        "data":result.rows
      });
    } catch (err) {
      console.error('Error executing query', err.stack);
      res.status(500).send('Internal Server Error');
    }
  });

app.listen(port, () => {
  console.log(`Node.js server running on http://localhost:${port}`);
});
