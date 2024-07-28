package main

import (
	"bytes"
	"database/sql"
	"fmt"
	"log"
	"math/rand"
	"time"

	_ "github.com/lib/pq"
)

const (
	// Database connection string
	connStr = "user=postgres dbname=playground_db sslmode=disable"
)

const (
	// Size of the text file in bytes
	fileSize = 1 * 1024 * 1024 // 3 MB
)

// Function to generate random text of specified size
func generateRandomText(size int) string {
	var buffer bytes.Buffer
	chars := "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
	for buffer.Len() < size {
		buffer.WriteString(chars)
	}
	return buffer.String()[:size]
}

// Function to create 1000 blog articles in SQL rows
func createArticlesInRows(db *sql.DB) {
	// Generate 3 MB of random text
	text := generateRandomText(fileSize)

	for i := 1; i <= 1000; i++ {
		content := "This is the content of the article."
		if i%5 == 0 {
			content = text
		}
		_, err := db.Exec(
			fmt.Sprintf("INSERT INTO %s (title, content) VALUES ($1, $2)", tableName),
			fmt.Sprintf("Article %d", i),
			content,
		)
		if err != nil {
			log.Fatal(err)
		}
	}
}

// Function to create 1000 blog articles with file paths
func createArticlesWithFiles(db *sql.DB) {
	for i := 1; i <= 1000; i++ {
		filePath := fmt.Sprintf("/path/to/articles/article_%d.txt", i)
		_, err := db.Exec(
			fmt.Sprintf("INSERT INTO %s (title, file_path) VALUES ($1, $2)", tableName),
			fmt.Sprintf("Article %d", i), filePath,
		)
		if err != nil {
			log.Fatal(err)
		}
	}
}

// Function to benchmark querying all articles
func benchmarkQuery(db *sql.DB, useFiles bool) time.Duration {
	start := time.Now()

	var rows *sql.Rows
	var err error

	if useFiles {
		rows, err = db.Query(fmt.Sprintf("SELECT title,file_path FROM %s", tableName))
	} else {
		rows, err = db.Query(fmt.Sprintf("SELECT title,content FROM %s", tableName))
	}
	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()

	for rows.Next() {
		var title string
		var content string
		if useFiles {
			err = rows.Scan(&title, &content)
		} else {
			err = rows.Scan(&title, &content)
		}
		if err != nil {
			log.Fatal(err)
		}
	}

	return time.Since(start)
}

var tableName = "blog_inplace_content"

func main() {
	rand.Seed(time.Now().UnixNano())

	db, err := sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	_, err = db.Exec(fmt.Sprintf("DROP TABLE IF EXISTS %s", tableName))
	if err != nil {
		log.Fatal(err)
	}

	approachOne(db)
	// Drop and recreate table for file paths approach
	_, err = db.Exec(fmt.Sprintf("DROP TABLE IF EXISTS %s", tableName))
	if err != nil {
		log.Fatal(err)
	}
	approachTwo(db)
}

func approachTwo(db *sql.DB) {
	_, err := db.Exec(
		fmt.Sprintf(
			"CREATE TABLE %s (id SERIAL PRIMARY KEY, title VARCHAR(255) NOT NULL, file_path VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
			tableName,
		),
	)
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println("Inserting articles with file paths...")
	createArticlesWithFiles(db)
	durationFiles := benchmarkQuery(db, true)
	fmt.Printf("Querying with file paths took: %v\n", durationFiles)
}

func approachOne(db *sql.DB) {
	_, err := db.Exec(fmt.Sprintf("create table %s (", tableName) +
		"id Serial PRIMARY KEY," +
		"title VARCHAR(255) NOT NULL," +
		"content TEXT NOT NULL," +
		"created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);",
	)
	if err != nil {
		log.Fatal(err)
	}

	// Insert articles and benchmark
	fmt.Println("Inserting articles with content in rows...")
	createArticlesInRows(db)
	durationRows := benchmarkQuery(db, false)
	fmt.Printf("Querying with content in rows took: %v\n", durationRows)
}
