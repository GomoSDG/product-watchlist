(ns product-watchlist.infrastructure.resources
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- extract-domain [ns-name]
  (when-let [domain (second (re-find #"product-watchlist\.domain\.([^.]+)" (str ns-name)))]
    (str/replace domain "-" "_")))

(defn- current-domain []
  (or (extract-domain *ns*)
      (throw (ex-info "Cannot determine domain from namespace. Must be called from a domain namespace."
                      {:namespace *ns*}))))

(defn ensure-resource! [path]
  (when-not (io/resource path)
    (throw (ex-info (str "Required resource not found: " path)
                    {:path path}))))

(defn make-domain-paths
  ([] (make-domain-paths (current-domain)))
  ([domain-name]
   (let [base-path (str "product_watchlist/domain/" domain-name)
         migrations-path (str base-path "/migrations")
         sql-path (str base-path "/sql/" domain-name ".sql")]
     {:base-path base-path
      :migrations-path migrations-path
      :sql-path sql-path})))

(defn validate-domain-paths! [{:keys [migrations-path sql-path]}]
  (ensure-resource! migrations-path)
  (ensure-resource! sql-path))

(comment
  (io/resource "product_watchlist/domain/product/products.sql"))