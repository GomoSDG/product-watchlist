(ns product-watchlist.domain.products.core
  "Core product domain logic and schemas"
  (:require [malli.core :as m]))

(def Product
  [:map
   [:id uuid?]
   [:ean string?]  ;; Made required for SA market
   [:title string?]
   [:brand [:maybe string?]]
   [:created-at inst?]
   [:updated-at inst?]])

(def RetailerProduct
  [:map
   [:id uuid?]
   [:product-id uuid?]
   [:retailer-id uuid?]
   [:sku string?]
   [:retailer-code string?]
   [:price [:map
            [:amount number?]
            [:currency keyword?]]]
   [:url [:maybe string?]]
   [:created-at inst?]
   [:updated-at inst?]])

(def RetailerProductEvent
  [:map
   [:id uuid?]
   [:retailer-product-id uuid?]
   [:type keyword?]
   [:data map?]
   [:created-at inst?]])

(defn new-retailer-product-event
  "Creates a new retailer product event.
   Args:
     retailer-product-id - UUID of the retailer product
     type - Keyword indicating the event type
     data - Map containing the event data
   Returns:
     A new event map with generated UUID and current timestamp"
  [retailer-product-id type data]
  {:id (random-uuid)
   :retailer-product-id retailer-product-id
   :type type
   :data data
   :created-at (java.util.Date.)})

(defmulti apply-retailer-product-event
  "Multimethod that applies events to a product based on event type.
   Dispatch fn: Takes a product and event, returns event type"
  (fn [_product event] (:type event)))

(defmethod apply-retailer-product-event :retailer-product/created
  [_ event]
  (:data event))

(defmethod apply-retailer-product-event :retailer-product/price-updated
  [product event]
  (assoc product
         :price (:price (:data event))
         :updated-at (:created-at event)))

(defmethod apply-retailer-product-event :retailer-product/url-updated
  [product event]
  (assoc product
         :url (:url (:data event))
         :updated-at (:created-at event)))

(defn reconstruct-retailer-product
  "Reconstructs a retailer product's current state from its event history.
   Args:
     events - Sequence of events to apply
   Returns:
     The current state of the retailer product"
  [events]
  (reduce apply-retailer-product-event nil events))

(defn new-product
  "Creates a new product with default values and merged attributes.
   Args:
     attrs - Map of product attributes to merge with defaults
   Returns:
     A new product map with generated UUID and timestamps"
  [attrs]
  (merge
   {:id (random-uuid)
    :created-at (java.util.Date.)
    :updated-at (java.util.Date.)}
   attrs))

(defn valid-product?
  "Validates a product against the Product schema.
   Args:
     product - Product map to validate
   Returns:
     Boolean indicating whether the product is valid"
  [product]
  (m/validate Product product))

(defn valid-retailer-product?
  "Validates a retailer product against the RetailerProduct schema.
   Args:
     retailer-product - RetailerProduct map to validate
   Returns:
     Boolean indicating whether the retailer product is valid"
  [retailer-product]
  (m/validate RetailerProduct retailer-product))

(defn new-retailer-product
  "Creates a new retailer product with default values and merged attributes.
   Args:
     attrs - Map of retailer product attributes to merge with defaults
   Returns:
     A new retailer product map with generated UUID and default values"
  [attrs]
  (merge
   {:id (random-uuid)
    :product-id nil      ; Required, must be provided
    :retailer-id nil     ; Required, must be provided  
    :sku ""             ; Required, must be provided
    :retailer-code ""   ; Required, must be provided
    :price {:amount (BigDecimal. "0.00")
            :currency :zar}
    :url nil
    :created-at (java.util.Date.)
    :updated-at (java.util.Date.)}
   attrs))

(comment
  (valid-product? {:id (random-uuid)
                   :ean "1234567890123"
                   :title "Test Product"
                   :brand "Test Brand"
                   :created-at (java.util.Date.)
                   :updated-at (java.util.Date.)})

  (valid-retailer-product? {:id (random-uuid)
                            :product-id (random-uuid)
                            :retailer-id (random-uuid)
                            :sku "1234567890"
                            :retailer-code "1234567890"
                            :price {:amount 123.45
                                    :currency :zar}
                            :url "https://example.com"
                            :created-at (java.util.Date.)
                            :updated-at (java.util.Date.)})

  ;; Example usage
  (valid-retailer-product?
   (new-retailer-product
    {:product-id (random-uuid)
     :retailer-id (random-uuid)
     :sku "SKU123"
     :retailer-code "RET123"
     :price {:amount (BigDecimal. "99.99")
             :currency :zar}
     :url "https://example.com"}))

  ;; Event sourcing examples
  (let [product-id (random-uuid)
        retailer-product-id (random-uuid)
        events [(new-retailer-product-event
                 retailer-product-id
                 :retailer-product/created
                 (new-retailer-product
                  {:id retailer-product-id
                   :product-id product-id
                   :retailer-id (random-uuid)
                   :sku "SKU123"
                   :retailer-code "RET123"
                   :price {:amount (BigDecimal. "99.99")
                           :currency :zar}}))
                (new-retailer-product-event
                 retailer-product-id
                 :retailer-product/price-updated
                 {:price {:amount (BigDecimal. "149.99")
                          :currency :zar}})]]
    (reconstruct-retailer-product events)))