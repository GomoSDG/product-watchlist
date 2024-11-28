CREATE TABLE products (
  id UUID PRIMARY KEY,
  upc TEXT,
  ean TEXT,
  mpn TEXT,
  brand TEXT,
  title TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

--;;

CREATE TABLE retailer_products (
  id UUID PRIMARY KEY,
  product_id UUID NOT NULL REFERENCES products(id),
  retailer_id UUID NOT NULL,
  sku TEXT NOT NULL,
  retailer_code TEXT NOT NULL,
  price_amount DECIMAL NOT NULL,
  price_currency VARCHAR(3) NOT NULL,
  url TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

--;;

CREATE INDEX idx_retailer_products_retailer_id ON retailer_products(retailer_id);
--;;
CREATE INDEX idx_retailer_products_sku ON retailer_products(sku);
--;;
CREATE INDEX idx_retailer_products_product_id ON retailer_products(product_id);
