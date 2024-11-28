(ns product-watchlist.domain.migrations.core
  "Core migrations domain logic.
   Handles database migrations across all domains."
  (:require [migratus.core :as migratus]
            [product-watchlist.infrastructure.db :as db]
            [product-watchlist.infrastructure.resources :as resources]))

(def migration-registry (atom #{}))


(defn register-migrations!
  "Register a domain's migration directory"
  [migration-path]
  (swap! migration-registry conj migration-path))

(def config
  {:store :database
   :migration-dir @migration-registry
   :db db/db-spec})

(defn init
  "Initialize the migration infrastructure"
  []
  (migratus/init config))

(defn migrate
  "Run all pending migrations"
  []
  (migratus/migrate config))

(defn rollback
  "Rollback the last migration"
  []
  (migratus/rollback config))

(defn- get-domain-config [domain-name]
  (let [paths (resources/make-domain-paths domain-name)
        migrations-path (:migrations-path paths)]
    (assoc config :migration-dir migrations-path)))

(defn create
  "Create a new migration file for a specific domain"
  [domain-name name]
  (let [domain-config (get-domain-config domain-name)]
    (migratus/create domain-config name)))

(comment
  (get-domain-config "product"))