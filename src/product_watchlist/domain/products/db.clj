(ns product-watchlist.domain.products.db
  "Product persistence operations"
  (:require [product-watchlist.infrastructure.db :as db]
            [product-watchlist.domain.products.core :as product]
            [product-watchlist.domain.migrations.core :as migrations]
            [product-watchlist.infrastructure.resources :as resources]
            [hugsql.core :as hugsql]
            [hugsql.adapter.next-jdbc :as hugsql-adapter]
            [next.jdbc.result-set :as rs]))

(def domain-paths (resources/make-domain-paths))  ;; No argument needed anymore
(resources/validate-domain-paths! domain-paths)

(migrations/register-migrations! (:migrations-path domain-paths))

;; Forward declare HugSQL functions that will be defined later.
;; This is necessary because these functions are generated at runtime by def-db-fns,
;; but we want to reference them in our public API functions above their definition
;; to keep the code organized with public functions at the top.
(declare find-product-by-id
         find-products-by-retailer
         save-product!)

(hugsql/def-db-fns (:sql-path domain-paths)
  {:adapter (hugsql-adapter/hugsql-adapter-next-jdbc
             {:builder-fn rs/as-unqualified-kebab-maps})})

(defn find-by-id [id]
  (find-product-by-id db/ds {:id id}))

(defn find-by-retailer [retailer-id]
  (find-products-by-retailer db/ds {:retailer_id retailer-id}))

(defn save! [product]
  (when (product/valid-product? product)
    (save-product! db/ds product)))
