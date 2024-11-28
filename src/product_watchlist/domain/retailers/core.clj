(ns product-watchlist.domain.retailers.core
  "Core retailer domain logic and schemas"
  (:require [malli.core :as m]))

(def Retailer
  [:map
   [:id uuid?]
   [:name string?]
   [:status [:enum :active :inactive]]
   [:created-at inst?]
   [:updated-at inst?]
   [:description {:optional true} string?]])

(defn new-retailer [attrs]
  (merge
   {:id (random-uuid)
    :status :active
    :created-at (java.util.Date.)
    :updated-at (java.util.Date.)}
   attrs))

(defn valid-retailer? [retailer]
  (m/validate Retailer retailer))
