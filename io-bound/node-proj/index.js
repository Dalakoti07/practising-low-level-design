const express = require('express');
const app = express();

function calculatePrimes(limit) {
    const isPrime = Array(limit + 1).fill(true);
    isPrime[0] = isPrime[1] = false;
    for (let p = 2; p * p <= limit; p++) {
        if (isPrime[p]) {
            for (let i = p * p; i <= limit; i += p) {
                isPrime[i] = false;
            }
        }
    }
    return Array.from({length: limit + 1}, (v, k) => k).filter(p => isPrime[p]);
}

app.get('/primes', (req, res) => {
    const startTime = Date.now(); // Capture start time
    const primes = calculatePrimes(1000000);
    const endTime = Date.now();   // Capture end time
    const duration = endTime - startTime; // Calculate duration in milliseconds
    console.log("duration ", duration);
    res.json({
        numbers: primes,
        from: "node"
    });
});

app.listen(3000, () => {
    console.log('Server listening on port 3000');
});
