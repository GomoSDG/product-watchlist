(ns product-watchlist.system
  (:require [com.stuartsierra.component :as component]
            [product-watchlist.domain.migrations.component :as migrations]))

(defn new-system [config]
  (component/system-map
   :migrations (migrations/new-migrations)
    ;; Add other components after migrations
    ;; so they start after DB is migrated
   ))