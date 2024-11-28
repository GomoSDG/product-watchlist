(ns product-watchlist.infrastructure.db
  "Database infrastructure layer.
   Manages all database interactions and connection handling."
  (:require [next.jdbc :as jdbc]))

(defn try-parse-int
  "Safely parse string to integer, returns default if parsing fails"
  [s default]
  (try
    (Integer/parseInt s)
    (catch NumberFormatException _
      default)))

(def db-spec
  {:dbtype "postgresql"
   :dbname "product_watchlist_test" #_(System/getenv "DB_NAME")
   :host "localhost" #_(System/getenv "DB_HOST")
   :port (try-parse-int (System/getenv "DB_PORT") 5433)
   :user "test_user" #_(System/getenv "DB_USER")
   :password "test_pass" #_(System/getenv "DB_PASSWORD")})

(def ds (jdbc/get-datasource db-spec))

(defn with-transaction
  "This function `with-transaction` takes a function `f` as an argument and executes it within a database transaction.
   It obtains a connection from the datasource `ds`, and ensures that the connection is closed after the transaction is complete.
   The transaction is managed using `jdbc/with-transaction`, which ensures that the transaction is committed if `f` completes successfully,
   or rolled back if an exception is thrown.

   Parameters:
   - `f`: A function that takes a transaction object `tx` and performs database operations within the transaction.

   Usage:
   (with-transaction (fn [tx] 
                       ;; perform database operations using tx
                       ))"
  [f]
  (with-open [conn (jdbc/get-connection ds)]
    (jdbc/with-transaction [tx conn]
      (f tx))))

(comment)