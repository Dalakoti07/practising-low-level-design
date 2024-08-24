package main

import (
	"database/sql"
	"fmt"
	"sync"
	"testing"

	_ "github.com/lib/pq"
	"github.com/stretchr/testify/assert"
)

const (
	// Database connection string
	connStr = "user=postgres dbname=playground_db sslmode=disable"
)

func setupDB() (*sql.DB, error) {
	db, err := sql.Open("postgres", connStr)
	if err != nil {
		return nil, err
	}
	return db, nil
}

// transferMoney transfers a specified amount from one account to another within a single transaction
func transferMoney(db *sql.DB, fromAccount string, toAccount string, amount float64) error {
	// Begin a transaction
	tx, err := db.Begin()
	if err != nil {
		return err
	}
	defer func() {
		if err != nil {
			tx.Rollback()
		} else {
			err = tx.Commit()
		}
	}()

	// Step 1: Deduct amount from the source account
	_, err = tx.Exec("UPDATE accounts SET balance = balance - $1 WHERE name = $2", amount, fromAccount)
	if err != nil {
		return err
	}

	// Step 2: Add amount to the destination account
	_, err = tx.Exec("UPDATE accounts SET balance = balance + $1 WHERE name = $2", amount, toAccount)
	if err != nil {
		return err
	}

	return nil
}

const TotalAccounts = 100

func TestAccountTransfers(t *testing.T) {
	db, err := setupDB()
	if err != nil {
		t.Fatalf("Failed to connect to the database: %v", err)
	}
	defer db.Close()

	// Delete table if it exists
	_, err = db.Exec(`DROP TABLE IF EXISTS accounts`)
	if err != nil {
		t.Fatalf("Failed to drop table: %v", err)
	}

	// Create the table
	_, err = db.Exec(`
	CREATE TABLE accounts (
		id SERIAL PRIMARY KEY,
		name VARCHAR(255) NOT NULL,
		balance NUMERIC NOT NULL
	)`)
	if err != nil {
		t.Fatalf("Failed to create table: %v", err)
	}

	// Insert SAURABH account
	_, err = db.Exec(`INSERT INTO accounts (name, balance) VALUES ($1, $2)`, "SAURABH", 0)
	if err != nil {
		t.Fatalf("Failed to insert SAURABH account: %v", err)
	}

	// create 100 accounts
	for i := 0; i < TotalAccounts; i++ {
		_, err := db.Exec(`INSERT INTO accounts (name, balance) VALUES ($1, $2)`, "Account"+fmt.Sprintf("-%d", i), 100)
		if err != nil {
			t.Fatalf("Failed to create account %d: %v", i, err)
		}
	}

	// use goroutine
	var wg sync.WaitGroup
	// transfer money from 100 accounts to SAURABH account
	for i := 0; i < TotalAccounts; i++ {
		wg.Add(1)
		go func(i int) {
			defer wg.Done()
			err = transferMoney(db, "Account"+fmt.Sprintf("-%d", i), "SAURABH", 100)
			if err != nil {
				fmt.Printf("%s error in transferring amount from %s to Saurabh\n", err.Error(), "Account"+fmt.Sprintf("-%d", i))
			}
		}(i)
	}
	wg.Wait()
	if err != nil {
		t.Fatalf("Failed to commit transaction: %v", err)
	}
	println("All transfer done")

	// Validate the results
	row := db.QueryRow(`SELECT balance FROM accounts WHERE name = $1`, "SAURABH")
	var balance float64
	err = row.Scan(&balance)
	if err != nil {
		t.Fatalf("Failed to query SAURABH balance: %v", err)
	}

	assert.Equal(t, TotalAccounts*100.0, balance, "Balance of SAURABH should be 1000")
}
