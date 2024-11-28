(ns product-watchlist.domain.migrations.component) (ns product-watchlist.domain.migrations.component
                                                     (:require [com.stuartsierra.component :as component]
                                                               [product-watchlist.domain.migrations.core :as migrations]))

(defrecord Migrations []
  component/Lifecycle

  (start [this]
    (println "Running database migrations...")
    (migrations/init)
    (migrations/migrate)
    (println "Migrations completed.")
    this)

  (stop [this]
    this))

(defn new-migrations []
  (map->Migrations {}))