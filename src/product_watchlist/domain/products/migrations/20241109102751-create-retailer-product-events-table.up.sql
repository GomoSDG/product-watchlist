
CREATE TABLE retailer_product_events (
  id UUID PRIMARY KEY,
  retailer_product_id UUID NOT NULL,
  type VARCHAR(255) NOT NULL,
  data JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL,
  FOREIGN KEY (retailer_product_id) REFERENCES retailer_products(id)
);

--;;

CREATE INDEX idx_retailer_product_events_retailer_product_id 
ON retailer_product_events(retailer_product_id);

--;;

CREATE INDEX idx_retailer_product_events_created_at 
ON retailer_product_events(created_at);