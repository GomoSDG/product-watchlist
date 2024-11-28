-- :name find-product-by-id :? :1
-- :doc Get product by ID
SELECT * FROM products
WHERE id = :id;

-- :name save-product! :! :1
-- :doc Insert a new product
INSERT INTO products (
  id,
  upc,
  ean,
  mpn,
  title,
  brand,
  created_at,
  updated_at
) VALUES (
  :id,
  :upc,
  :ean,
  :mpn,
  :title,
  :brand,
  :created_at,
  :updated_at
);

-- :name find-retailer-product-by-id :? :1
-- :doc Get retailer product by ID
SELECT * FROM retailer_products
WHERE id = :id;

-- :name find-retailer-products-by-retailer :? :*
-- :doc Get all products for a retailer
SELECT rp.*, p.*
FROM retailer_products rp
JOIN products p ON p.id = rp.product_id
WHERE rp.retailer_id = :retailer_id;

-- :name save-retailer-product! :! :1
-- :doc Insert a new retailer product
INSERT INTO retailer_products (
  id,
  product_id,
  retailer_id,
  sku,
  retailer_code,
  price_amount,
  price_currency,
  url,
  created_at,
  updated_at
) VALUES (
  :id,
  :product_id,
  :retailer_id,
  :sku,
  :retailer_code,
  :price_amount,
  :price_currency,
  :url,
  :created_at,
  :updated_at
);


-- :name save-retailer-product-event! :! :1
-- :doc Record a product event
INSERT INTO retailer_product_events (
  id,
  retailer_product_id,
  event_type,
  data,
  created_at
) VALUES (
  :id,
  :retailer_product_id,
  :event_type,
  :data::jsonb,
  :created_at
);

-- :name find-retailer-product-events :? :*
-- :doc Get all events for a retailer product
SELECT id, retailer_product_id, event_type, data, created_at
FROM retailer_product_events
WHERE retailer_product_id = :retailer_product_id
ORDER BY created_at DESC;